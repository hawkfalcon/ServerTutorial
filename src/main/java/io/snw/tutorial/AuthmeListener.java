package io.snw.tutorial;

import fr.xephi.authme.events.LoginEvent;
import io.snw.tutorial.data.Caching;
import io.snw.tutorial.data.Getters;
import io.snw.tutorial.util.UUIDFetcher;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class AuthmeListener implements Listener {
    private final ServerTutorial plugin = ServerTutorial.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(LoginEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
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
        for (String name : Getters.getGetters().getAllInTutorial()) {
            Player tut = plugin.getServer().getPlayerExact(name);
            if (tut != null) {
                player.hidePlayer(tut);
            }
        }
        if (!player.hasPlayedBefore()) {
            if (Getters.getGetters().getConfigs().firstJoin()) {
                plugin.startTutorial(Getters.getGetters().getConfigs().firstJoinTutorial(), player);
            }
        }

        BukkitRunnable runnable = TutorialListener.getRestoreQueue().get(player.getUniqueId());
        if(runnable != null) {
            runnable.runTaskLater(plugin, 20L);
        }
        TutorialListener.getRestoreQueue().remove(player.getUniqueId());
    }
}
