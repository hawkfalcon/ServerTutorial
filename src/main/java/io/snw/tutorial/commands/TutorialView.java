
package io.snw.tutorial.commands;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.enums.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TutorialView implements CommandExecutor {
    
    private ServerTutorial plugin;
    
    public TutorialView (ServerTutorial plugin) {
        this.plugin = plugin;
    }
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        Player player = (Player) sender;
        
        if (Permissions.VIEW.hasPerm(sender)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6-------------------------------\nAvailable tutorials:"));
            StringBuilder sb = new StringBuilder();
            if (!plugin.getters().getAllTutorials().isEmpty()) {
                for (String tutorial : plugin.getters().getAllTutorials()) {
                    if (sb.length() > 0) {
                        sb.append(',');
                        sb.append(' ');
                    }
                    sb.append(tutorial);
                }
                player.sendMessage(sb.toString());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6-------------------------------"));
            } else {
                player.sendMessage(ChatColor.RED + "There are currently no tutorials setup.\nSet one up with /tutorial create <name>");
            }
        }
        return true;
    }
}
