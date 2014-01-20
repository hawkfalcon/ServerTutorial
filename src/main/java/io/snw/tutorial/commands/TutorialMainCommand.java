package io.snw.tutorial.commands;

import com.google.common.collect.Maps;
import io.snw.tutorial.ServerTutorial;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TutorialMainCommand implements CommandExecutor {

    private ServerTutorial plugin;
    private Map<String, CommandExecutor> subCommandMap = Maps.newHashMap();

    public TutorialMainCommand(ServerTutorial plugin) {
        this.plugin = plugin;
        subCommandMap.put("create", new TutorialCreate(plugin));
        subCommandMap.put("use", new TutorialUse(plugin));
        subCommandMap.put("view", new TutorialView(plugin));
        subCommandMap.put("remove", new TutorialRemove(plugin));
        subCommandMap.put("addview", new TutorialCreate(plugin));
    }

    
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        CommandExecutor subCommandUse = subCommandMap.get("use");
        CommandExecutor subCommandView = subCommandMap.get("view");
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("tutorial")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6-------------------------------\n&8>ServerTutorial Help:\n&8>&71. /tutorial <name> to enter tutorial\n&8>&72. /tutorial to list\n&8>&73. /tutorial create <name>\n&8>&74. /tutorial addview <name>"));
                } else {
                    return subCommandUse.onCommand(sender, cmd, commandLabel, args);
                }
                return true;
            }
            if (args.length > 1) {
                String subCommandName = args[0].toLowerCase();
                CommandExecutor subCommand = subCommandMap.get(subCommandName);
                if(subCommandMap.containsKey(subCommandName)) {
                    return subCommand.onCommand(sender, cmd, commandLabel, args);
                }
                return true;
            }
            if (args.length == 0) {
                return subCommandView.onCommand(sender, cmd, commandLabel, args);
            }
            return true;
        }
        return false;
    }
}