package pw.hwk.tutorial;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import pw.hwk.tutorial.api.EndTutorialEvent;
import pw.hwk.tutorial.api.ViewSwitchEvent;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.DataLoading;
import pw.hwk.tutorial.data.TutorialManager;
import pw.hwk.tutorial.data.TutorialPlayer;
import pw.hwk.tutorial.enums.ViewType;
import pw.hwk.tutorial.rewards.TutorialEco;
import pw.hwk.tutorial.util.TutorialUtils;
import pw.hwk.tutorial.util.UUIDFetcher;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.GameMode;

public class TutorialListener implements Listener {

    private static ServerTutorial plugin = ServerTutorial.getInstance();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (event.getAction() != Action.PHYSICAL) {
            if (TutorialManager.getManager().isInTutorial(name) && TutorialManager.getManager().getCurrentTutorial
                    (name).getViewType() != ViewType.TIME) {
                if (TutorialManager.getManager().getCurrentTutorial(name).getTotalViews() == TutorialManager
                        .getManager().getCurrentView(name)) {
                    plugin.getEndTutorial().endTutorial(player);
                } else {
                    plugin.incrementCurrentView(name);
                    TutorialUtils.getTutorialUtils().messageUtils(player);
                    Caching.getCaching().setTeleport(player, true);
                    player.teleport(TutorialManager.getManager().getTutorialView(name).getLocation());
                }
            }
        }
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) &&
                !TutorialManager.getManager().isInTutorial(name)) {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) block.getState();
                String match = ChatColor.stripColor(TutorialUtils.color(TutorialManager.getManager().getConfigs()
                        .signSetting()));
                if (sign.getLine(0).equalsIgnoreCase(match) && sign.getLine(1) != null) {
                    plugin.startTutorial(sign.getLine(1), player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (TutorialManager.getManager().isInTutorial(p.getName())) {
            event.setCancelled(true);
            return;
        }

        HashSet<Player> set = new HashSet<Player>(event.getRecipients());
        for (Player setPlayer : set) {
            if (setPlayer == null) {
                continue;
            }

            if (TutorialManager.getManager().isInTutorial(setPlayer.getName())) {
                event.getRecipients().remove(setPlayer);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!TutorialManager.getManager().isInTutorial(player.getName())) {
            return;
        }

        if (Caching.getCaching().canTeleport(player)) {
            Caching.getCaching().setTeleport(player, false);
        } else {
            event.setCancelled(true);
        }

        if (TutorialManager.getManager().isInTutorial(player.getName())) {
            if(event.getFrom().getWorld() != event.getTo().getWorld()) {
                GameMode gameMode = TutorialManager.getManager().getCurrentTutorial(player.getName()).getGameMode();
                player.setGameMode(gameMode);
            }
        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (TutorialManager.getManager().isInTutorial(player.getName())) {
            Caching.getCaching().setTeleport(player, true);
            player.teleport(TutorialManager.getManager().getTutorialView(player.getName()).getLocation());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (TutorialManager.getManager().isInTutorial(event.getPlayer().getName())) {
            player.closeInventory();
            Caching.getCaching().setTeleport(player, true);

            TutorialPlayer tutorialPlayer = plugin.getTutorialPlayer(player.getUniqueId());
            tutorialPlayer.restorePlayer(player);

            plugin.removeTutorialPlayer(player);
            plugin.removeFromTutorial(event.getPlayer().getName());
        }
        if (!plugin.getServer().getOnlineMode()) {
            try {
                Caching.getCaching().getResponse().remove(player.getName());
            } catch (Exception ignored) {

            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (TutorialManager.getManager().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (TutorialManager.getManager().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (TutorialManager.getManager().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(PlayerDropItemEvent event) {
        if (TutorialManager.getManager().isInTutorial(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWhee(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (TutorialManager.getManager().isInTutorial(event.getEntity().getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
        if (TutorialManager.getManager().getConfigs().getCheckGameMode()) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
        if (!plugin.getServer().getOnlineMode()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        Caching.getCaching().getResponse().put(playerName, UUIDFetcher.getUUIDOf(playerName));
                    } catch (Exception ignored) {

                    }
                }
            });
        }
        for (String name : TutorialManager.getManager().getAllInTutorial()) {
            Player tut = plugin.getServer().getPlayerExact(name);
            if (tut != null) {
                player.hidePlayer(tut);
            }
        }
        if (!player.hasPlayedBefore()) {
            if (TutorialManager.getManager().getConfigs().firstJoin()) {
                plugin.startTutorial(TutorialManager.getManager().getConfigs().firstJoinTutorial(), player);
            }
        }
    }

    @EventHandler
    public void onViewSwitch(ViewSwitchEvent event) {
        Player player = event.getPlayer();
        if (TutorialManager.getManager().getConfigs().getExpCountdown()) {
            player.setExp(player.getExp() - 1f);
        }
        if (TutorialManager.getManager().getConfigs().getRewards()) {
            UUID uuid = Caching.getCaching().getUUID(player);
            if (!seenTutorial(uuid, event.getTutorial().getName())) {
                if (TutorialManager.getManager().getConfigs().getViewExp()) {
                    player.setTotalExperience(player.getTotalExperience() + TutorialManager.getManager().getConfigs()
                            .getPerViewExp());
                    player.sendMessage(ChatColor.BLUE + "You received " + TutorialManager.getManager().getConfigs()
                            .getViewExp());
                }
                if (TutorialManager.getManager().getConfigs().getViewMoney()) {
                    if (TutorialEco.getTutorialEco().setupEconomy()) {
                        EconomyResponse ecoResponse = TutorialEco.getTutorialEco().getEcon().depositPlayer(player,
                                TutorialManager.getManager().getConfigs().getPerViewMoney());
                        if (ecoResponse.transactionSuccess()) {
                            player.sendMessage(ChatColor.BLUE + "You received " + ecoResponse.amount + " New Balance:" +
                                    " " + ecoResponse.balance);
                        } else {
                            plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: " +
                                    "{0}", player.getName());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTutorialEnd(EndTutorialEvent event) {
        Player player = event.getPlayer();
        UUID uuid = Caching.getCaching().getUUID(player);
        if (TutorialManager.getManager().getConfigs().getRewards()) {
            if (!seenTutorial(uuid, event.getTutorial().getName())) {
                if (TutorialEco.getTutorialEco().setupEconomy() && TutorialManager.getManager().getConfigs()
                        .getTutorialMoney()) {
                    EconomyResponse ecoResponse = TutorialEco.getTutorialEco().getEcon().depositPlayer(player,
                            TutorialManager.getManager().getConfigs().getPerTutorialMoney());
                    if (ecoResponse.transactionSuccess()) {
                        player.sendMessage(ChatColor.BLUE + "You received " + ecoResponse.amount + " for completing " +
                                "the tutorial!");
                    } else {
                        plugin.getLogger().log(Level.WARNING, "There was an error processing Economy for player: " +
                                "{0}", player.getName());
                    }
                }
                if (TutorialManager.getManager().getConfigs().getTutorialExp()) {
                    player.setExp(player.getTotalExperience() + TutorialManager.getManager().getConfigs()
                            .getPerTutorialExp());
                }
            }
        }
        DataLoading.getDataLoading().getPlayerData().set("players." + uuid + ".tutorials." + event.getTutorial()
                .getName(), "true");
        DataLoading.getDataLoading().savePlayerData();
        Caching.getCaching().reCachePlayerData();
    }

    private boolean seenTutorial(UUID uuid, String tutorial) {
        Set<String> seenTutorials = TutorialManager.getManager().getSeenTutorials(uuid);
        if (seenTutorials != null) {
            if (seenTutorials.contains(tutorial)) {
                return true;
            }
        }
        return false;
    }
}
