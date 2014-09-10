
package io.snw.tutorial.enums;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Permissions {
    VIEW("tutorial.view", "tutorial.*"),
    USE("tutorial.use", "tutorial.*"),
    CREATE("tutorial.create", "tutorial.*"),
    REMOVE("tutorial.remove", "tutorial.*"),
    RELOAD("tutorial.reload", "tutorial.*"),
    CONFIG("tutorial.config", "tutorial.*"),
    TUTORIAL("tutorial.tutorial.%tutorial%", "tutorial.tutorial.*");

    String perm;
    ArrayList<String> hierarchy = new ArrayList<String>();

    Permissions(String perm, String... hierarchy) {
        this.perm = perm;
        this.hierarchy.addAll(Arrays.asList(hierarchy));
        
    }

    public boolean hasPerm(CommandSender sender){
        if(sender instanceof Player){
        return hasPerm((Player) sender);
        } else {
            sender.sendMessage("Commands can only be done in game!");
            return false;
        }
    }

    public boolean hasPerm(Player player){
        if(!(player.hasPermission(this.perm))) {
            for(String s : this.hierarchy){
                if(player.hasPermission(s)){
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean hasTutorialPerm(Player player, String tutorial) {
        String s = TUTORIAL.perm.replace("%tutorial%", tutorial);
        return player.hasPermission(s);
    }
}
