
package io.snw.tutorial.data;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.Tutorial;

public class Setters {

    ServerTutorial plugin;
    
    public Setters(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void addCurrentTutorial(String name, String tutorialName) {
        plugin.caching().currentTutorial().put(name, tutorialName);
    }

    public void addTutorial(String tutorialName, Tutorial tutorial) {
        plugin.caching().tutorial().put(tutorialName, tutorial);
    }

    public void addToTutorial(String name) {
        plugin.caching().playerInTutorial().add(name);
    }
}
