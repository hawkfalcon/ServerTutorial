/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.hwk.tutorial.util;

import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.ServerTutorial;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.Getters;
import pw.hwk.tutorial.enums.MessageType;

/**
 *
 * @author Frostalf
 */
public class TutorialViewTimer {

    private ServerTutorial plugin = ServerTutorial.getInstance();
    private String playerName;
    private String tutorialName;
    private int seconds;
    private Timer timer;
    private Player player;

    public TutorialViewTimer(String playerName, String tutorialName) {
        this.playerName = playerName;
        this.player = plugin.getServer().getPlayerExact(playerName);
        this.tutorialName = tutorialName;
        this.seconds = Getters.getGetters().getTutorial(tutorialName).getTimeLength();
        this.timer = new Timer();
    }

    public void startTimer() {
        timer.schedule(new DisplayCounter(), 0, 1000);
    }

    public void cancelTimer() {
        timer.cancel();
    }

    public class DisplayCounter extends TimerTask {

        @Override
        public void run() {
            try {
                TutorialUtils.getTutorialUtils().textUtils(player);
                if (Getters.getGetters().getTutorialView(playerName).getMessageType() == MessageType.META) {
                    TutorialTask.getTutorialTask().setPlayerItemName(player);
                }

                for (int i = 0; i < Getters.getGetters().getTutorial(tutorialName).getTimeLength(); i++) {
                    player.sendMessage(" "); // Flood chat to make it more readable
                }
                if (seconds > 0) {
                    player.sendMessage("Seconds Remaining: " + seconds);
                    seconds--;
                } else {
                    if (Getters.getGetters().getCurrentTutorial(playerName).getTotalViews() == Getters.getGetters().getCurrentView(playerName)) {
                        plugin.getEndTutorial().endTutorial(player);
                        this.cancel();
                        return;
                    }
                    plugin.incrementCurrentView(playerName);
                    Caching.getCaching().setTeleport(player.getUniqueId(), true);
                    player.teleport(Getters.getGetters().getTutorialView(playerName).getLocation());
                    TutorialUtils.getTutorialUtils().textUtils(player);
                    seconds = Getters.getGetters().getTutorial(tutorialName).getTimeLength();
                    player.sendMessage("Seconds Remaining: " + seconds);
                    seconds--;
                }
            } catch (NullPointerException ex) {
                this.cancel();
            }
        }
    }
}
