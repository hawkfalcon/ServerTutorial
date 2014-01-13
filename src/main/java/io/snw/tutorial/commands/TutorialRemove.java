
package io.snw.tutorial.commands;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.enums.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Frostalf
 */
public class TutorialRemove implements CommandExecutor {
    
    private ServerTutorial plugin;
    
    public TutorialRemove(ServerTutorial plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        Player player = (Player) sender;
        
        if(Permissions.REMOVE.hasPerm(sender)){
            if(plugin.getAllTutorials().contains(args[0])){
            }
        }
        return true;
    }
}
