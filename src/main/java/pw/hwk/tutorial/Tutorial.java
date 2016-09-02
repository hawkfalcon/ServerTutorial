package pw.hwk.tutorial;

import org.bukkit.GameMode;
import pw.hwk.tutorial.enums.CommandType;
import pw.hwk.tutorial.enums.ViewType;

import java.util.Map;

public class Tutorial {

    private final String name;
    private final Map<Integer, TutorialView> tutorialViews;
    private final ViewType viewType;
    private final int timeLength;
    private final String endMessage;
    private final String command;
    private final CommandType commandType;
    private GameMode gamemode;

    public Tutorial(String name, Map<Integer, TutorialView> tutorialViews, ViewType viewType, int timeLength, String
            endMessage, String command, CommandType commandType, GameMode gamemode) {
        this.name = name;
        this.tutorialViews = tutorialViews;
        this.viewType = viewType;
        this.timeLength = timeLength;
        this.endMessage = endMessage;
        this.command = command;
        this.commandType = commandType;
        this.gamemode = gamemode;
    }

    public String getName() {
        return this.name;
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
