package io.snw.tutorial;

import io.snw.tutorial.rewards.TutorialExp;
import io.snw.tutorial.rewards.TutorialEco;
import io.snw.tutorial.api.EndTutorialEvent;
import io.snw.tutorial.api.StartTutorialEvent;
import io.snw.tutorial.api.ViewSwitchEvent;
import io.snw.tutorial.data.Caching;
import io.snw.tutorial.data.Getters;
import io.snw.tutorial.data.Setters;
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
    private Getters getters = new Getters(plugin);
    private Setters setters = new Setters(plugin);
    private Caching cache = new Caching(plugin);


    public TutorialListener(ServerTutorial plugin) {
        this.plugin = plugin;
    }
    private PluginManager pm;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (getters.isInTutorial(name)) {
            if (getters.getCurrentTutorial(name).getViewType() == ViewType.CLICK) {
                if (event.getAction() != Action.PHYSICAL) {
                    if (player.getItemInHand().getType() == getters.getCurrentTutorial(name).getItem()) {
                        if (getters.getCurrentTutorial(name).getTotalViews() == getters.getCurrentView(name)) {
                            plugin.getEndTutorial().endTutorial(player);
                        } else {
                            plugin.incrementCurrentView(name);
                            plugin.getTutorialUtils().textUtils(player);
                            player.teleport(getters.getTutorialView(name).getLocation());
                            if (getters.getTutorialView(name).getMessageType() == MessageType.TEXT) {
                                player.sendMessage(plugin.getTutorialUtils().tACC(getters.getTutorialView(name).getMessage()));
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
        if (getters.isInTutorial(player.getName())) {
            player.teleport(getters.getTutorialView(player.getName()).getLocation());               
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (getters.isInTutorial(event.getPlayer().getName())) {
            plugin.removeFromTutorial(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (getters.isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (getters.isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (getters.isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(PlayerDropItemEvent event) {
        if (getters.isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWhee(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (getters.isInTutorial(((Player) event.getEntity()).getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (String name : getters.getAllInTutorial()) {
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
        if(getters.getConfigs().getExpCountdown()) {
            player.setExp(player.getExp() - 1f);
        }
        if (getters.getConfigs().getRewards()) {
            if (getters.getConfigs().getPerViewExp() || getters.getConfigs().getPerViewMoney()) {
                if(!getters.getConfigs().getExpCountdown()) {
                    player.setExp(player.getExp() + getters.getConfigs().getViewExp());
                }
                if(eco.setupEconomy()) {
                    EconomyResponse ecoResponse = eco.getEcon().depositPlayer(player.getName(), getters.getConfigs().getViewMoney());
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
        if(getters.getConfigs().getRewards()) {
            Player player = event.getPlayer();
            String playerName = player.getName().toLowerCase();
            if(eco.setupEconomy()) {
                if(getters.getConfigs().getPerTutorialMoney()) {
                    EconomyResponse ecoResponse = eco.getEcon().depositPlayer(player.getName(), getters.getConfigs().getTutorialMoney());
                    if(ecoResponse.transactionSuccess()) {
                        player.sendMessage(ChatColor.BLUE + "You recieved " + ecoResponse.amount + ". New Balance: " + ecoResponse.balance);
                    } else {
                        plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                    }
                }
            }
            
            if(getters.getConfigs().getPerTutorialExp()) {
                if(getters.getConfigs().getExpCountdown()) {
                    if(getters.getConfigs().getPerViewExp()) {
                        player.setExp(event.getTutorial().getTotalViews() * getters.getConfigs().getViewExp() + getters.getConfigs().getTutorialExp() + this.expTracker.get(playerName).getExp());
                        this.expTracker.remove(playerName);
                    } else {
                        player.setExp(getters.getConfigs().getTutorialExp() + this.expTracker.get(playerName).getExp());
                        this.expTracker.remove(playerName);
                    }
                } else {
                    player.setExp(getters.getConfigs().getTutorialExp() + player.getExp());
                }
            }
        }                
    }
    
    @EventHandler
    public void onTutorialStart(StartTutorialEvent event) {
        Player player = event.getPlayer();
        if(getters.getConfigs().getExpCountdown()) {
            float expCounter = event.getTutorial().getTotalViews();
            player.setExp(expCounter);
            TutorialExp tutorialExp = new TutorialExp(player.getName().toLowerCase(), player.getExp());
            this.expTracker.put(player.getName(), tutorialExp);            
        }
    }
}
