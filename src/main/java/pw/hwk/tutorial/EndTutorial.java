package pw.hwk.tutorial;

import pw.hwk.tutorial.api.EndTutorialEvent;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.Getters;
import pw.hwk.tutorial.enums.CommandType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pw.hwk.tutorial.util.TutorialUtils;

public class EndTutorial {


    private ServerTutorial plugin;

    public EndTutorial(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void endTutorial(Player player) {
        String name = plugin.getServer().getPlayer(player.getUniqueId()).getName();
        Tutorial tutorial = Getters.getGetters().getCurrentTutorial(name);
        endTutorialPlayer(player, name, tutorial.getEndMessage());
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
            case CONSOLE:
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                break;
        }
    }

    public void endTutorialPlayer(final Player player, final String name, String endMessage) {
        player.sendMessage(TutorialUtils.color(endMessage));
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

    public void reloadEndTutorial(Player player) {
        String name = plugin.getServer().getPlayer(player.getUniqueId()).getName();
        Tutorial tutorial = Getters.getGetters().getCurrentTutorial(name);
        endTutorialPlayer(player, name, tutorial.getEndMessage());
    }
}
