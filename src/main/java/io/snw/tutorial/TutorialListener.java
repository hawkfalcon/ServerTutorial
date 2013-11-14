package io.snw.tutorial;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TutorialListener implements Listener {


    ServerTutorial plugin;


    public TutorialListener(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (plugin.isInTutorial(name)) {
            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    endTutorial(player);
                }
            } else if (this.plugin.getTotalViews() == this.plugin.getCurrentView(name)) {
                endTutorial(player);
            } else {
                if (plugin.getTutorialView(name).getMessageType() == MessageType.TEXT) {
                    player.sendMessage(tACC(plugin.getTutorialView(player.getName()).getMessage()));
                }
                this.plugin.incrementCurrentView(name);
                player.teleport(plugin.getTutorialView(name).getLocation());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (plugin.isInTutorial(event.getPlayer().getName())) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.isInTutorial(event.getPlayer().getName())) {
            plugin.removeFromTutorial(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onWhee(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (plugin.isInTutorial(((Player) event.getEntity()).getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (String name : plugin.getAllInTutorial()) {
            Player tut = plugin.getServer().getPlayerExact(name);
            tut.hidePlayer(player);
            player.hidePlayer(tut);
        }
    }

    public void endTutorial(final Player player) {
        String name = player.getName();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("endmessage", "&7Woo")));
        plugin.removeFromTutorial(name);
        player.closeInventory();
        player.getInventory().remove(new ItemStack(Material.matchMaterial(plugin.getConfig().getString("material", "stick"))));
        player.teleport(plugin.getFirstLoc(name));
        plugin.cleanFirstLoc(name);
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player online : plugin.getServer().getOnlinePlayers()) {
                    online.showPlayer(player);
                    player.showPlayer(online);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public String tACC(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
