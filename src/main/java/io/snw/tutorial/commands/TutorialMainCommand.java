package io.snw.tutorial.commands;

import com.google.common.collect.Maps;
import io.snw.tutorial.ServerTutorial;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class TutorialMainCommand implements CommandExecutor {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private Map<String, CommandExecutor> subCommandMap = Maps.newHashMap();

    public TutorialMainCommand() {
        subCommandMap.put("create", new TutorialCreate());
        subCommandMap.put("use", new TutorialUse());
        subCommandMap.put("view", new TutorialView());
        subCommandMap.put("remove", new TutorialRemove());
        subCommandMap.put("addview", new TutorialCreate());
        subCommandMap.put("reload", new TutorialReload());
        subCommandMap.put("config", new TutorialConfig());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        CommandExecutor subCommandUse = subCommandMap.get("use");
        CommandExecutor subCommandView = subCommandMap.get("view");
        CommandExecutor subCommandReload = subCommandMap.get("reload");
        CommandExecutor subCommandConfig = subCommandMap.get("config");
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (!cmd.getName().equalsIgnoreCase("tutorial")) {
            return false;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                          "&6-------------------------------\n&8>ServerTutorial Help:\n&8>&71. /tutorial <name> to enter tutorial\n&8>&72. /tutorial to list\n&8>&73. /tutorial create <name>\n&8>&74. /tutorial addview <name>"));
            } else if (args[0].equalsIgnoreCase("reload")) {
                return subCommandReload.onCommand(sender, cmd, commandLabel, args);
            } else {
                return subCommandUse.onCommand(sender, cmd, commandLabel, args);
            }
            return true;
        } else if (args.length > 1) {
            String subCommandName = args[0].toLowerCase();
            CommandExecutor subCommand = subCommandMap.get(subCommandName);
            return !subCommandMap.containsKey(subCommandName) || subCommand.onCommand(sender, cmd, commandLabel, args);
        } else if (args.length == 0) {
            return subCommandView.onCommand(sender, cmd, commandLabel, args);
        }
        return true;
    }
}