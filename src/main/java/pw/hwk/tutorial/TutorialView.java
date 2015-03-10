package pw.hwk.tutorial;

import pw.hwk.tutorial.enums.MessageType;
import org.bukkit.Location;

public class TutorialView {

    private final String message;
    private final Location location;
    private final MessageType messageType;

    public TutorialView(String message, Location location, MessageType messageType) {
        this.message = message;
        this.location = location;
        this.messageType = messageType;
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
