package pw.hwk.tutorial.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.conversation.CreateTutorial;
import pw.hwk.tutorial.conversation.ViewConversation;
import pw.hwk.tutorial.data.TutorialManager;
import pw.hwk.tutorial.enums.Permissions;


public class TutorialCreate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        if (!Permissions.CREATE.hasPerm(sender)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("create")) {

            if (TutorialManager.getManager().getAllTutorials().contains(args[1].toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "This tutorial already exists!");
                return true;
            }

            CreateTutorial.getCreateTutorial().createNewTutorial(player, args[1].toLowerCase());
            return true;
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("addview")) {
            if (!TutorialManager.getManager().getAllTutorials().contains(args[1].toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "You must create this tutorial first! " + ChatColor.GOLD +
                        "/tutorial create <name>");
                return true;
            }
            ViewConversation.getViewConversation().createNewView(player, args[1].toLowerCase());
            return true;

        }
        sender.sendMessage(ChatColor.RED + "Try " + ChatColor.GOLD + "/tutorial help");
        return true;
    }
}
