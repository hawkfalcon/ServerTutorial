package pw.hwk.tutorial.commands;

import com.google.common.collect.Maps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.util.TutorialUtils;

import java.util.Map;

public class TutorialMainCommand implements CommandExecutor {

    private Map<String, CommandExecutor> subCommandMap = Maps.newHashMap();

    public TutorialMainCommand() {
        subCommandMap.put("create", new TutorialCreate());
        subCommandMap.put("use", new TutorialUse());
        subCommandMap.put("view", new TutorialView());
        subCommandMap.put("remove", new TutorialRemove());
        subCommandMap.put("addview", new TutorialCreate());
        subCommandMap.put("reload", new TutorialReload());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (!cmd.getName().equalsIgnoreCase("tutorial")) {
            return false;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage(TutorialUtils.color("&6-------------------------------\n>ServerTutorial " +
                        "Help:\n&8>&7 /tutorial <name> to enter tutorial\n&8>&7 /tutorial to list\n&8>&7 /tutorial " +
                        "create <name>\n&8>&7 /tutorial addview <name>"));
            } else if (args[0].equalsIgnoreCase("reload")) {
                return subCommandMap.get(args[0].toLowerCase()).onCommand(sender, cmd, commandLabel, args);
            } else {
                return subCommandMap.get("use").onCommand(sender, cmd, commandLabel, args);
            }
            return true;
        } else if (args.length > 1) {
            return !subCommandMap.containsKey(args[0].toLowerCase()) || subCommandMap.get(args[0].toLowerCase()).onCommand(sender, cmd, commandLabel, args);
        } else if (args.length == 0) {
            return subCommandMap.get("view").onCommand(sender, cmd, commandLabel, args);
        }
        return true;
    }
}
