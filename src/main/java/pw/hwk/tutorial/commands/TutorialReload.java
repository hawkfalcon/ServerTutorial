package pw.hwk.tutorial.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.ServerTutorial;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.TutorialManager;
import pw.hwk.tutorial.enums.Permissions;

public class TutorialReload implements CommandExecutor {

    private static ServerTutorial plugin = ServerTutorial.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!Permissions.RELOAD.hasPerm(sender)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
            return true;
        }
        for (String playerName : TutorialManager.getManager().getAllInTutorial()) {
            Player player = plugin.getServer().getPlayer(playerName);
            plugin.getEndTutorial().reloadEndTutorial(player);
        }
        Caching.getCaching().reCasheTutorials();
        Caching.getCaching().reCacheConfigs();
        Caching.getCaching().reCachePlayerData();
        Caching.getCaching().reCacheTempPlayerData();
        sender.sendMessage(ChatColor.RED + "Server Tutorial Reloaded!");
        return true;
    }
}
