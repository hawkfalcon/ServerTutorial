
package io.snw.tutorial.enums;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Frostalf
 */
public enum Permissions {
    
    VIEW("tutorial.view", "", "tutorial.*"),
    USE("tutorial.use", "", "tutorial.*"),
    CREATE("tutorial.create", "", "tutorial.*"),
    REMOVE("tutorial.remove", "", "tutorial.*");
    
    String perm;
    String requiredPerm;
    ArrayList<String> hierarchy = new ArrayList<String>();

    Permissions(String perm, String requiredPerm, String... hierarchy) {
        this.perm = perm;
        this.requiredPerm = requiredPerm;
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
        boolean hasRequiredPerm = this.requiredPerm.equalsIgnoreCase("") ? true : player.hasPermission(this.requiredPerm);
        if(!(player.hasPermission(this.perm) && hasRequiredPerm)) {
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
}
