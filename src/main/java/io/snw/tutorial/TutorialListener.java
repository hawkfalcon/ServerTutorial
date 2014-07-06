package io.snw.tutorial;

import io.snw.tutorial.rewards.TutorialExp;
import io.snw.tutorial.rewards.TutorialEco;
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
    private TutorialEco eco;
    private HashMap<String, TutorialExp> expTracker = new HashMap<String, TutorialExp>();


    public TutorialListener(ServerTutorial plugin) {
        this.plugin = plugin;
    }
    private PluginManager pm;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (plugin.getters().isInTutorial(name)) {
            if (plugin.getters().getCurrentTutorial(name).getViewType() == ViewType.CLICK) {
                if (event.getAction() != Action.PHYSICAL) {
                    if (player.getItemInHand().getType() == plugin.getters().getCurrentTutorial(name).getItem()) {
                        if (plugin.getters().getCurrentTutorial(name).getTotalViews() == plugin.getters().getCurrentView(name)) {
                            plugin.getEndTutorial().endTutorial(player);
                        } else {
                            plugin.incrementCurrentView(name);
                            plugin.getTutorialUtils().textUtils(player);
                            player.teleport(plugin.getters().getTutorialView(name).getLocation());
                            if (plugin.getters().getTutorialView(name).getMessageType() == MessageType.TEXT) {
                                player.sendMessage(plugin.getTutorialUtils().tACC(plugin.getters().getTutorialView(name).getMessage()));
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
                if (sign.getLine(0).equalsIgnoreCase(ChatColor.stripColor(plugin.getters().getConfigs().signSetting()))) {
                    if (sign.getLine(1) == null) return;
                    if(plugin.getters().isInTutorial(name)) return;
                    plugin.startTutorial(sign.getLine(1), player);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getters().isInTutorial(player.getName())) {
            player.teleport(plugin.getters().getTutorialView(player.getName()).getLocation());               
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getters().isInTutorial(event.getPlayer().getName())) {
            plugin.removeFromTutorial(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (plugin.getters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.getters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (plugin.getters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(PlayerDropItemEvent event) {
        if (plugin.getters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWhee(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (plugin.getters().isInTutorial(((Player) event.getEntity()).getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (String name : plugin.getters().getAllInTutorial()) {
            Player tut = plugin.getServer().getPlayerExact(name);
            tut.hidePlayer(player);
            player.hidePlayer(tut);
        }
        if (!player.hasPlayedBefore()) {
            if (plugin.getters().getConfigs().firstJoin()) {
               plugin.startTutorial(plugin.getters().getConfigs().firstJoinTutorial(), player);
            }
        }
    }

    @EventHandler
    public void onViewSwitch(ViewSwitchEvent event) {
        Player player = event.getPlayer();
        if(plugin.getters().getConfigs().getExpCountdown()) {
            player.setExp(player.getExp() - 1f);
        }
        if (plugin.getters().getConfigs().getRewards()) {
            if (plugin.getters().getConfigs().getPerViewExp()) {
                if(!plugin.getters().getConfigs().getExpCountdown()) {
                    player.setExp(player.getExp() + plugin.getters().getConfigs().getViewExp());
                } else {
                    TutorialExp tutorialExp = new TutorialExp(player.getName().toLowerCase(), this.expTracker.get(player.getName()).getExp() + plugin.getters().getConfigs().getViewExp());
                    this.expTracker.put(player.getName(), tutorialExp);
                    player.sendMessage(ChatColor.BLUE + "You recieved " + plugin.getters().getConfigs().getViewExp() + " Exp you have: " + this.expTracker.get(player.getName()).getExp());
                }
            }
            if(plugin.getters().getConfigs().getPerViewMoney()) {
                if(this.eco.setupEconomy()) {
                    EconomyResponse ecoResponse = this.eco.getEcon().bankDeposit(player.getName(), plugin.getters().getConfigs().getViewMoney());
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
        if(plugin.getters().getConfigs().getRewards()) {
            Player player = event.getPlayer();
            String playerName = player.getName().toLowerCase();
            
            if(this.eco.setupEconomy()) {
                if(plugin.getters().getConfigs().getPerTutorialMoney()) {
                    EconomyResponse ecoResponse = this.eco.getEcon().bankDeposit(player.getName(), plugin.getters().getConfigs().getTutorialMoney());
                    if(ecoResponse.transactionSuccess()) {
                        player.sendMessage(ChatColor.BLUE + "You recieved " + ecoResponse.amount + ". New Balance: " + ecoResponse.balance);
                    } else {
                        plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                    }
                }
            }
            if(plugin.getters().getConfigs().getPerTutorialExp() || plugin.getters().getConfigs().getPerTutorialMoney()) {
                if(plugin.getters().getConfigs().getExpCountdown()) {
                    player.setExp(plugin.getters().getConfigs().getTutorialExp() + this.expTracker.get(playerName).getExp());
                } else {
                    player.setExp(plugin.getters().getConfigs().getTutorialExp() + player.getExp());
                }
            }
            this.expTracker.remove(playerName);
        }
    }

    @EventHandler
    public void onTutorialStart(StartTutorialEvent event) {
        Player player = event.getPlayer();
        if(plugin.getters().getConfigs().getExpCountdown()) {
            TutorialExp tutorialExp = new TutorialExp(player.getName().toLowerCase(), player.getExp());
            this.expTracker.put(player.getName(), tutorialExp);
            float expCounter = event.getTutorial().getTotalViews();
            player.setExp(expCounter);
        }
    }
}
