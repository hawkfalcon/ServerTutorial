
package io.snw.tutorial.commands;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.data.Getters;
import io.snw.tutorial.enums.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TutorialRemove implements CommandExecutor {
    
    private ServerTutorial plugin;
    private Getters getters = new Getters(plugin);
    
    public TutorialRemove(ServerTutorial plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        Player player = (Player) sender;
        
        if(Permissions.REMOVE.hasPerm(sender)){
            if(getters.getAllTutorials().contains(args[1])){
                if(args.length == 2) {
                    plugin.removeTutorial(args[1]);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Removed Tutorial: &b" + args[1]));
                    return true;
                }
                if(args.length == 3) {
                    if(getters.getTutorial(args[1]).getView(Integer.parseInt(args[2])) != null) {
                        try {
                            int id = Integer.parseInt(args[2]);
                            plugin.removeTutorialView(args[1], id);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Removed View: &b" + id + " from " + args[1]));
                            return true;
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Last Argument Needs to be a Number!"));
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Tutorial View does not exist!"));
                        return true;
                    }
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere is no Tutorial by that Name!"));
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
            return true;
        }
        return true;
    }
}
