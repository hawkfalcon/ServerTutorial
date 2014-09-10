package io.snw.tutorial.api;

import io.snw.tutorial.Tutorial;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StartTutorialEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Tutorial tutorial;

    public StartTutorialEvent(Player player, Tutorial tutorial) {
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

    /**
     * Player who started a tutorial
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Tutorial which was started
     * @return tutorial
     */
    public Tutorial getTutorial() {
        return this.tutorial;
    }
}
