package pw.hwk.tutorial;

import java.util.HashMap;

public class PlayerData {

    private HashMap<String, MapPlayerTutorial> tutorials;

    public PlayerData(HashMap<String, MapPlayerTutorial> tutorials) {
        this.tutorials = tutorials;
    }
    
    public HashMap<String, MapPlayerTutorial> getPlayerTutorialData() {
        return this.tutorials;
    }
}
