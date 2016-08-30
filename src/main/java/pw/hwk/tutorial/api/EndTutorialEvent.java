package pw.hwk.tutorial.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pw.hwk.tutorial.Tutorial;

public class EndTutorialEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Tutorial tutorial;

    public EndTutorialEvent(Player player, Tutorial tutorial) {
        this.player = player;
        this.tutorial = tutorial;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /*
     * Player who ended a tutorial
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /*
     * Tutorial which was ended
     * @return tutorial
     */
    public Tutorial getTutorial() {
        return this.tutorial;
    }
}