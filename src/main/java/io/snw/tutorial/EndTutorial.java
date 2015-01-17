package io.snw.tutorial;

import io.snw.tutorial.api.EndTutorialEvent;
import io.snw.tutorial.data.Caching;
import io.snw.tutorial.data.Getters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EndTutorial {


    private ServerTutorial plugin;

    public EndTutorial(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void endTutorial(final Player player) {
        final String name = plugin.getServer().getPlayer(player.getName()).getName();
        Tutorial tutorial = Getters.getGetters().getCurrentTutorial(name);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', tutorial.getEndMessage()));
        player.closeInventory();
        player.getInventory().clear();
        player.setAllowFlight(plugin.getFlight(name));
        player.setFlying(false);
        plugin.removeFlight(name);
        Caching.getCaching().setTeleport(player.getUniqueId(), true);
        //Todo: Cache players gamemode
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(plugin.getFirstLoc(name));
        plugin.cleanFirstLoc(name);
        plugin.removeFromTutorial(name);
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player online : plugin.getServer().getOnlinePlayers()) {
                    online.showPlayer(player);
                }
                player.getInventory().setContents(plugin.getInventory(name));
                plugin.cleanInventory(name);
            }
        }.runTaskLater(plugin, 20L);
        EndTutorialEvent event = new EndTutorialEvent(player, tutorial);
        
        //Todo
        plugin.getServer().getPluginManager().callEvent(event);
        Bukkit.getServer().getPluginManager().callEvent(event);

        String command = tutorial.getCommand();
        if (command == null ||command.isEmpty()) return;
        if (command.startsWith("/")) command = command.replaceFirst("/", "");
        command = command.replace("%player%", player.getName());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public void reloadEndTutorial(final Player player) {
        final String name = plugin.getServer().getPlayer(player.getUniqueId()).getName();
        Tutorial tutorial = Getters.getGetters().getCurrentTutorial(name);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', tutorial.getEndMessage()));
        player.closeInventory();
        player.getInventory().clear();
        player.setAllowFlight(plugin.getFlight(name));
        player.setFlying(false);
        plugin.removeFlight(name);
        Caching.getCaching().setTeleport(player.getUniqueId(), true);
        player.teleport(plugin.getFirstLoc(name));
        //Todo: Cache players gamemode
        player.setGameMode(GameMode.SURVIVAL);
        plugin.cleanFirstLoc(name);
        plugin.removeFromTutorial(name);
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player online : plugin.getServer().getOnlinePlayers()) {
                    online.showPlayer(player);
                }
                player.getInventory().setContents(plugin.getInventory(name));
                plugin.cleanInventory(name);
            }
        }.runTaskLater(plugin, 20L);
    }
}
