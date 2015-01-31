package io.snw.tutorial;

import io.snw.tutorial.api.EndTutorialEvent;
import io.snw.tutorial.data.Caching;
import io.snw.tutorial.data.Getters;
import io.snw.tutorial.enums.CommandType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        player.setGameMode(Caching.getCaching().getGameMode(player.getUniqueId()));
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

        plugin.getServer().getPluginManager().callEvent(event);

        String command = tutorial.getCommand();
        CommandType type = tutorial.getCommandType();
        if (type == CommandType.NONE || command == null || command.isEmpty()) {
            return;
        }
        if (command.startsWith("/")) {
            command = command.replaceFirst("/", "");
        }
        command = command.replace("%player%", player.getName());

        switch (type) {
            case PLAYER:
                Bukkit.dispatchCommand(player, command);
                break;
            case SUDO:
                boolean shouldBeOp = !player.isOp();
                if (shouldBeOp) {
                    player.setOp(true);
                }
                Bukkit.dispatchCommand(player, command);
                if (shouldBeOp) {
                    player.setOp(false);
                }
                break;
            case CONSOLE:
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                break;
        }
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
        player.setGameMode(Caching.getCaching().getGameMode(player.getUniqueId()));
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
