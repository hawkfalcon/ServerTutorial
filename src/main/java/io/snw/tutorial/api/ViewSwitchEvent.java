package io.snw.tutorial.api;

import io.snw.tutorial.Tutorial;
import io.snw.tutorial.TutorialView;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ViewSwitchEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private TutorialView fromTutorialView;
    private TutorialView toTutorialView;
    private Tutorial tutorial;

    public ViewSwitchEvent(Player player, TutorialView fromTutorialView, TutorialView toTutorialView, Tutorial tutorial) {
        this.player = player;
        this.tutorial = tutorial;
        this.fromTutorialView = fromTutorialView;
        this.toTutorialView = toTutorialView;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Player who switched view
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * View switched from
     * @return fromTutorialView
     */
    public TutorialView getFromTutorialView() {
        return this.fromTutorialView;
    }

    /**
     * View switched to
     * @return toTutorialView
     */
    public TutorialView getToTutorialView() {
        return this.toTutorialView;
    }
    
    /**
     * Tutorial from which view is part of
     * @return Tutorial
     */
    public Tutorial getTutorial() {
        return this.tutorial;
    }
}