
package io.snw.tutorial.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Frostalf
 */
public class TutorialReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList(); 
    

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }    
    
}
