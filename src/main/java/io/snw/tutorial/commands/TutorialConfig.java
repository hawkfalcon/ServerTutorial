package io.snw.tutorial.commands;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.enums.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 *
 * @author Frostalf
 */
public class TutorialConfig implements CommandExecutor {

    private static ServerTutorial plugin = ServerTutorial.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length <= 2 || !args[1].equalsIgnoreCase("edit")) {
            sender.sendMessage(ChatColor.RED + "This only accepts 1 input \"edit\"");
            return true;
        }

        Player player = (Player) sender;
        if (Permissions.CONFIG.hasPerm(player)) {
            plugin.getConfigConversation().editConfig(player);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
        return true;
    }
}
