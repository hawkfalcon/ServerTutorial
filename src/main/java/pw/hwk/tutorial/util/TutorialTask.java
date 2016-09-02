package pw.hwk.tutorial.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.TutorialManager;

public class TutorialTask {

    private static TutorialTask instance;

    public void tutorialTimeTask(final String tutorialName, final String name) {
        assert tutorialName != null && name != null;
        final Player player = Bukkit.getPlayerExact(name);

        if (player == null) {
            return;
        }

        Caching.getCaching().setTeleport(player, true);
        player.teleport(TutorialManager.getManager().getTutorialView(name).getLocation());
        TutorialViewTimer timer = new TutorialViewTimer(name, tutorialName);
        timer.startTimer();

    }

    public static TutorialTask getTutorialTask() {
        if (instance == null) {
            instance = new TutorialTask();
        }
        return instance;
    }
}
