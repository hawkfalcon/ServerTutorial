
package io.snw.tutorial.api;

import io.snw.tutorial.Tutorial;
import io.snw.tutorial.TutorialView;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AddViewEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Tutorial tutorial;
    private TutorialView tutorialView;

    public AddViewEvent(Player player, Tutorial tutorial, TutorialView tutorialView) {
        this.player = player;
        this.tutorial = tutorial;
        this.tutorialView = tutorialView;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Player who created view
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Tutorial which view was created for
     * @return tutorial
     */
    public Tutorial getTutorial() {
        return this.tutorial;
    }
    /**
     * View which was created
     * @return view
     */
    public TutorialView getTutorialView() {
        return this.tutorialView;
    }
}
