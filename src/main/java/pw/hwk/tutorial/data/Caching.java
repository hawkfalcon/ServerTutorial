package pw.hwk.tutorial.data;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.ServerTutorial;
import pw.hwk.tutorial.Tutorial;
import pw.hwk.tutorial.TutorialConfigs;
import pw.hwk.tutorial.TutorialView;
import pw.hwk.tutorial.enums.CommandType;
import pw.hwk.tutorial.enums.MessageType;
import pw.hwk.tutorial.enums.ViewType;
import pw.hwk.tutorial.util.TutorialUtils;

import java.util.*;

public class Caching {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private List<String> tutorialNames = new ArrayList<>();
    private Map<String, Tutorial> tutorials = new HashMap<>();
    private Map<String, TutorialConfigs> configs = new HashMap<>();
    //player name, tutorial name
    private Map<String, String> currentTutorial = new HashMap<>();
    private Map<String, Integer> currentTutorialView = new HashMap<>();
    private ArrayList<String> playerInTutorial = new ArrayList<>();
    private Map<String, UUID> response = new HashMap<>();
    private Map<UUID, Set<String>> seenTutorials = new HashMap<>();
    private static Caching instance;
    private Map<UUID, Boolean> allowedTeleports = new HashMap<>();

    public void casheAllData() {
        if (DataLoading.getDataLoading().getData().getString("tutorials") == null) {
            return;
        }
        for (String tutorialName : DataLoading.getDataLoading().getData().getConfigurationSection("tutorials")
                .getKeys(false)) {
            this.tutorialNames.add(tutorialName.toLowerCase());
            Map<Integer, TutorialView> tutorialViews = new HashMap<>();
            if (DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." + tutorialName + "" +
                    ".views") != null) {
                for (String vID : DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." +
                        tutorialName + ".views").getKeys(false)) {
                    int viewID = Integer.parseInt(vID);
                    MessageType messageType = MessageType.valueOf(DataLoading.getDataLoading().getData().getString
                            ("tutorials." + tutorialName + ".views." + viewID + ".messagetype", "META"));
                    TutorialView view = new TutorialView(DataLoading.getDataLoading().getData().getString("tutorials" +
                            "." + tutorialName + ".views." + viewID + ".message", "No message written"),
                            TutorialUtils.getTutorialUtils().getLocation(tutorialName, viewID), messageType);
                    tutorialViews.put(viewID, view);
                }
            }

            ViewType viewType;
            try {
                viewType = ViewType.valueOf(DataLoading.getDataLoading().getData().getString("tutorials." +
                        tutorialName + ".viewtype", "CLICK"));
            } catch (IllegalArgumentException e) {
                viewType = ViewType.CLICK;
            }

            String timeLengthS = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + "" +
                    ".timelength", "10");
            int timeLength = Integer.parseInt(timeLengthS);
            String endMessage = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + "" +
                    ".endmessage", "Sample end message");

            String command = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + "" +
                    ".endcommand", "");

            CommandType commandType;
            try {
                commandType = CommandType.valueOf(DataLoading.getDataLoading().getData().getString("tutorials." +
                        tutorialName + ".endcommandtype", "NONE"));
            } catch (IllegalArgumentException e) {
                commandType = CommandType.NONE;
            }

            GameMode gm;
            try {
                gm = GameMode.valueOf(DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName +
                        ".gamemode", "SPECTATOR"));
            } catch (IllegalArgumentException e) {
                gm = GameMode.SPECTATOR;
            }

            Tutorial tutorial = new Tutorial(tutorialName, tutorialViews, viewType, timeLength, endMessage, command,
                    commandType, gm);
            TutorialManager.getManager().addTutorial(tutorialName, tutorial);
        }
    }

    public void cachePlayerData() {
        if (DataLoading.getDataLoading().getPlayerData().getString("players") == null) {
            return;
        }
        for (String uuid : DataLoading.getDataLoading().getPlayerData().getConfigurationSection("players").getKeys
                (false)) {
            UUID playerUUID = UUID.fromString(uuid);
            if (DataLoading.getDataLoading().getPlayerData().getConfigurationSection("players." + uuid + "" +
                    ".tutorials") != null) {
                Set<String> tutorials = DataLoading.getDataLoading().getPlayerData().getConfigurationSection("players" +
                        "." + uuid + ".tutorials").getKeys(false);
                seenTutorials.put(playerUUID, tutorials);
            }
        }
    }

    public List<String> tutorialNames() {
        return this.tutorialNames;
    }

    public Map<String, Tutorial> tutorial() {
        return this.tutorials;
    }

    public Map<String, String> currentTutorial() {
        return this.currentTutorial;
    }

    public Map<String, Integer> currentTutorialView() {
        return this.currentTutorialView;
    }

    public Map<String, TutorialConfigs> configs() {
        return this.configs;
    }

    public Map<UUID, Set<String>> seenTutorials() {
        return this.seenTutorials;
    }

    public List<String> playerInTutorial() {
        return playerInTutorial;
    }

    public Map<String, UUID> getResponse() {
        return this.response;
    }

    public boolean canTeleport(Player player) {
        return allowedTeleports.get(getUUID(player));
    }

    public void setTeleport(Player player, boolean value) {
        allowedTeleports.put(getUUID(player), value);
    }

    public void cacheConfigs() {
        TutorialConfigs configOptions = new TutorialConfigs(plugin.getConfig().getBoolean("auto-update"), plugin
                .getConfig().getString("sign"), plugin.getConfig().getBoolean("first_join"), plugin.getConfig()
                .getString("first_join_tutorial"), plugin.getConfig().getBoolean("rewards"), plugin.getConfig()
                .getBoolean("exp_countdown"), plugin.getConfig().getBoolean("view_money"), plugin.getConfig()
                .getBoolean("view_exp"), plugin.getConfig().getBoolean("tutorial_money"), plugin.getConfig()
                .getBoolean("tutorial_exp"), Double.valueOf(plugin.getConfig().getString("per_tutorial_money")),
                Integer.valueOf(plugin.getConfig().getString("per_tutorial_exp")), Integer.valueOf(plugin.getConfig()
                .getString("per_view_exp")), Double.valueOf(plugin.getConfig().getString("per_view_money")), plugin.getConfig().getBoolean("check_gamemode"));
        this.addConfig(configOptions);
    }

    public void addConfig(TutorialConfigs configs) {
        this.configs.put("config", configs);
    }

    public void reCasheTutorials() {
        this.tutorials.clear();
        this.tutorialNames.clear();
        casheAllData();
    }

    public void reCacheConfigs() {
        this.configs.clear();
        cacheConfigs();
    }

    public void reCachePlayerData() {
        this.seenTutorials().clear();
        cachePlayerData();
    }

    public UUID getUUID(Player player) {
        UUID uuid;
        if (plugin.getServer().getOnlineMode()) {
            uuid = player.getUniqueId();
        } else {
            uuid = this.getResponse().get(player.getName());
        }

        if (uuid == null) {
            uuid = player.getUniqueId();
        }

        return uuid;
    }

    public static Caching getCaching() {
        if (instance == null) {
            instance = new Caching();
        }
        return instance;
    }
}
