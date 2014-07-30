
package io.snw.tutorial;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private boolean seen;
    private HashMap<String, MapPlayerTutorial> tutorials;

    public PlayerData(UUID uuid, boolean seen, HashMap<String, MapPlayerTutorial> tutorials) {
        this.uuid = uuid;
        this.seen = seen;
        this.tutorials = tutorials;
    }

    public UUID getUUID() {
        return this.uuid;
    }
    
    public boolean getSeen() {
        return this.seen;
    }

    public HashMap<String, MapPlayerTutorial> getPlayerTutorialData() {
        return this.tutorials;
    }
}
