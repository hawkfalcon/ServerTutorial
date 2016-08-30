package pw.hwk.tutorial.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.TutorialView;
import pw.hwk.tutorial.data.DataLoading;
import pw.hwk.tutorial.data.Getters;

public class TutorialUtils {

    private static TutorialUtils instance;

    public Location getLocation(String tutorialName, int viewID) {
        String[] loc = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".views." + viewID + ".location").split(",");

        World w = Bukkit.getWorld(loc[0]);
        Double x = Double.parseDouble(loc[1]);
        Double y = Double.parseDouble(loc[2]);
        Double z = Double.parseDouble(loc[3]);
        float yaw = Float.parseFloat(loc[4]);
        float pitch = Float.parseFloat(loc[5]);

        return new Location(w, x, y, z, yaw, pitch);
    }

    public void messageUtils(Player player) {
        TutorialView tutorialView = Getters.getGetters().getTutorialView(player.getName());
        String message = color(tutorialView.getMessage());

        switch (tutorialView.getMessageType()) {
            case META:
            case TEXT:
                String lines[] = message.split("\\\\n");

                for (String msg : lines) {
                    player.sendMessage(msg);
                }
                break;
        }
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static TutorialUtils getTutorialUtils() {
        if (instance == null) {
            instance = new TutorialUtils();
        }
        return instance;
    }
}
