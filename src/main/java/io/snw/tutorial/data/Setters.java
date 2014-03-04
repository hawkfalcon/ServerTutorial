
package io.snw.tutorial.data;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.Tutorial;

public class Setters {

    ServerTutorial plugin;
    private Caching cache = new Caching(plugin);
    
    public Setters(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void addCurrentTutorial(String name, String tutorialName) {
        cache.currentTutorial().put(name, tutorialName);
    }
    
    public void addTutorial(String tutorialName, Tutorial tutorial) {
        cache.tutorial().put(tutorialName, tutorial);
    }
    
    public void addToTutorial(String name) {
        cache.playerInTutorial().add(name);
    }    
}
