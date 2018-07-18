/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.hwk.tutorial.util;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pw.hwk.tutorial.ServerTutorial;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.TutorialManager;

public class TutorialViewTimer {

    private ServerTutorial plugin = ServerTutorial.getInstance();
    private String playerName;
    private String tutorialName;
    private int seconds;
    private Player player;

    public TutorialViewTimer(String playerName, String tutorialName) {
        this.playerName = playerName;
        this.player = plugin.getServer().getPlayerExact(playerName);
        this.tutorialName = tutorialName;
        this.seconds = TutorialManager.getManager().getTutorial(tutorialName).getTimeLength();
    }

    public void startTimer() {
        new DisplayCounter().runTaskTimer(plugin, 0, 20);
    }

    public class DisplayCounter extends BukkitRunnable {

        @Override
        public void run() {
            try {
                if (seconds > 0) {
                    player.setLevel(seconds);
                    seconds--;
                } else {
                    if (TutorialManager.getManager().getCurrentTutorial(playerName).getTotalViews() ==
                            TutorialManager.getManager().getCurrentView(playerName)) {
                        plugin.getEndTutorial().endTutorial(player);
                        this.cancel();
                        return;
                    }
                    plugin.incrementCurrentView(playerName);
                    Caching.getCaching().setTeleport(player, true);
                    player.teleport(TutorialManager.getManager().getTutorialView(playerName).getLocation());
                    TutorialUtils.getTutorialUtils().messageUtils(player);
                    if (TutorialManager.getManager().getTutorial(tutorialName).getView(TutorialManager.getManager().getCurrentView(playerName)).getViewTime().equalsIgnoreCase("default")) {
                        seconds = TutorialManager.getManager().getTutorial(tutorialName).getTimeLength();
                    } else {
                        seconds = Integer.parseInt(TutorialManager.getManager().getTutorial(tutorialName).getView(TutorialManager.getManager().getCurrentView(playerName)).getViewTime());
                    }
                    player.setLevel(seconds);
                    seconds--;
                }
            } catch (NullPointerException ex) {
                this.cancel();
            }
        }
    }
}
