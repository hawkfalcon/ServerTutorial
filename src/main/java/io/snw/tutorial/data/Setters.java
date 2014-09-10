
package io.snw.tutorial.data;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.Tutorial;

public class Setters {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private static Setters instance;

    public void addCurrentTutorial(String name, String tutorialName) {
        Caching.getCaching().currentTutorial().put(name, tutorialName);
    }

    public void addTutorial(String tutorialName, Tutorial tutorial) {
        Caching.getCaching().tutorial().put(tutorialName, tutorial);
    }

    public void addToTutorial(String name) {
        Caching.getCaching().playerInTutorial().add(name);
    }
    
    public static Setters getSetters() {
        if (instance == null) {
            instance = new Setters();
        }
        return instance;
    }
}
