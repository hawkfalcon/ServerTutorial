package io.snw.tutorial;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TutorialCommands implements CommandExecutor {

    ServerTutorial plugin;

    public TutorialCommands(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("tutorial")) {
            if (args.length == 0) {
                if (sender.hasPermission("tutorial.view")) {
                    player.sendMessage("Availible tutorials:");
                    StringBuilder sb = new StringBuilder();
                    for (String tutorial : plugin.getAllTutorials()) {
                        if (sb.length() > 0) {
                            sb.append(',');
                        }
                        sb.append(tutorial);
                    }
                    player.sendMessage(sb.toString());
                }
            }

            if (args.length == 1) {
                this.plugin.startTutorial(args[0], player);
            }
            if (args.length > 1) {
                if (sender.hasPermission("tutorial.create")) {
                    if (args[0].equalsIgnoreCase("create")) {
                        plugin.getCreateTutorial().createNewTutorial(player, args[1]);
                        sender.sendMessage(ChatColor.DARK_BLUE + "[Tutorial] " + ChatColor.LIGHT_PURPLE + "Tutorial " + args[1] + " was successfully saved.");
                    } else if (args[0].equalsIgnoreCase("addview")) {
                        plugin.getViewConversation().createNewView(player, args[1]);
                        sender.sendMessage(ChatColor.DARK_BLUE + "[Tutorial] " + ChatColor.LIGHT_PURPLE + "View was successfully saved.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Try /tutorial");
                }
            }
        }
        return true;
    }
}