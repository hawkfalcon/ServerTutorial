package io.snw.tutorial;

import io.snw.tutorial.enums.MessageType;
import org.bukkit.Location;

public class TutorialView {
    private final int viewId;
    private final String message;
    private final Location location;
    private final MessageType messageType;

    public TutorialView(int viewId, String message, Location location, MessageType messageType) {
        this.viewId = viewId;
        this.message = message;
        this.location = location;
        this.messageType = messageType;
    }

    public int getId() {
        return this.viewId;
    }

    public String getMessage() {
        return this.message;
    }

    public Location getLocation() {
        return this.location;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
