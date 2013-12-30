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
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("tutorial")) {
            if (args.length == 0) {
                if (sender.hasPermission("tutorial.view")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6-------------------------------\nAvailable tutorials:"));
                    StringBuilder sb = new StringBuilder();
                    if (plugin.getAllTutorials().size() != 0) {
                        for (String tutorial : plugin.getAllTutorials()) {
                            if (sb.length() > 0) {
                                sb.append(',');
                                sb.append(' ');
                            }
                            sb.append(tutorial);
                        }
                        player.sendMessage(sb.toString());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6-------------------------------"));
                    } else {
                        player.sendMessage(ChatColor.RED + "There are currently no tutorials setup.\nSet one up with /tutorial create <name>");
                    }
                }
                return true;
            }


            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6-------------------------------\n&8>ServerTutorial Help:\n&8>&71. /tutorial <name> to enter tutorial\n&8>&72. /tutorial to list\n&8>&73. /tutorial create <name>\n&8>&74. /tutorial addview <name>"));
                } else {
                    if(plugin.getAllTutorials().contains(args[0])){
                    boolean permissions = plugin.getData().getBoolean("permissions");
                        if (permissions == true){
                            if(sender.hasPermission("servertutorial.tutorials" + args[0]) || sender.hasPermission("servertutorial.tutorials.*") || sender.hasPermission("servertutorials.*")){
                            this.plugin.startTutorial(args[0], player);
                            }
                        } else {
                        this.plugin.startTutorial(args[0], player);
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere is no Tutorial by that Name!"));
                    }
                    
                }
                return true;
            }
            if (args.length > 1) {
                if (sender.hasPermission("tutorial.create")) {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (!plugin.getAllTutorials().contains(args[1])) {
                            plugin.getCreateTutorial().createNewTutorial(player, args[1]);
                        } else {
                            sender.sendMessage(ChatColor.RED + "This tutorial already exists!");
                            return true;
                        }
                    } else if (args[0].equalsIgnoreCase("addview")) {
                        if (!plugin.getAllTutorials().contains(args[1])) {
                            sender.sendMessage(ChatColor.RED + "You must create this tutorial first! " + ChatColor.GOLD + "/server create <name>");
                            return true;
                        }
                        plugin.getViewConversation().createNewView(player, args[1]);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Try " + ChatColor.GOLD + "/tutorial help");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
                    return true;
                }
            }
        }
        return false;
    }
}