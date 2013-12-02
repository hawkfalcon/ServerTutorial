package io.snw.tutorial;

import io.snw.tutorial.enums.ViewType;
import java.util.HashMap;
import org.bukkit.Material;

public class Tutorial {
    private final String name;
    private final HashMap<Integer, TutorialView> tutorialViews;
    private final ViewType viewType;
    private final String endMessage;
    private final Material item;

    public Tutorial(String name, HashMap<Integer, TutorialView> tutorialViews, ViewType viewType, String endMessage, Material item) {
        this.name = name;
        this.tutorialViews = tutorialViews;
        this.viewType = viewType;
        this.endMessage = endMessage;
        this.item = item;
    }

    public String getName() {
        return this.name;
    }

    public HashMap<Integer, TutorialView> getViews() {
        return this.tutorialViews;
    }

    public TutorialView getView(int viewID) {
        return this.tutorialViews.get(viewID);
    }

    public int getTotalViews() {
        return this.tutorialViews.size();
    }

    public ViewType getViewType() {
        return this.viewType;
    }

    public String getEndMessage() {
        return this.endMessage;
    }

    public Material getItem() {
        return this.item;
    }
}
