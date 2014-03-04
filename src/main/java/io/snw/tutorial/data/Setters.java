
package io.snw.tutorial.data;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.Tutorial;

public class Setters {

    ServerTutorial plugin;
    
    public Setters(ServerTutorial plugin) {
        this.plugin = plugin;
    }
    
    private Caching cache;

    public void addCurrentTutorial(String name, String tutorialName) {
        this.cache.currentTutorial().put(name, tutorialName);
    }
    
    public void addTutorial(String tutorialName, Tutorial tutorial) {
        this.cache.tutorial().put(tutorialName, tutorial);
    }
    
    public void addToTutorial(String name) {
        this.cache.playerInTutorial().add(name);
    }    
}
