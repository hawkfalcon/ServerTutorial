package pw.hwk.tutorial.commands;

import pw.hwk.tutorial.ServerTutorial;
import pw.hwk.tutorial.data.Getters;
import pw.hwk.tutorial.enums.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.util.TutorialUtils;

public class TutorialUse implements CommandExecutor {

    private static ServerTutorial plugin = ServerTutorial.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (Permissions.USE.hasPerm(sender)) {
            if (Getters.getGetters().getAllTutorials().contains(args[0].toLowerCase())) {
                plugin.startTutorial(args[0], player);
                return true;
            } else {
                player.sendMessage(TutorialUtils.color("&cThere is no Tutorial by that Name!"));
                return true;
            }
        } else if (Permissions.TUTORIAL.hasTutorialPerm(player, args[0].toLowerCase())) {
            if (Getters.getGetters().getAllTutorials().contains(args[0].toLowerCase())) {
                plugin.startTutorial(args[0], player);
                return true;
            } else {
                player.sendMessage(TutorialUtils.color("&cThere is no Tutorial by that Name!"));
                return true;
            }
        }

        player.sendMessage(TutorialUtils.color("&cYou do not have permission for this!!"));
        return true;
    }
}
