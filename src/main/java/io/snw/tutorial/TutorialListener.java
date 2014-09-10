package io.snw.tutorial;

import io.snw.tutorial.api.EndTutorialEvent;
import io.snw.tutorial.api.StartTutorialEvent;
import io.snw.tutorial.api.ViewSwitchEvent;
import io.snw.tutorial.data.Caching;
import io.snw.tutorial.data.DataLoading;
import io.snw.tutorial.data.Getters;
import io.snw.tutorial.enums.MessageType;
import io.snw.tutorial.rewards.TutorialEco;
import io.snw.tutorial.rewards.TutorialExp;
import io.snw.tutorial.util.TutorialUtils;
import io.snw.tutorial.util.UUIDFetcher;
import java.util.HashMap;
import java.util.logging.Level;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

public class TutorialListener implements Listener {


    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private HashMap<String, TutorialExp> expTracker = new HashMap<String, TutorialExp>();
    private TutorialEco eco = new TutorialEco();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (event.getAction() != Action.PHYSICAL) {
            if (Getters.getGetters().isInTutorial(name)) {
                if (player.getItemInHand().getType() == Getters.getGetters().getCurrentTutorial(name).getItem()) {
                    if (Getters.getGetters().getCurrentTutorial(name).getTotalViews() == Getters.getGetters().getCurrentView(name)) {
                            plugin.getEndTutorial().endTutorial(player);
                        } else {
                            plugin.incrementCurrentView(name);
                            TutorialUtils.getTutorialUtils().textUtils(player);
                            player.teleport(Getters.getGetters().getTutorialView(name).getLocation());
                            if (Getters.getGetters().getTutorialView(name).getMessageType() == MessageType.TEXT) {
                                player.sendMessage(TutorialUtils.getTutorialUtils().tACC(Getters.getGetters().getTutorialView(name).getMessage()));
                            }
                        }
                }
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK && !Getters.getGetters().isInTutorial(name)) {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) block.getState();
                if (sign.getLine(0).equalsIgnoreCase(ChatColor.stripColor(Getters.getGetters().getConfigs().signSetting()))) {
                    if (sign.getLine(1) == null) return;
                    plugin.startTutorial(sign.getLine(1), player);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Getters.getGetters().isInTutorial(player.getName())) {
            player.teleport(Getters.getGetters().getTutorialView(player.getName()).getLocation());               
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            plugin.removeFromTutorial(event.getPlayer().getName());
        }
        if (!plugin.getServer().getOnlineMode()) {
            Caching.getCaching().getResponse().remove(player.getName());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(PlayerDropItemEvent event) {
        if (Getters.getGetters().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWhee(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (Getters.getGetters().isInTutorial(((Player) event.getEntity()).getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        final String playerName = player.getName();
        if (!plugin.getServer().getOnlineMode()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        Caching.getCaching().getResponse().put(playerName, UUIDFetcher.getUUIDOf(playerName));
                    } catch (Exception e) {
                        
                    }
                }
            });
        }
        for (String name : Getters.getGetters().getAllInTutorial()) {
            Player tut = plugin.getServer().getPlayerExact(name);
            tut.hidePlayer(player);
            player.hidePlayer(tut);
        }
        if (!player.hasPlayedBefore()) {
            if (Getters.getGetters().getConfigs().firstJoin()) {
               plugin.startTutorial(Getters.getGetters().getConfigs().firstJoinTutorial(), player);
            }
        }
    }

    @EventHandler
    public void onViewSwitch(ViewSwitchEvent event) {
        Player player = event.getPlayer();
        if(Getters.getGetters().getConfigs().getExpCountdown()) {
            player.setExp(player.getExp() - 1f);
        }
        if (Getters.getGetters().getConfigs().getRewards()) {
            if (!seenTutorial(player.getName(), event.getTutorial().getName())) {
                if (Getters.getGetters().getConfigs().getViewExp()) {
                    if(!Getters.getGetters().getConfigs().getExpCountdown()) {
                        player.setTotalExperience(player.getTotalExperience() + Getters.getGetters().getConfigs().getPerViewExp());
                    } else {
                        TutorialExp tutorialExp = new TutorialExp(player.getName().toLowerCase(), this.expTracker.get(player.getName()).getExp() + Getters.getGetters().getConfigs().getPerViewExp());
                        this.expTracker.remove(player.getName().toLowerCase());
                        this.expTracker.put(player.getName().toLowerCase(), tutorialExp);
                        player.sendMessage(ChatColor.BLUE + "You recieved " + Getters.getGetters().getConfigs().getViewExp() + " Exp you have: " + this.expTracker.get(player.getName().toLowerCase()).getExp());
                    }
                }
                if(Getters.getGetters().getConfigs().getViewMoney()) {
                    if(eco.setupEconomy()) {
                        EconomyResponse ecoResponse = eco.getEcon().depositPlayer((OfflinePlayer)player, Getters.getGetters().getConfigs().getPerViewMoney());
                        if(ecoResponse.transactionSuccess()) {
                            player.sendMessage(ChatColor.BLUE + "You recieved " + ecoResponse.amount + " New Balance: " + ecoResponse.balance);
                        } else {
                            plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTutorialEnd(EndTutorialEvent event) {
        if(Getters.getGetters().getConfigs().getRewards()) {
            Player player = event.getPlayer();
            String playerName = player.getName().toLowerCase();
            if (!seenTutorial(playerName, event.getTutorial().getName())) {
                if(eco.setupEconomy()) {
                    if(Getters.getGetters().getConfigs().getTutorialMoney()) {
                        EconomyResponse ecoResponse = eco.getEcon().depositPlayer((OfflinePlayer) player, Getters.getGetters().getConfigs().getPerTutorialMoney());
                        if(ecoResponse.transactionSuccess()) {
                            player.sendMessage(ChatColor.BLUE + "You recieved " + ecoResponse.amount + ". New Balance: " + ecoResponse.balance);
                        } else {
                            plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: {0}", player.getName());
                        }
                    }
                    if(Getters.getGetters().getConfigs().getTutorialExp() || Getters.getGetters().getConfigs().getTutorialMoney()) {
                        if(Getters.getGetters().getConfigs().getExpCountdown()) {
                            player.setTotalExperience(0);
                            player.setTotalExperience(Getters.getGetters().getConfigs().getPerTutorialExp() + this.expTracker.get(playerName).getExp());
                        } else {
                            player.setExp(Getters.getGetters().getConfigs().getPerTutorialExp() + player.getTotalExperience());
                        }
                    }
                    this.expTracker.remove(playerName);
                }
            } else {
                player.sendMessage(ChatColor.BLUE + "You have been through this tutorial already. You will not collect rewards!");
            }
        }
        DataLoading.getDataLoading().getPlayerData().set("players." + Caching.getCaching().getUUID(event.getPlayer()) + ".tutorials." + event.getTutorial().getName(), "true");
        DataLoading.getDataLoading().savePlayerData();
        Caching.getCaching().reCachePlayerData();
    }

    @EventHandler
    public void onTutorialStart(StartTutorialEvent event) {
        Player player = event.getPlayer();
        if(Getters.getGetters().getConfigs().getExpCountdown()) {
            TutorialExp tutorialExp = new TutorialExp(player.getName().toLowerCase(), player.getTotalExperience());
            this.expTracker.put(player.getName(), tutorialExp);
            int expCounter = event.getTutorial().getTotalViews();
            player.setTotalExperience(expCounter);
        }
    }

    public boolean seenTutorial(String name, String tutorial) {
        if (Getters.getGetters().getPlayerData().containsKey(name)) {
            if (Getters.getGetters().getPlayerData(name).getPlayerTutorialData().containsKey(tutorial)) {
                return Getters.getGetters().getPlayerData(name).getPlayerTutorialData().get(tutorial).getSeen();
            }
        }
        return false;
    }
}
