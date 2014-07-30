
package io.snw.tutorial.commands;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.api.TutorialReloadEvent;
import io.snw.tutorial.enums.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TutorialReload implements CommandExecutor {

    private ServerTutorial plugin;

    public TutorialReload(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(Permissions.RELOAD.hasPerm(sender)) {
            for(String playerName : plugin.getters().getAllInTutorial()) {
                Player player = plugin.getServer().getPlayer(playerName);
                plugin.getEndTutorial().reloadEndTutorial(player);
            }
            plugin.caching().reCasheTutorials();
            plugin.caching().reCacheConfigs();
            TutorialReloadEvent event = new TutorialReloadEvent();
            plugin.getServer().getPluginManager().callEvent(event);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
            return true;
        }
    }
}
