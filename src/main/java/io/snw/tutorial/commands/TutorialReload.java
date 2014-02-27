
package io.snw.tutorial.commands;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.enums.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Frostalf
 */
public class TutorialReload implements CommandExecutor {
    
    private ServerTutorial plugin;
    
    public TutorialReload(ServerTutorial plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(Permissions.RELOAD.hasPerm(sender)) {
            plugin.reCasheTutorials();
            for(String playerName : plugin.getAllInTutorial()) {
                Player player = plugin.getServer().getPlayer(playerName);
                plugin.getEndTutorial().endTutorial(player);
            }
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
            return true;
        }
    }    

}
