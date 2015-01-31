package io.snw.tutorial.data;

import io.snw.tutorial.MapPlayerTutorial;
import io.snw.tutorial.PlayerData;
import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.Tutorial;
import io.snw.tutorial.TutorialConfigs;
import io.snw.tutorial.TutorialView;
import io.snw.tutorial.enums.CommandType;
import io.snw.tutorial.enums.MessageType;
import io.snw.tutorial.enums.ViewType;
import io.snw.tutorial.util.TutorialUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Caching {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private ArrayList<String> tutorialNames = new ArrayList<String>();
    private HashMap<String, Tutorial> tutorials = new HashMap<String, Tutorial>();
    private HashMap<String, TutorialConfigs> configs = new HashMap<String, TutorialConfigs>();
    //player name, tutorial name
    private HashMap<String, String> currentTutorial = new HashMap<String, String>();
    private HashMap<String, Integer> currentTutorialView = new HashMap<String, Integer>();
    private ArrayList<String> playerInTutorial = new ArrayList<String>();
    private Map<String, UUID> response = new HashMap<String, UUID>();
    private HashMap<String, PlayerData> playerDataMap = new HashMap<String, PlayerData>();
    private static Caching instance;
    private HashMap<UUID, Boolean> allowedTeleports = new HashMap<UUID, Boolean>();
    private HashMap<UUID, GameMode> gameModes = new HashMap<UUID, GameMode>();

    public void casheAllData() {
        if (DataLoading.getDataLoading().getData().getString("tutorials") == null) {
            return;
        }
        for (String tutorialName : DataLoading.getDataLoading().getData().getConfigurationSection("tutorials").getKeys(false)) {
            this.tutorialNames.add(tutorialName.toLowerCase());
            HashMap<Integer, TutorialView> tutorialViews = new HashMap<Integer, TutorialView>();
            if (DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." + tutorialName + ".views") != null) {
                for (String vID : DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." + tutorialName + ".views")
                        .getKeys(false)) {
                    int viewID = Integer.parseInt(vID);
                    MessageType
                            messageType =
                            MessageType.valueOf(DataLoading.getDataLoading().getData()
                                                        .getString("tutorials." + tutorialName + ".views." + viewID + ".messagetype", "META"));
                    TutorialView
                            view =
                            new TutorialView(viewID, DataLoading.getDataLoading().getData()
                                    .getString("tutorials." + tutorialName + ".views." + viewID + ".message", "No message written"),
                                             TutorialUtils.getTutorialUtils().getLocation(tutorialName, viewID), messageType);
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
                if(item == null) {
                    throw new NullPointerException();
                }
            } catch (Exception e) {
                item = Material.STICK;
            }

            String command = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".command", "");

            CommandType commandType;
            try {
                commandType = CommandType.valueOf(DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".commandtype", "NONE"));
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
            boolean seenOnServer = Boolean.valueOf(DataLoading.getDataLoading().getData().getString("players." + uuid + ".seen"));
            HashMap<String, MapPlayerTutorial> playerTutorials = new HashMap<String, MapPlayerTutorial>();
            if (DataLoading.getDataLoading().getPlayerData().getConfigurationSection("players." + uuid + ".tutorials") != null) {
                for (String playerTutorial : DataLoading.getDataLoading().getPlayerData().getConfigurationSection("players." + uuid + ".tutorials")
                        .getKeys(false)) {
                    boolean
                            seen =
                            Boolean.valueOf(
                                    DataLoading.getDataLoading().getPlayerData().getString("players." + uuid + ".tutorials." + playerTutorial));
                    MapPlayerTutorial mapPlayerTutorial = new MapPlayerTutorial(playerTutorial, seen);
                    playerTutorials.put(playerTutorial, mapPlayerTutorial);
                }
            }
            PlayerData playerData = new PlayerData(playerUUID, seenOnServer, playerTutorials);
            this.playerDataMap.put(plugin.getServer().getOfflinePlayer(playerUUID).getName(), playerData);
        }
    }

    public ArrayList<String> tutorialNames() {
        return this.tutorialNames;
    }

    public HashMap<String, Tutorial> tutorial() {
        return this.tutorials;
    }

    public HashMap<String, String> currentTutorial() {
        return this.currentTutorial;
    }

    public HashMap<String, Integer> currentTutorialView() {
        return this.currentTutorialView;
    }

    public HashMap<String, TutorialConfigs> configs() {
        return this.configs;
    }

    public HashMap<String, PlayerData> playerDataMap() {
        return this.playerDataMap;
    }

    public ArrayList<String> playerInTutorial() {
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
        TutorialConfigs
                configOptions =
                new TutorialConfigs(plugin.getConfig().getBoolean("auto-update"), plugin.getConfig().getBoolean("metrics"),
                                    plugin.getConfig().getString("sign"), plugin.getConfig().getBoolean("first_join"),
                                    plugin.getConfig().getString("first_join_tutorial"),
                                    plugin.getConfig().getBoolean("rewards"), plugin.getConfig().getBoolean("exp_countdown"),
                                    plugin.getConfig().getBoolean("view_money"), plugin.getConfig().getBoolean(
                        "view_exp"), plugin.getConfig().getBoolean("tutorial_money"), plugin.getConfig().getBoolean("tutorial_exp"),
                                    Double.valueOf(plugin.getConfig().getString("per_tutorial_money")), Integer.valueOf(
                        plugin.getConfig().getString("per_tutorial_exp")), Integer.valueOf(plugin.getConfig().getString("per_view_exp")),
                                    Double.valueOf(
                                            plugin.getConfig().getString("per_view_money")));
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
