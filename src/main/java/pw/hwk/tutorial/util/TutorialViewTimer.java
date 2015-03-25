/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.hwk.tutorial.util;

import java.util.Timer;
import java.util.TimerTask;
import pw.hwk.tutorial.ServerTutorial;
import pw.hwk.tutorial.data.Getters;

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

    public TutorialViewTimer(String playerName, String tutorialName) {
        this.playerName = playerName;
        this.tutorialName = tutorialName;
        this.seconds = Getters.getGetters().getTutorial(tutorialName).getTimeLength();
        this.timer = new Timer();
        timer.schedule(new DisplayCounter(), 1000);
    }

    public class DisplayCounter extends TimerTask {

        @Override
        public void run() {
            if (seconds > 0) {
                plugin.getServer().getPlayer(playerName).sendMessage("Seconds Remaining: " + seconds);
                seconds--;
            } else {
                cancel();
            }

        }
    }
}
