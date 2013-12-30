package io.snw.tutorial;

import io.snw.tutorial.enums.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class TutorialTask {
    ServerTutorial plugin;

    public TutorialTask(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void tutorialTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (String name : plugin.getAllInTutorial()) {

                    Player player = plugin.getServer().getPlayerExact(name);
                    if (!player.isDead()) {
                        player.closeInventory();
                        if (plugin.getTutorialView(name).getMessageType() == MessageType.META) {
                            setPlayerItemName(player);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    public void tutorialTimeTask(String tutorialName, final String name) {
        long num = (long) plugin.getTutorial(tutorialName).getTimeLength();
        Long timeLength = num * 20L;

        new BukkitRunnable() {

            @Override
            public void run() {
                Player player = plugin.getServer().getPlayerExact(name);
                if (plugin.getCurrentTutorial(name).getTotalViews() == plugin.getCurrentView(name)) {
                    endTutorial(player);
                    cancel();
                    return;
                }
                plugin.incrementCurrentView(name);
                plugin.getTutorialUtils().textUtils(player);
                player.teleport(plugin.getTutorialView(name).getLocation());
            }

        }.runTaskTimer(plugin, timeLength, timeLength);
    }

    public String tACC(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    boolean reset = true;
    String alt;

    public void setPlayerItemName(Player player) {
        ItemStack i = new ItemStack(plugin.getCurrentTutorial(player.getName()).getItem());
        ItemMeta data = i.getItemMeta();
        if (reset) {
            alt = "" + ChatColor.RESET;
            reset = false;
        } else {
            alt = "";
            reset = true;
        }
        data.setDisplayName(tACC(plugin.getTutorialView(player.getName()).getMessage()) + alt);

        i.setItemMeta(data);
        player.setItemInHand(i);
    }

    public void endTutorial(final Player player) {
        final String name = player.getName();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getCurrentTutorial(name).getEndMessage()));
        player.closeInventory();
        player.getInventory().clear();
        player.setAllowFlight(plugin.getFlight(name));
        player.setFlying(false);
        plugin.removeFlight(name);
        player.teleport(plugin.getFirstLoc(name));
        plugin.cleanFirstLoc(name);
        plugin.removeFromTutorial(name);
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player online : plugin.getServer().getOnlinePlayers()) {
                    online.showPlayer(player);
                    player.showPlayer(online);
                }
                player.getInventory().setContents(plugin.getInventory(name));
                plugin.cleanInventory(name);
            }
        }.runTaskLater(plugin, 20L);
    }
}
