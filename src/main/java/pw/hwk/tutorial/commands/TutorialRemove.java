package pw.hwk.tutorial.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.ServerTutorial;
import pw.hwk.tutorial.data.TutorialManager;
import pw.hwk.tutorial.enums.Permissions;
import pw.hwk.tutorial.util.TutorialUtils;

public class TutorialRemove implements CommandExecutor {

    private static ServerTutorial plugin = ServerTutorial.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        if (!Permissions.REMOVE.hasPerm(sender)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
            return true;
        }
        if (!TutorialManager.getManager().getAllTutorials().contains(args[1])) {
            player.sendMessage(TutorialUtils.color("&cThere is no Tutorial by that Name!"));
            return true;
        }
        if (args.length == 2) {
            plugin.removeTutorial(args[1]);
            sender.sendMessage(TutorialUtils.color("&4Removed Tutorial: &b" + args[1]));
            return true;
        }
        if (args.length != 3) {
            return true;
        }

        if (TutorialManager.getManager().getTutorial(args[1]).getView(Integer.parseInt(args[2])) != null) {
            try {
                int id = Integer.parseInt(args[2]);
                plugin.removeTutorialView(args[1], id);
                sender.sendMessage(TutorialUtils.color("&4Removed View: &b" + id + " from " + args[1]));
                return true;
            } catch (NumberFormatException ex) {
                sender.sendMessage(TutorialUtils.color("&4Last Argument Needs to be a Number!"));
                return true;
            }
        }
        sender.sendMessage(TutorialUtils.color("&4Tutorial View does not exist!"));
        return true;
    }
}
