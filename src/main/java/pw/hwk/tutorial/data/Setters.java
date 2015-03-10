package pw.hwk.tutorial.data;

import pw.hwk.tutorial.Tutorial;

public class Setters {

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
