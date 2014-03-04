
package io.snw.tutorial.data;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.Tutorial;
import io.snw.tutorial.TutorialConfigs;
import io.snw.tutorial.util.TutorialTask;
import io.snw.tutorial.TutorialView;
import java.util.ArrayList;

public class Getters {
    
    ServerTutorial plugin;
    
    public Getters(ServerTutorial plugin) {
        this.plugin = plugin;
    }
    
    private Caching cache;
    private TutorialTask tutorialTask;

    public Tutorial getCurrentTutorial(String name) {
        return this.cache.tutorial().get(cache.currentTutorial().get(name));
    }
    
    public Tutorial getTutorial(String tutorialName) {
        return this.cache.tutorial().get(tutorialName);
    }
    
    public TutorialView getTutorialView(String tutorialName, String name) {
        return this.cache.tutorial().get(tutorialName).getView(getCurrentView(name));
    }

    public TutorialView getTutorialView(String name) {
        return this.cache.tutorial().get(this.getCurrentTutorial(name).getName()).getView(getCurrentView(name));
    }    
    
    public TutorialConfigs getConfigs() {
        return this.cache.configs().get("config");
    }        
    
    public ArrayList<String> getAllTutorials() {
        return cache.tutorialNames();
    }

    public ArrayList<String> getAllInTutorial() {
        return cache.playerInTutorial();
    }

    public int getCurrentView(String name) {
        return cache.currentTutorialView().get(name);
    }

    public boolean isInTutorial(String name) {
        return cache.playerInTutorial().contains(name);
    }
    
    public TutorialTask getTutorialTask() {
        return tutorialTask;
    }

    public void getTutorialTimeTask(String tutorialName, String name) {
        getTutorialTask().tutorialTimeTask(tutorialName, name);
    }
}