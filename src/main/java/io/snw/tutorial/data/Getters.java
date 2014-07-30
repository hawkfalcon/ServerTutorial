
package io.snw.tutorial.data;

import io.snw.tutorial.PlayerData;
import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.Tutorial;
import io.snw.tutorial.TutorialConfigs;
import io.snw.tutorial.util.TutorialTask;
import io.snw.tutorial.TutorialView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Getters {

    ServerTutorial plugin;

    public Getters(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public Tutorial getCurrentTutorial(String name) {
        return plugin.caching().tutorial().get(plugin.caching().currentTutorial().get(name));
    }

    public Tutorial getTutorial(String tutorialName) {
        return plugin.caching().tutorial().get(tutorialName);
    }

    public TutorialView getTutorialView(String tutorialName, String name) {
        return plugin.caching().tutorial().get(tutorialName).getView(getCurrentView(name));
    }

    public TutorialView getTutorialView(String name) {
        return plugin.caching().tutorial().get(this.getCurrentTutorial(name).getName()).getView(getCurrentView(name));
    }

    public TutorialView getTutorialView(String tutorialName, int id) {
        return plugin.caching().tutorial().get(this.getTutorial(tutorialName).getName()).getView(id);
    }
    public TutorialConfigs getConfigs() {
        return plugin.caching().configs().get("config");
    }
    
    public PlayerData getPlayerData(String name) {
        return plugin.caching().playerDataMap().get(name);
    }

    public HashMap<String, PlayerData> getPlayerData() {
        return plugin.caching().playerDataMap();
    }

    public boolean getPlayerSeenServer(String name) {
        if (plugin.caching().playerDataMap().containsKey(name)) {
            return plugin.caching().playerDataMap().get(name).getSeen();
        } else {
            return false;
        }
    }

    public ArrayList<String> getAllTutorials() {
        return plugin.caching().tutorialNames();
    }

    public ArrayList<String> getAllInTutorial() {
        return plugin.caching().playerInTutorial();
    }

    public int getCurrentView(String name) {
        return plugin.caching().currentTutorialView().get(name);
    }

    public boolean isInTutorial(String name) {
        return plugin.caching().playerInTutorial().contains(name);
    }
    
    public TutorialTask getTutorialTask() {
        return plugin.tutorialTask();
    }

    public void getTutorialTimeTask(String tutorialName, String name) {
        getTutorialTask().tutorialTimeTask(tutorialName, name);
    }
    
    public Map<String, UUID> getResponse() {
        return plugin.caching().getResponse();
    }
}
