package io.snw.tutorial;

import io.snw.tutorial.enums.MessageType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TutorialUtils {
    ServerTutorial plugin;

    public TutorialUtils(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public Location getLocation(String tutorialName, int viewID) {
        String[] loc = plugin.getConfig().getString("tutorials." + tutorialName + ".views." + viewID + ".location").split("\\,");
        World w = Bukkit.getWorld(loc[0]);
        Double x = Double.parseDouble(loc[1]);
        Double y = Double.parseDouble(loc[2]);
        Double z = Double.parseDouble(loc[3]);
        float yaw = Float.parseFloat(loc[4]);
        float pitch = Float.parseFloat(loc[5]);
        return new Location(w, x, y, z, yaw, pitch);
    }

    public void textUtils(Player player) {
        String name = player.getName();
        if (plugin.getTutorialView(name).getMessageType() == MessageType.TEXT) {
            player.getInventory().clear();
            ItemStack i = new ItemStack(plugin.getCurrentTutorial(name).getItem());
            ItemMeta data = i.getItemMeta();
            data.setDisplayName(" ");
            i.setItemMeta(data);
            player.setItemInHand(i);
            player.sendMessage(tACC(plugin.getTutorialView(name).getMessage()));
        }
    }

    public String tACC(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
