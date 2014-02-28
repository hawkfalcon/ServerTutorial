package io.snw.tutorial;

import io.snw.tutorial.api.EndTutorialEvent;
import io.snw.tutorial.api.ViewSwitchEvent;
import io.snw.tutorial.enums.MessageType;
import io.snw.tutorial.enums.ViewType;
import java.util.logging.Level;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

public class TutorialListener implements Listener {


    ServerTutorial plugin;
    private TutorialEco eco = new TutorialEco(plugin);


    public TutorialListener(ServerTutorial plugin) {
        this.plugin = plugin;
    }
    private PluginManager pm;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (plugin.isInTutorial(name)) {
            if (plugin.getCurrentTutorial(name).getViewType() == ViewType.CLICK) {
                if (event.getAction() != Action.PHYSICAL) {
                    if (player.getItemInHand().getType() == plugin.getCurrentTutorial(name).getItem()) {
                        if (plugin.getCurrentTutorial(name).getTotalViews() == plugin.getCurrentView(name)) {
                            plugin.getEndTutorial().endTutorial(player);
                        } else {
                            plugin.incrementCurrentView(name);
                            plugin.getTutorialUtils().textUtils(player);
                            player.teleport(plugin.getTutorialView(name).getLocation());
                            if (plugin.getTutorialView(name).getMessageType() == MessageType.TEXT) {
                                player.sendMessage(plugin.getTutorialUtils().tACC(plugin.getTutorialView(name).getMessage()));
                            }
                        }
                    }
                }
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) block.getState();
                if (sign.getLine(0).equalsIgnoreCase(ChatColor.stripColor(plugin.getConfig().getString("sign")))) {
                    if (sign.getLine(1) == null) return;
                    plugin.startTutorial(sign.getLine(1), player);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.isInTutorial(player.getName())) {
            player.teleport(plugin.getTutorialView(player.getName()).getLocation());               
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.isInTutorial(event.getPlayer().getName())) {
            plugin.removeFromTutorial(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (plugin.isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (plugin.isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(PlayerDropItemEvent event) {
        if (plugin.isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWhee(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (plugin.isInTutorial(((Player) event.getEntity()).getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (String name : plugin.getAllInTutorial()) {
            Player tut = plugin.getServer().getPlayerExact(name);
            tut.hidePlayer(player);
            player.hidePlayer(tut);
        }
        if (!player.hasPlayedBefore()) {
            if (plugin.getConfig().getBoolean("first_join")) {
               plugin.startTutorial(plugin.getConfig().getString("first_join_tutorial"), player);
            }
        }
    }
    
    @EventHandler
    public void onViewSwitch(ViewSwitchEvent event) {
        if(eco.setupEconomy() && plugin.getConfig().getBoolean("rewards")) {
            Player player = event.getPlayer();
            if(plugin.getConfig().getBoolean("per_view_money")) {
                EconomyResponse ecoResponse = eco.getEcon().depositPlayer(player.getName(), plugin.getConfig().getDouble("view_money"));
                if(ecoResponse.transactionSuccess()) {
                    player.sendMessage(ChatColor.BLUE + "You recieved " + ecoResponse.amount + ". New Balance: " + ecoResponse.balance);
                } else {
                    plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                }
            }
            
            if(plugin.getConfig().getBoolean("per_view_exp")) {
                float exp = plugin.getConfig().getInt("view_exp_give");
                float currentExp = player.getExp();
                player.setExp(exp + currentExp);
                player.sendMessage(ChatColor.BLUE + "You recieved " + exp + " experience. You now have " + player.getExp() + " experience.");
            }            
        }
    }
    
    @EventHandler
    public void onTutorialEnd(EndTutorialEvent event) {
        if(eco.setupEconomy() && plugin.getConfig().getBoolean("rewards")) {
            Player player = event.getPlayer();
            if(plugin.getConfig().getBoolean("per_tutorial_money")) {
                EconomyResponse ecoResponse = eco.getEcon().depositPlayer(player.getName(), plugin.getConfig().getDouble("tutorial_money"));
                if(ecoResponse.transactionSuccess()) {
                    player.sendMessage(ChatColor.BLUE + "You recieved " + ecoResponse.amount + ". New Balance: " + ecoResponse.balance);
                } else {
                    plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                }
            }
            
            if(plugin.getConfig().getBoolean("per_tutorial_exp")) {
                float exp = plugin.getConfig().getInt("tutorial_exp_give");
                float currentExp = player.getExp();
                player.setExp(exp + currentExp);
                player.sendMessage(ChatColor.BLUE + "You recieved " + exp + " experience. You now have " + player.getExp() + " experience.");
            }            
        }                
    }
}
