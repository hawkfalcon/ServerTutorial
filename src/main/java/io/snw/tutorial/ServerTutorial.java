package io.snw.tutorial;


import io.snw.tutorial.enums.MessageType;
import io.snw.tutorial.enums.ViewType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerTutorial extends JavaPlugin {

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
    private ArrayList<String> playerInTutorial = new ArrayList<String>();

    private TutorialUtils tutorialUtils = new TutorialUtils(this);
    private TutorialTask tutorialTask = new TutorialTask(this);
    private CreateTutorial createTutorial = new CreateTutorial(this);
    private ViewConversation viewConversation = new ViewConversation(this);

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new TutorialListener(this), this);
        this.getCommand("tutorial").setExecutor(new TutorialCommands(this));
        this.saveDefaultConfig();
        if (!getConfig().contains("tutorials")) {
            this.getConfig().createSection("tutorials");
        }
        this.saveConfig();
        this.casheAllData();
        this.getTutorialTask().tutorialTask();
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
        if (this.getConfig().getString("tutorials") == null) {
            return;
        }
        for (String tutorialName : this.getConfig().getConfigurationSection("tutorials").getKeys(false)) {
            this.tutorialNames.add(tutorialName);
            HashMap<Integer, TutorialView> tutorialViews = new HashMap<Integer, TutorialView>();
            if (this.getConfig().getConfigurationSection("tutorials." + tutorialName + ".views") != null) {
                for (String vID : this.getConfig().getConfigurationSection("tutorials." + tutorialName + ".views").getKeys(false)) {
                    int viewID = Integer.parseInt(vID);
                    MessageType messageType = MessageType.valueOf(this.getConfig().getString("tutorials." + tutorialName + ".views." + viewID + ".type", "META"));
                    TutorialView view = new TutorialView(viewID, this.getConfig().getString("tutorials." + tutorialName + ".views." + viewID + ".message", "No message written"), this.getTutorialUtils().getLocation(tutorialName, viewID), messageType);
                    tutorialViews.put(viewID, view);
                }
            }
            ViewType viewType = ViewType.valueOf(this.getConfig().getString("tutorials." + tutorialName + ".viewtype", "CLICK"));
            String timeLengthS = this.getConfig().getString("tutorials." + tutorialName + ".timelength", "10");
            int timeLength = Integer.parseInt(timeLengthS);
            String endMessage = this.getConfig().getString("tutorials." + tutorialName + ".endmessage", "Sample end message");
            Material item = Material.matchMaterial(this.getConfig().getString("tutorials." + tutorialName + ".item", "stick"));
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
        if (this.getConfig().getConfigurationSection("tutorials") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a tutorial first! /tutorial create <message>");
            return;
        }
        if (this.getConfig().getConfigurationSection("tutorials." + tutorialName + ".views") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a view first! /tutorial addview <tutorial name>");
            return;
        }
        if (this.getTutorial(tutorialName) == null) {
            player.sendMessage("Invalid tutorial");
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
        this.currentTutorialView.put(name, getCurrentView(name) + 1);
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
}
