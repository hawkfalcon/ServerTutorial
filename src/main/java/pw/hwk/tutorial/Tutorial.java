package pw.hwk.tutorial;

import pw.hwk.tutorial.enums.CommandType;
import pw.hwk.tutorial.enums.ViewType;
import org.bukkit.GameMode;
import org.bukkit.Material;

import java.util.HashMap;


public class Tutorial {

    private final String name;
    private final HashMap<Integer, TutorialView> tutorialViews;
    private final ViewType viewType;
    private final int timeLength;
    private final String endMessage;
    private final Material item;
    private final String command;
    private final CommandType commandType;
    private GameMode gamemode;

    public Tutorial(String name, HashMap<Integer, TutorialView> tutorialViews, ViewType viewType, int timeLength, String endMessage, Material item, String command, CommandType commandType, GameMode gamemode) {
        this.name = name;
        this.tutorialViews = tutorialViews;
        this.viewType = viewType;
        this.timeLength = timeLength;
        this.endMessage = endMessage;
        this.item = item;
        this.command = command;
        this.commandType = commandType;
        this.gamemode = gamemode;
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

    public int getTimeLength() {
        return this.timeLength;
    }

    public String getEndMessage() {
        return this.endMessage;
    }

    public Material getItem() {
        return this.item;
    }

    public String getCommand() {
        return command;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public GameMode getGameMode() {
        return gamemode;
    }
}
