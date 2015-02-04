package io.snw.tutorial.util;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.data.Caching;
import io.snw.tutorial.data.Getters;
import io.snw.tutorial.enums.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class TutorialTask {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private static TutorialTask instance;

    public void tutorialTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (String name : Getters.getGetters().getAllInTutorial()) {

                    Player player = plugin.getServer().getPlayerExact(name);
                    if (player == null) //Something went wrong, this shouldn't happen, but it does...
                    {
                        cancel();
                        return;
                    }
                    if (!player.isDead()) {
                        player.closeInventory();
                        if (Getters.getGetters().getTutorialView(name).getMessageType() == MessageType.META) {
                            setPlayerItemName(player);
                            try {
                                if (Getters.getGetters().getTutorialView(name).getMessageType() == MessageType.META) {
                                    setPlayerItemName(player);
                                }
                            } catch (NullPointerException ignored) {
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    public void tutorialTimeTask(final String tutorialName, final String name) {
        //long num = (long) Getters.getGetters().getTutorial(tutorialName).getTimeLength();
        //Long timeLength = num * 20L;

        assert tutorialName != null && name != null;

        final Player player = Bukkit.getPlayerExact(name);

        if (player == null) {
            return;
        }

        Caching.getCaching().setTeleport(player.getUniqueId(), true);
        player.teleport(Getters.getGetters().getTutorialView(name).getLocation());

        final int modifier = 2; //Todo: make configurable

        long speed = 20L / modifier;

        new BukkitRunnable() {
            long timeLeft = (long) Getters.getGetters().getTutorial(tutorialName).getTimeLength() * modifier;

            @Override
            public void run() {
                try {
                    Player player = plugin.getServer().getPlayerExact(name);
                    if (Getters.getGetters().getCurrentTutorial(name).getTotalViews() == Getters.getGetters().getCurrentView(name)) {
                        plugin.getEndTutorial().endTutorial(player);
                        cancel();
                        return;
                    }

                    long shownTime = timeLeft / modifier;

                    try {
                        if (Getters.getGetters().getCurrentTutorial(name).getTotalViews() == Getters.getGetters().getCurrentView(name)) {
                            plugin.getEndTutorial().endTutorial(player);
                            cancel();
                            return;
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                        plugin.getEndTutorial().endTutorial(player);
                        cancel();
                    }

                    if (Getters.getGetters().getTutorialView(name).getMessageType() == MessageType.META) {
                        setPlayerItemName(player);
                    }

                    for (int i = 0; i < 25; i++) {
                        player.sendMessage(" "); // Flood chat to make it more readable
                    }

                    TutorialUtils.getTutorialUtils().textUtils(player);
                    player.sendMessage(ChatColor.RED + "(" + shownTime + ")"); // Send time left

                    // View ended
                    if (timeLeft == 0) {
                        plugin.incrementCurrentView(name);
                        tutorialTimeTask(tutorialName, name); // Restart for next view
                        cancel();
                        return;
                    }

                    timeLeft--;
                } catch (NullPointerException e) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, speed);
    }

    public String tACC(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    boolean reset = true;
    String alt;

    public void setPlayerItemName(Player player) {
        ItemStack i = new ItemStack(Getters.getGetters().getCurrentTutorial(player.getName()).getItem());
        ItemMeta data = i.getItemMeta();
        if (reset) {
            alt = "" + ChatColor.RESET;
            reset = false;
        } else {
            alt = "";
            reset = true;
        }
        data.setDisplayName(tACC(Getters.getGetters().getTutorialView(player.getName()).getMessage()) + alt);

        i.setItemMeta(data);
        player.setItemInHand(i);
    }

    public static TutorialTask getTutorialTask() {
        if (instance == null) {
            instance = new TutorialTask();
        }
        return instance;
    }
}
