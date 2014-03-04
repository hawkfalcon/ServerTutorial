package io.snw.tutorial.util;

import io.snw.tutorial.ServerTutorial;
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
                for (String name : plugin.getters().getAllInTutorial()) {

                    Player player = plugin.getServer().getPlayerExact(name);
                    if (!player.isDead()) {
                        player.closeInventory();
                        if (plugin.getters().getTutorialView(name).getMessageType() == MessageType.META) {
                            setPlayerItemName(player);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    public void tutorialTimeTask(String tutorialName, final String name) {
        long num = (long) plugin.getters().getTutorial(tutorialName).getTimeLength();
        Long timeLength = num * 20L;

        new BukkitRunnable() {

            @Override
            public void run() {
                Player player = plugin.getServer().getPlayerExact(name);
                if (plugin.getters().getCurrentTutorial(name).getTotalViews() == plugin.getters().getCurrentView(name)) {
                    plugin.getEndTutorial().endTutorial(player);
                    cancel();
                    return;
                }
                if (plugin.getters().getTutorialView(name).getMessageType() == MessageType.META) {
                    setPlayerItemName(player);
                }
                plugin.incrementCurrentView(name);
                plugin.getTutorialUtils().textUtils(player);
                player.teleport(plugin.getters().getTutorialView(name).getLocation());
            }

        }.runTaskTimer(plugin, timeLength, timeLength);
    }

    public String tACC(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    boolean reset = true;
    String alt;

    public void setPlayerItemName(Player player) {
        ItemStack i = new ItemStack(plugin.getters().getCurrentTutorial(player.getName()).getItem());
        ItemMeta data = i.getItemMeta();
        if (reset) {
            alt = "" + ChatColor.RESET;
            reset = false;
        } else {
            alt = "";
            reset = true;
        }
        data.setDisplayName(tACC(plugin.getters().getTutorialView(player.getName()).getMessage()) + alt);

        i.setItemMeta(data);
        player.setItemInHand(i);
    }
}
