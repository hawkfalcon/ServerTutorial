
package io.snw.tutorial.commands;

import io.snw.tutorial.ServerTutorial;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Frostalf
 */
public class TutorialConfig implements CommandExecutor {

    ServerTutorial plugin;

    public TutorialConfig(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(args[1].equalsIgnoreCase("edit")) {
            
        }
        return true;
    }
}
