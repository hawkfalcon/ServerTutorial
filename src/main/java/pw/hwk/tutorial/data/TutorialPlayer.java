package pw.hwk.tutorial.data;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TutorialPlayer {
    private Location startLoc;
    private ItemStack[] inventory;
    private boolean flight;
    private boolean allowFlight;
    private float exp;
    private int level;
    private int hunger;
    private double health;
    private GameMode gameMode;

    public TutorialPlayer(Player player) {
        this.startLoc = player.getLocation();
        this.inventory = player.getInventory().getContents();
        this.flight = player.isFlying();
        this.allowFlight = player.getAllowFlight();
        this.exp = player.getExp();
        this.level = player.getLevel();
        this.hunger = player.getFoodLevel();
        this.health = player.getHealth();
        this.gameMode = player.getGameMode();
    }

    public void clearPlayer(Player player) {
        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);

        player.setExp(1.0f);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());

        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            online.hidePlayer(player);
            player.hidePlayer(online);
        }
    }

    public void restorePlayer(Player player) {
        player.teleport(startLoc);
        player.getInventory().setContents(inventory);
        player.setFlying(flight);
        player.setAllowFlight(allowFlight);
        player.setExp(exp);
        player.setLevel(level);
        player.setFoodLevel(hunger);
        player.setHealth(health);
        player.setGameMode(gameMode);

        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            online.showPlayer(player);
            player.showPlayer(online);
        }
    }


}
