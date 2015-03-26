package pw.hwk.tutorial.data;

import pw.hwk.tutorial.*;
import pw.hwk.tutorial.enums.CommandType;
import pw.hwk.tutorial.enums.MessageType;
import pw.hwk.tutorial.enums.ViewType;
import pw.hwk.tutorial.util.TutorialUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private Map<String, PlayerData> playerDataMap = new HashMap<>();
    private static Caching instance;
    private Map<UUID, Boolean> allowedTeleports = new HashMap<>();
    private Map<UUID, GameMode> gameModes = new HashMap<>();

    public void casheAllData() {
        if (DataLoading.getDataLoading().getData().getString("tutorials") == null) {
            return;
        }
        for (String tutorialName : DataLoading.getDataLoading().getData().getConfigurationSection("tutorials").getKeys(false)) {
            this.tutorialNames.add(tutorialName.toLowerCase());
            Map<Integer, TutorialView> tutorialViews = new HashMap<>();
            if (DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." + tutorialName + ".views") != null) {
                for (String vID : DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." + tutorialName + ".views").getKeys(false)) {
                    int viewID = Integer.parseInt(vID);
                    MessageType messageType = MessageType.valueOf(DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".views." + viewID + ".messagetype", "META"));
                    TutorialView view = new TutorialView(DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".views." + viewID + ".message", "No message written"), TutorialUtils.getTutorialUtils().getLocation(tutorialName, viewID), messageType);
                    tutorialViews.put(viewID, view);
                }
            }

            ViewType viewType;
            try {
                viewType = ViewType.valueOf(DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".viewtype", "CLICK"));
            } catch (IllegalArgumentException e) {
                viewType = ViewType.CLICK;
            }

            String timeLengthS = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".timelength", "10");
            int timeLength = Integer.parseInt(timeLengthS);
            String endMessage = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".endmessage", "Sample end message");

            Material item;
            try {
                item = Material.matchMaterial(DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".item", "STICK"));
                if (item == null) {
                    throw new NullPointerException();
                }
            } catch (NullPointerException e) {
                item = Material.STICK;
            }

            String command = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".endcommand", "");

            CommandType commandType;
            try {
                commandType = CommandType.valueOf(DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".endcommandtype", "NONE"));
            } catch (IllegalArgumentException e) {
                commandType = CommandType.NONE;
            }

            GameMode gm;
            try {
                gm = GameMode.valueOf(DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".gamemode", "SPECTATOR"));
            } catch (IllegalArgumentException e) {
                gm = GameMode.SPECTATOR;
            }

            Tutorial tutorial = new Tutorial(tutorialName, tutorialViews, viewType, timeLength, endMessage, item, command, commandType, gm);
            Setters.getSetters().addTutorial(tutorialName, tutorial);
        }
    }

    public void cachePlayerData() {
        if (DataLoading.getDataLoading().getPlayerData().getString("players") == null) {
            return;
        }
        for (String uuid : DataLoading.getDataLoading().getPlayerData().getConfigurationSection("players").getKeys(false)) {
            UUID playerUUID = UUID.fromString(uuid);
            HashMap<String, MapPlayerTutorial> playerTutorials = new HashMap<String, MapPlayerTutorial>();
            if (DataLoading.getDataLoading().getPlayerData().getConfigurationSection("players." + uuid + ".tutorials") != null) {
                for (String playerTutorial : DataLoading.getDataLoading().getPlayerData().getConfigurationSection("players." + uuid + ".tutorials").getKeys(false)) {
                    boolean seen = Boolean.valueOf(DataLoading.getDataLoading().getPlayerData().getString("players." + uuid + ".tutorials." + playerTutorial));
                    MapPlayerTutorial mapPlayerTutorial = new MapPlayerTutorial(playerTutorial, seen);
                    playerTutorials.put(playerTutorial, mapPlayerTutorial);
                }
            }
            PlayerData playerData = new PlayerData(playerTutorials);
            this.playerDataMap.put(plugin.getServer().getOfflinePlayer(playerUUID).getName(), playerData);
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

    public Map<String, PlayerData> playerDataMap() {
        return this.playerDataMap;
    }

    public List<String> playerInTutorial() {
        return playerInTutorial;
    }

    public Map<String, UUID> getResponse() {
        return this.response;
    }

    public boolean canTeleport(UUID uuid) {
        return allowedTeleports.get(uuid);
    }

    public void setTeleport(UUID uuid, boolean value) {
        allowedTeleports.put(uuid, value);
    }

    public void cacheConfigs() {
        TutorialConfigs configOptions = new TutorialConfigs(plugin.getConfig().getBoolean("auto-update"), plugin.getConfig().getString("sign"), plugin.getConfig().getBoolean("first_join"), plugin.getConfig().getString("first_join_tutorial"), plugin.getConfig().getBoolean("rewards"), plugin.getConfig().getBoolean("exp_countdown"), plugin.getConfig().getBoolean("view_money"), plugin.getConfig().getBoolean("view_exp"), plugin.getConfig().getBoolean("tutorial_money"), plugin.getConfig().getBoolean("tutorial_exp"), Double.valueOf(plugin.getConfig().getString("per_tutorial_money")), Integer.valueOf(plugin.getConfig().getString("per_tutorial_exp")), Integer.valueOf(plugin.getConfig().getString("per_view_exp")), Double.valueOf(plugin.getConfig().getString("per_view_money")));
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
        this.playerDataMap().clear();
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

    public void setGameMode(UUID uniqueId, GameMode gameMode) {
        gameModes.put(uniqueId, gameMode);
    }

    public GameMode getGameMode(UUID uniqueId) {
        return gameModes.get(uniqueId);
    }
}
