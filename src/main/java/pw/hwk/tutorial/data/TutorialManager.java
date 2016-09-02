package pw.hwk.tutorial.data;

import pw.hwk.tutorial.Tutorial;
import pw.hwk.tutorial.TutorialConfigs;
import pw.hwk.tutorial.TutorialView;
import pw.hwk.tutorial.util.TutorialTask;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TutorialManager {

    private static TutorialManager instance;

    public void addCurrentTutorial(String name, String tutorialName) {
        Caching.getCaching().currentTutorial().put(name, tutorialName);
    }

    public void addTutorial(String tutorialName, Tutorial tutorial) {
        Caching.getCaching().tutorial().put(tutorialName, tutorial);
    }

    public void addToTutorial(String name) {
        Caching.getCaching().playerInTutorial().add(name);
    }


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
        return Caching.getCaching().tutorial().get(this.getCurrentTutorial(name).getName()).getView(getCurrentView
                (name));
    }

    public TutorialView getTutorialView(String tutorialName, int id) {
        return Caching.getCaching().tutorial().get(this.getTutorial(tutorialName).getName()).getView(id);
    }

    public TutorialConfigs getConfigs() {
        return Caching.getCaching().configs().get("config");
    }

    public Set<String> getSeenTutorials(UUID uuid) {
        return Caching.getCaching().seenTutorials().get(uuid);
    }

    public void setSeenTutorial(UUID uuid, String tutorial) {
        Caching.getCaching().seenTutorials().get(uuid).add(tutorial);
    }

    public List<String> getAllTutorials() {
        return Caching.getCaching().tutorialNames();
    }

    public List<String> getAllInTutorial() {
        return Caching.getCaching().playerInTutorial();
    }

    public int getCurrentView(String name) {
        return Caching.getCaching().currentTutorialView().get(name);
    }

    public boolean isInTutorial(String name) {
        if (getCurrentTutorial(name) == null && Caching.getCaching().playerInTutorial().contains(name)) {
            Caching.getCaching().playerInTutorial().remove(name);
            return false;
        }
        return Caching.getCaching().playerInTutorial().contains(name);
    }

    public void getTutorialTimeTask(String tutorialName, String name) {
        TutorialTask.getTutorialTask().tutorialTimeTask(tutorialName, name);
    }

    public static TutorialManager getManager() {
        if (instance == null) {
            instance = new TutorialManager();
        }
        return instance;
    }
}
