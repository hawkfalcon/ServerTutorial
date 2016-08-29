package pw.hwk.tutorial.commands;

import pw.hwk.tutorial.data.Getters;
import pw.hwk.tutorial.enums.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.util.TutorialUtils;

public class TutorialView implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!Permissions.VIEW.hasPerm(sender)) {
            return true;
        }

        player.sendMessage(TutorialUtils.color("&6-------------------------------\nAvailable tutorials:"));
        StringBuilder sb = new StringBuilder();
        if (Getters.getGetters().getAllTutorials().isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are currently no tutorials setup.\nSet one up with /tutorial create <name>");
            return true;
        }

        for (String tutorial : Getters.getGetters().getAllTutorials()) {
            if (sb.length() > 0) {
                sb.append(',');
                sb.append(' ');
            }
            sb.append(tutorial);
        }
        player.sendMessage(sb.toString());
        player.sendMessage(TutorialUtils.color("&6-------------------------------"));
        return true;
    }
}
