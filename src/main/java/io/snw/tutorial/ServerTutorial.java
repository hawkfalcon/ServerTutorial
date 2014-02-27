package io.snw.tutorial;

import io.snw.tutorial.api.StartTutorialEvent;
import io.snw.tutorial.api.ViewSwitchEvent;
import io.snw.tutorial.commands.TutorialMainCommand;
import io.snw.tutorial.enums.MessageType;
import io.snw.tutorial.enums.ViewType;
import io.snw.tutorial.util.EndTutorial;
import io.snw.tutorial.util.Metrics;
import io.snw.tutorial.util.Updater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerTutorial extends JavaPlugin {

    static boolean UPDATE;
    static String NEWVERSION;

    //player name, id
    private HashMap<String, Integer> currentTutorialView = new HashMap<String, Integer>();
    //player name, tutorial name
    private HashMap<String, String> currentTutorial = new HashMap<String, String>();
    //tutorial name, tutorial object
    private HashMap<String, Tutorial> tutorials = new HashMap<String, Tutorial>();
    private ArrayList<String> tutorialNames = new ArrayList<String>();
    private HashMap<String, Location> startLoc = new HashMap<String, Location>();
    private HashMap<String, ItemStack[]> inventories = new HashMap<String, ItemStack[]>();
    private HashMap<String, Boolean> flight = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> godmode = new HashMap<String, Boolean>();
    private ArrayList<String> playerInTutorial = new ArrayList<String>();

    private TutorialUtils tutorialUtils = new TutorialUtils(this);
    private TutorialTask tutorialTask = new TutorialTask(this);
    private CreateTutorial createTutorial = new CreateTutorial(this);
    private ViewConversation viewConversation = new ViewConversation(this);
    private EndTutorial endTutorial = new EndTutorial(this);

    private File dataFile;
    private YamlConfiguration data;
    private File playerDataFile;
    private YamlConfiguration playerData;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new TutorialListener(this), this);
        this.getCommand("tutorial").setExecutor(new TutorialMainCommand(this));
        this.saveDefaultConfig();
        this.loadData();
        this.casheAllData();
        this.getTutorialTask().tutorialTask();
        this.startMetrics();
        this.checkUpdate();
    }

    private void startMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {
            getLogger().warning("Failed to load metrics :(");
        }
    }

    private void checkUpdate() {
        if (getConfig().get("auto-update") == null) {
            getConfig().set("auto-update", true);
            saveConfig();
        }
        if (getConfig().getBoolean("auto-update")) {
            final ServerTutorial plugin = this;
            final File file = this.getFile();
            final Updater.UpdateType updateType = Updater.UpdateType.DEFAULT;
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    Updater updater = new Updater(plugin, 69090, file, updateType, false);
                    ServerTutorial.UPDATE = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
                    ServerTutorial.NEWVERSION = updater.getLatestName();
                    if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
                        getLogger().log(Level.INFO, "Successfully updated ServerTutorial to version {0} for next restart!", updater.getLatestName());
                    }
                }
            });
        }
    }

    private void loadData() {
        File f = new File(getDataFolder(), "data.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataFile = f;
        data = YamlConfiguration.loadConfiguration(f);
        if (getConfig().contains("tutorials")) {
            ConfigurationSection section = getConfig().getConfigurationSection("tutorials");
            data.set("tutorials", section);
            saveData();
            getConfig().set("tutorials", null);
            saveConfig();
        }
    }
    
    private void loadPlayerData() {
        File f = new File(getDataFolder(), "players.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playerDataFile = f;
        playerData = YamlConfiguration.loadConfiguration(f);
    }
    
    public YamlConfiguration getPlayerData() {
        return this.playerData;
    }

    public YamlConfiguration getData() {
        return this.data;
    }

    public void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException exception) {
            getLogger().warning("Failed to save data :(");
        }
    }
    
    public void savePlayerData() {
        try {
            data.save(playerDataFile);
        } catch (IOException e) {
            getLogger().warning("Failed to save player data :(");
        }
    }

    public void addTutorial(String tutorialName, Tutorial tutorial) {
        this.tutorials.put(tutorialName, tutorial);
    }

    public ArrayList<String> getAllTutorials() {
        return this.tutorialNames;
    }

    public Tutorial getCurrentTutorial(String name) {
        return this.tutorials.get(this.currentTutorial.get(name));
    }

    public void addCurrentTutorial(String name, String tutorialName) {
        this.currentTutorial.put(name, tutorialName);
    }

    /**
     * Setup all views from config
     */
    public void casheAllData() {
        if (this.data.getString("tutorials") == null) {
            return;
        }
        for (String tutorialName : this.data.getConfigurationSection("tutorials").getKeys(false)) {
            this.tutorialNames.add(tutorialName);
            HashMap<Integer, TutorialView> tutorialViews = new HashMap<Integer, TutorialView>();
            if (this.data.getConfigurationSection("tutorials." + tutorialName + ".views") != null) {
                for (String vID : this.data.getConfigurationSection("tutorials." + tutorialName + ".views").getKeys(false)) {
                    int viewID = Integer.parseInt(vID);
                    MessageType messageType = MessageType.valueOf(this.data.getString("tutorials." + tutorialName + ".views." + viewID + ".messagetype", "META"));
                    TutorialView view = new TutorialView(viewID, this.data.getString("tutorials." + tutorialName + ".views." + viewID + ".message", "No message written"), this.getTutorialUtils().getLocation(tutorialName, viewID), messageType);
                    tutorialViews.put(viewID, view);
                }
            }
            ViewType viewType = ViewType.valueOf(this.data.getString("tutorials." + tutorialName + ".viewtype", "CLICK"));
            String timeLengthS = this.data.getString("tutorials." + tutorialName + ".timelength", "10");
            int timeLength = Integer.parseInt(timeLengthS);
            String endMessage = this.data.getString("tutorials." + tutorialName + ".endmessage", "Sample end message");
            Material item = Material.matchMaterial(this.data.getString("tutorials." + tutorialName + ".item", "stick"));
            Tutorial tutorial = new Tutorial(tutorialName, tutorialViews, viewType, timeLength, endMessage, item);
            this.addTutorial(tutorialName, tutorial);
        }

    }

    public void reCasheTutorials() {
        this.tutorials.clear();
        this.tutorialNames.clear();
        casheAllData();
    }


    public void startTutorial(String tutorialName, Player player) {
        if (this.data.getConfigurationSection("tutorials") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a tutorial first! /tutorial create <message>");
            return;
        }
        if (this.getTutorial(tutorialName) == null) {
            player.sendMessage("Invalid tutorial");
            return;
        }
        if (this.data.getConfigurationSection("tutorials." + tutorialName + ".views") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a view first! /tutorial addview <tutorial name>");
            return;
        }
        String name = player.getName();
        this.startLoc.put(name, player.getLocation());
        this.addInventory(name, player.getInventory().getContents());
        this.addFlight(name, player.getAllowFlight());
        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);
        this.initializeCurrentView(name);
        this.addCurrentTutorial(name, tutorialName);
        this.addToTutorial(name);
        for (Player online : this.getServer().getOnlinePlayers()) {
            online.hidePlayer(player);
            player.hidePlayer(online);
        }
        this.getServer().getPlayerExact(name).teleport(this.getTutorialView(tutorialName, name).getLocation());
        if (this.getTutorial(tutorialName).getViewType() == ViewType.TIME) {
            this.getTutorialTimeTask(tutorialName, name);
        }
        this.getTutorialUtils().textUtils(player);
        StartTutorialEvent event = new StartTutorialEvent(player, this.getTutorial(tutorialName));
        this.getServer().getPluginManager().callEvent(event);
        if(this.playerData.get("players." + name) == null) {
            this.playerData.set("players." + name + ".seen", "true");
            this.playerData.set("players." + name + ".tutorials." + tutorialName, "true");
            this.savePlayerData();
        } else if(this.playerData.get("players." + name + ".tutorials." + tutorialName) == null) {
            this.playerData.set("players." + name + ".tutorials." + tutorialName, "true");
            this.saveData();
        }
    }

    public Tutorial getTutorial(String tutorialName) {
        return this.tutorials.get(tutorialName);
    }

    public void addToTutorial(String name) {
        this.playerInTutorial.add(name);
    }

    public void removeFromTutorial(String name) {
        this.playerInTutorial.remove(name);
        this.startLoc.remove(name);
        this.currentTutorial.remove(name);
        this.currentTutorialView.remove(name);
        this.flight.remove(name);
    }

    public boolean isInTutorial(String name) {
        return this.playerInTutorial.contains(name);
    }

    public ArrayList<String> getAllInTutorial() {
        return this.playerInTutorial;
    }

    public void initializeCurrentView(String name) {
        this.currentTutorialView.put(name, 1);
    }

    public void incrementCurrentView(String name) {
        TutorialView fromTutorialView = this.getTutorialView(name);
        this.currentTutorialView.put(name, getCurrentView(name) + 1);
        TutorialView toTutorialView = this.getTutorialView(name);
        ViewSwitchEvent event = new ViewSwitchEvent(Bukkit.getPlayerExact(name), fromTutorialView, toTutorialView);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public int getCurrentView(String name) {
        return this.currentTutorialView.get(name);
    }

    public TutorialView getTutorialView(String tutorialName, String name) {
        return this.tutorials.get(tutorialName).getView(getCurrentView(name));
    }

    public TutorialView getTutorialView(String name) {
        return this.tutorials.get(this.getCurrentTutorial(name).getName()).getView(getCurrentView(name));
    }

    public Location getFirstLoc(String name) {
        return this.startLoc.get(name);
    }

    public void cleanFirstLoc(String name) {
        this.startLoc.remove(name);
    }

    public ItemStack[] getInventory(String name) {
        return this.inventories.get(name);
    }

    public void addInventory(String name, ItemStack[] items) {
        this.inventories.put(name, items);
    }

    public void cleanInventory(String name) {
        this.inventories.remove(name);
    }

    public boolean getFlight(String name) {
        return this.flight.get(name);
    }

    public void addFlight(String name, boolean flight) {
        this.flight.put(name, flight);
    }
    
    public void removeFlight(String name) {
        this.flight.remove(name);
    }

    public TutorialUtils getTutorialUtils() {
        return tutorialUtils;
    }

    public TutorialTask getTutorialTask() {
        return tutorialTask;
    }

    public EndTutorial getEndTutorial() {
        return endTutorial;
    }


    public void getTutorialTimeTask(String tutorialName, String name) {
        tutorialTask.tutorialTimeTask(tutorialName, name);
    }

    public String tACC(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public CreateTutorial getCreateTutorial() {
        return createTutorial;
    }

    public ViewConversation getViewConversation() {
        return viewConversation;
    }
    
    public void removeTutorial(String tutorialName) {
        this.data.set("tutorials." + tutorialName, null);
        this.saveData();
        this.reCasheTutorials();
    }
    
    public void removeTutorialView(String tutorialName, int viewID) {
        int viewsCount = this.getTutorial(tutorialName).getTotalViews();
        this.getData().set("tutorials." + tutorialName + ".views." + viewID, null);
        this.saveData();
        if(viewsCount != viewID) {
            for(String vID : this.data.getConfigurationSection("tutorials." + tutorialName + ".views").getKeys(false)) {
                int currentID = Integer.parseInt(vID);
                int newViewID = Integer.parseInt(vID) - 1;
                String message = this.data.getString("tutorials." + tutorialName + ".views." + currentID + ".message");
                String messageType = this.data.getString("tutorials." + tutorialName + ".views." + currentID + ".messagetype");
                String location = this.data.getString("tutorials." + tutorialName + ".views." + currentID + ".location");
                this.data.set("tutorials." + tutorialName + ".views." + newViewID + ".message", message);
                this.data.set("tutorials." + tutorialName + ".views." + newViewID + ".messagetype", messageType);
                this.data.set("tutorials." + tutorialName + ".views." + newViewID + ".location" , location);
                this.data.set("tutorials." + tutorialName + ".views." + currentID, null);
                }
        }
        this.saveData();
        this.reCasheTutorials();
    }
}
