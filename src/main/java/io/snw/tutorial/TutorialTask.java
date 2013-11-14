package io.snw.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class TutorialTask {
    ServerTutorial plugin;
    public Material mat;

    public TutorialTask(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void tutorialTask() {
        mat = Material.matchMaterial(plugin.getConfig().getString("material", "stick"));
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
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public String tACC(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    boolean reset = true;
    String alt;

    public void setPlayerItemName(Player player) {
        ItemStack i = new ItemStack(mat);
        ItemMeta data = i.getItemMeta();
        if (reset) {
            alt = "" + ChatColor.RESET;
            reset = false;
        } else {
            alt = "";
            reset = true;
        }
        Bukkit.getLogger().info(tACC(plugin.getTutorialView(player.getName()).getMessage()) + alt);
        data.setDisplayName(tACC(plugin.getTutorialView(player.getName()).getMessage()) + alt);

        i.setItemMeta(data);
        player.setItemInHand(i);
    }
}
