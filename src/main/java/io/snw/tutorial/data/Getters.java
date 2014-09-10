
package io.snw.tutorial.data;

import io.snw.tutorial.PlayerData;
import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.Tutorial;
import io.snw.tutorial.TutorialConfigs;
import io.snw.tutorial.TutorialView;
import io.snw.tutorial.util.TutorialTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Getters {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private static Getters instance;

    public Tutorial getCurrentTutorial(String name) {
        return Caching.getCaching().tutorial().get(Caching.getCaching().currentTutorial().get(name));
    }

    public Tutorial getTutorial(String tutorialName) {
        return Caching.getCaching().tutorial().get(tutorialName);
    }

    public TutorialView getTutorialView(String tutorialName, String name) {
        return Caching.getCaching().tutorial().get(tutorialName).getView(getCurrentView(name));
    }

    public TutorialView getTutorialView(String name) {
        return Caching.getCaching().tutorial().get(this.getCurrentTutorial(name).getName()).getView(getCurrentView(name));
    }

    public TutorialView getTutorialView(String tutorialName, int id) {
        return Caching.getCaching().tutorial().get(this.getTutorial(tutorialName).getName()).getView(id);
    }
    public TutorialConfigs getConfigs() {
        return Caching.getCaching().configs().get("config");
    }
    
    public PlayerData getPlayerData(String name) {
        return Caching.getCaching().playerDataMap().get(name);
    }

    public HashMap<String, PlayerData> getPlayerData() {
        return Caching.getCaching().playerDataMap();
    }

    public boolean getPlayerSeenServer(String name) {
        if (Caching.getCaching().playerDataMap().containsKey(name)) {
            return Caching.getCaching().playerDataMap().get(name).getSeen();
        } else {
            return false;
        }
    }

    public ArrayList<String> getAllTutorials() {
        return Caching.getCaching().tutorialNames();
    }

    public ArrayList<String> getAllInTutorial() {
        return Caching.getCaching().playerInTutorial();
    }

    public int getCurrentView(String name) {
        return Caching.getCaching().currentTutorialView().get(name);
    }

    public boolean isInTutorial(String name) {
        return Caching.getCaching().playerInTutorial().contains(name);
    }
    
    public TutorialTask getTutorialTask() {
        return plugin.tutorialTask();
    }

    public void getTutorialTimeTask(String tutorialName, String name) {
        getTutorialTask().tutorialTimeTask(tutorialName, name);
    }
    
    public Map<String, UUID> getResponse() {
        return Caching.getCaching().getResponse();
    }
    
    public static Getters getGetters() {
        if (instance == null) {
            instance = new Getters();
        }
        return instance;
    }
}
