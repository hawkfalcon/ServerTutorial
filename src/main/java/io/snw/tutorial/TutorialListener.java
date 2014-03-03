package io.snw.tutorial;

import io.snw.tutorial.api.EndTutorialEvent;
import io.snw.tutorial.api.StartTutorialEvent;
import io.snw.tutorial.api.ViewSwitchEvent;
import io.snw.tutorial.enums.MessageType;
import io.snw.tutorial.enums.ViewType;
import java.util.HashMap;
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
    private HashMap<String, TutorialExp> expTracker = new HashMap<String, TutorialExp>();


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
        Player player = event.getPlayer();
        if(plugin.getConfigs().getExpCountdown()) {
            player.setExp(player.getExp() - 1f);
        }
        if (plugin.getConfigs().getRewards()) {
            if (plugin.getConfigs().getPerViewExp() || plugin.getConfigs().getPerViewMoney()) {
                if(!plugin.getConfigs().getExpCountdown()) {
                    player.setExp(player.getExp() + plugin.getConfigs().getViewExp());
                }
                if(eco.setupEconomy()) {
                    EconomyResponse ecoResponse = eco.getEcon().depositPlayer(player.getName(), plugin.getConfigs().getViewMoney());
                    if(ecoResponse.transactionSuccess()) {
                        player.sendMessage(ChatColor.BLUE + "You recieved " + ecoResponse.amount + ". New Balance: " + ecoResponse.balance);
                    } else {
                        plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                    }
                }                
            }
        }
    }
    
    @EventHandler
    public void onTutorialEnd(EndTutorialEvent event) {
        if(plugin.getConfigs().getRewards()) {
            Player player = event.getPlayer();
            String playerName = player.getName().toLowerCase();
            if(eco.setupEconomy()) {
                if(plugin.getConfigs().getPerTutorialMoney()) {
                    EconomyResponse ecoResponse = eco.getEcon().depositPlayer(player.getName(), plugin.getConfigs().getTutorialMoney());
                    if(ecoResponse.transactionSuccess()) {
                        player.sendMessage(ChatColor.BLUE + "You recieved " + ecoResponse.amount + ". New Balance: " + ecoResponse.balance);
                    } else {
                        plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                    }
                }
            }
            
            if(plugin.getConfigs().getPerTutorialExp()) {
                if(plugin.getConfigs().getExpCountdown()) {
                    if(plugin.getConfigs().getPerViewExp()) {
                        player.setExp(event.getTutorial().getTotalViews() * plugin.getConfigs().getViewExp() + plugin.getConfigs().getTutorialExp() + this.expTracker.get(playerName).getExp());
                        this.expTracker.remove(playerName);
                    } else {
                        player.setExp(plugin.getConfigs().getTutorialExp() + this.expTracker.get(playerName).getExp());
                        this.expTracker.remove(playerName);
                    }
                } else {
                    player.setExp(plugin.getConfigs().getTutorialExp() + player.getExp());
                }
            }
        }                
    }
    
    @EventHandler
    public void onTutorialStart(StartTutorialEvent event) {
        Player player = event.getPlayer();
        if(plugin.getConfigs().getExpCountdown()) {
            float expCounter = event.getTutorial().getTotalViews();
            player.setExp(expCounter);
            TutorialExp tutorialExp = new TutorialExp(player.getName().toLowerCase(), player.getExp());
            this.expTracker.put(player.getName(), tutorialExp);            
        }
    }
}
