package io.snw.tutorial;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerTutorial extends JavaPlugin {

    private HashMap<String, Integer> currentTutorialView = new HashMap<String, Integer>();
    private HashMap<Integer, TutorialView> tutorialViews = new HashMap<Integer, TutorialView>();
    private HashMap<String, Location> startLoc = new HashMap<String, Location>();
    private ArrayList<String> playerInTutorial = new ArrayList<String>();
    private int totalViews = 0;

    private TutorialUtils tutorialUtils;
    private TutorialTask tutorialTask;


    public void onEnable() {
        tutorialUtils = new TutorialUtils(this);
        tutorialTask = new TutorialTask(this);
        this.getServer().getPluginManager().registerEvents(new TutorialListener(this), this);
        this.getCommand("tutorial").setExecutor(new TutorialCommands(this));
        this.saveDefaultConfig();
        this.casheViewData();
        this.getTutorialTask().tutorialTask();
    }

    public void addTutorialView(int viewID, TutorialView view) {
        this.tutorialViews.put(viewID, view);
    }

    /**
     * Setup all views from config
     */
    public void casheViewData() {
        if (this.getConfig().getString("views") == null) return;
        for (String vID : this.getConfig().getConfigurationSection("views").getKeys(false)) {
            int viewID = Integer.parseInt(vID);
            MessageType messageType = MessageType.valueOf(this.getConfig().getString("views." + viewID + ".type", "META"));
            TutorialView view = new TutorialView(viewID, this.getConfig().getString("views." + viewID + ".message"), this.getTutorialUtils().getLocation(viewID), messageType);
            this.addTutorialView(viewID, view);
            this.totalViews = tutorialViews.size();
        }
    }

    public void startTutorial(Player player) {
        if (this.getConfig().getString("views") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a tutorial first! /tutorial create <message>");
            return;
        }
        String name = player.getName();
        this.startLoc.put(name, player.getLocation());
        this.initializeCurrentView(name);
        this.addToTutorial(name);
        for (Player online : this.getServer().getOnlinePlayers()) {
            online.hidePlayer(player);
            player.hidePlayer(online);
        }
        this.getServer().getPlayerExact(name).teleport(this.getTutorialView(name).getLocation());
        if (getTutorialView(name).getMessageType() == MessageType.TEXT) {
            player.sendMessage(tACC(getTutorialView(player.getName()).getMessage()));
        }
    }

    public void addToTutorial(String name) {
        this.playerInTutorial.add(name);
    }

    public void removeFromTutorial(String name) {
        this.playerInTutorial.remove(name);
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
        this.currentTutorialView.put(name, getCurrentView(name) + 1);
    }

    public int getCurrentView(String name) {
        return this.currentTutorialView.get(name);
    }

    public TutorialView getTutorialView(String name) {
        return this.tutorialViews.get(this.getCurrentView(name));
    }

    public void incrementTotalViews() {
        this.totalViews++;
    }

    public int getTotalViews() {
        return this.totalViews;
    }

    public Location getFirstLoc(String name) {
       return this.startLoc.get(name);
    }

    public void cleanFirstLoc(String name) {
        this.startLoc.remove(name);
    }

    public TutorialUtils getTutorialUtils() {
        return tutorialUtils;
    }

    public TutorialTask getTutorialTask() {
        return tutorialTask;
    }

    public String tACC(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
