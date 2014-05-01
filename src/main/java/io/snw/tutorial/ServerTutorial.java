package io.snw.tutorial;

import io.snw.tutorial.util.TutorialUtils;
import io.snw.tutorial.api.StartTutorialEvent;
import io.snw.tutorial.api.ViewSwitchEvent;
import io.snw.tutorial.commands.TutorialMainCommand;
import io.snw.tutorial.data.Caching;
import io.snw.tutorial.data.DataLoading;
import io.snw.tutorial.data.Getters;
import io.snw.tutorial.data.Setters;
import io.snw.tutorial.enums.ViewType;
import io.snw.tutorial.util.Metrics;
import io.snw.tutorial.util.TutorialTask;
import io.snw.tutorial.util.Updater;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerTutorial extends JavaPlugin {

    static boolean UPDATE;
    static String NEWVERSION;


    private HashMap<String, Location> startLoc = new HashMap<String, Location>();
    private HashMap<String, ItemStack[]> inventories = new HashMap<String, ItemStack[]>();
    private HashMap<String, Boolean> flight = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> godmode = new HashMap<String, Boolean>();
    private TutorialUtils tutorialUtils = new TutorialUtils(this);
    private CreateTutorial createTutorial = new CreateTutorial(this);
    private ViewConversation viewConversation = new ViewConversation(this);
    private EndTutorial endTutorial = new EndTutorial(this);
    private DataLoading dataLoad = new DataLoading(this);
    private Caching cache = new Caching(this);
    private Getters getters = new Getters(this);
    private Setters setters = new Setters(this);
    private TutorialTask tutorialTask = new TutorialTask(this);

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new TutorialListener(this), this);
        this.getCommand("tutorial").setExecutor(new TutorialMainCommand(this));
        this.saveDefaultConfig();
        this.dataLoad().loadData();
        this.dataLoad().loadPlayerData();
        this.caching().casheAllData();
        this.caching().cacheConfigs();
        this.tutorialTask().tutorialTask();
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
            this.caching().reCacheConfigs();
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

    public void startTutorial(String tutorialName, Player player) {
        if (this.dataLoad().getData().getConfigurationSection("tutorials") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a tutorial first! /tutorial create <message>");
            return;
        }
        if (this.getters().getTutorial(tutorialName) == null) {
            player.sendMessage("Invalid tutorial");
            return;
        }
        if (this.dataLoad().getData().getConfigurationSection("tutorials." + tutorialName + ".views") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a view first! /tutorial addview <tutorial name>");
            return;
        }
        String name = this.getServer().getPlayer(player.getUniqueId()).getName();
        this.startLoc.put(name, player.getLocation());
        this.addInventory(name, player.getInventory().getContents());
        this.addFlight(name, player.getAllowFlight());
        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);
        this.initializeCurrentView(name);
        this.setters().addCurrentTutorial(name, tutorialName);
        this.setters().addToTutorial(name);
        for (Player online : this.getServer().getOnlinePlayers()) {
            online.hidePlayer(player);
            player.hidePlayer(online);
        }
        this.getServer().getPlayerExact(name).teleport(this.getters().getTutorialView(tutorialName, name).getLocation());
        if (this.getters().getTutorial(tutorialName).getViewType() == ViewType.TIME) {
            this.getters().getTutorialTimeTask(tutorialName, name);
        }
        this.getTutorialUtils().textUtils(player);
        StartTutorialEvent event = new StartTutorialEvent(player, this.getters().getTutorial(tutorialName));
        this.getServer().getPluginManager().callEvent(event);
        if(this.dataLoad().getPlayerData().get("players." + name) == null) {
            this.dataLoad().getPlayerData().set("players." + name + ".seen", "true");
           this.dataLoad().getPlayerData().set("players." + name + ".tutorials." + tutorialName, "true");
            this.dataLoad().savePlayerData();
        } else if(this.dataLoad().getPlayerData().get("players." + name + ".tutorials." + tutorialName) == null) {
            this.dataLoad().getPlayerData().set("players." + name + ".tutorials." + tutorialName, "true");
            this.dataLoad().savePlayerData();
        }
    }

    public void removeFromTutorial(String name) {
        this.caching().playerInTutorial().remove(name);
        this.startLoc.remove(name);
        this.caching().currentTutorial().remove(name);
        this.caching().currentTutorialView().remove(name);
        this.flight.remove(name);
    }

    public void initializeCurrentView(String name) {
        this.caching().currentTutorialView().put(name, 1);
    }

    public void incrementCurrentView(String name) {
        TutorialView fromTutorialView = this.getters().getTutorialView(name);
        this.caching().currentTutorialView().put(name, this.getters().getCurrentView(name) + 1);
        TutorialView toTutorialView = this.getters().getTutorialView(name);
        ViewSwitchEvent event = new ViewSwitchEvent(Bukkit.getPlayerExact(name), fromTutorialView, toTutorialView);
        Bukkit.getServer().getPluginManager().callEvent(event);
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

    public EndTutorial getEndTutorial() {
        return endTutorial;
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
        this.dataLoad().getData().set("tutorials." + tutorialName, null);
        this.dataLoad().saveData();
        this.caching().reCasheTutorials();
    }

    public void removeTutorialView(String tutorialName, int viewID) {
        int viewsCount = this.getters().getTutorial(tutorialName).getTotalViews();
        this.dataLoad().getData().set("tutorials." + tutorialName + ".views." + viewID, null);
        this.dataLoad().saveData();
        if(viewsCount != viewID) {
            for(String vID : this.dataLoad().getData().getConfigurationSection("tutorials." + tutorialName + ".views").getKeys(false)) {
                int currentID = Integer.parseInt(vID);
                int newViewID = Integer.parseInt(vID) - 1;
                String message = this.dataLoad().getData().getString("tutorials." + tutorialName + ".views." + currentID + ".message");
                String messageType = this.dataLoad().getData().getString("tutorials." + tutorialName + ".views." + currentID + ".messagetype");
                String location = this.dataLoad().getData().getString("tutorials." + tutorialName + ".views." + currentID + ".location");
                this.dataLoad().getData().set("tutorials." + tutorialName + ".views." + newViewID + ".message", message);
                this.dataLoad().getData().set("tutorials." + tutorialName + ".views." + newViewID + ".messagetype", messageType);
                this.dataLoad().getData().set("tutorials." + tutorialName + ".views." + newViewID + ".location" , location);
                this.dataLoad().getData().set("tutorials." + tutorialName + ".views." + currentID, null);
                }
        }
        this.dataLoad().saveData();
        this.caching().reCasheTutorials();
    }

    public Getters getters() {
        return this.getters;
    }

    public Setters setters() {
        return this.setters;
    }

    public Caching caching() {
        return this.cache;
    }

    public DataLoading dataLoad() {
        return this.dataLoad;
    }

    public TutorialTask tutorialTask() {
        return this.tutorialTask;
    }
}
