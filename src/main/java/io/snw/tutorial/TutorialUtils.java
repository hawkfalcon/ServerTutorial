package io.snw.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class TutorialUtils {
    ServerTutorial plugin;

    public TutorialUtils(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void saveLoc(int viewID, Location loc) {
        String location = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
        plugin.getConfig().set("views." + viewID + ".location", location);
        plugin.saveConfig();
    }

    public Location getLocation(int viewID) {
        String[] loc = plugin.getConfig().getString("views." + viewID + ".location").split("\\,");
        World w = Bukkit.getWorld(loc[0]);
        Double x = Double.parseDouble(loc[1]);
        Double y = Double.parseDouble(loc[2]);
        Double z = Double.parseDouble(loc[3]);
        float yaw = Float.parseFloat(loc[4]);
        float pitch = Float.parseFloat(loc[5]);
        Location location = new Location(w, x, y, z, yaw, pitch);
        return location;
    }


}
