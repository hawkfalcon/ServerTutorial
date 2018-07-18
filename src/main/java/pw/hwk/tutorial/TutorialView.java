package pw.hwk.tutorial;

import org.bukkit.Location;
import pw.hwk.tutorial.enums.MessageType;

public class TutorialView {

    private final String message;
    private final Location location;
    private final MessageType messageType;
    private final String viewTime;

    public TutorialView(String message, Location location, MessageType messageType, String viewTime) {
        this.message = message;
        this.location = location;
        this.messageType = messageType;
        this.viewTime = viewTime;
    }

    public String getMessage() {
        return this.message;
    }

    public Location getLocation() {
        return this.location;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public String getViewTime() {
        return this.viewTime;
    }
}
