
package io.snw.tutorial.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TutorialReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList(); 

    public TutorialReloadEvent() {}
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
