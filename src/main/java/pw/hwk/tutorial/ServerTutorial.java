package pw.hwk.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pw.hwk.tutorial.api.StartTutorialEvent;
import pw.hwk.tutorial.api.ViewSwitchEvent;
import pw.hwk.tutorial.commands.TutorialMainCommand;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.DataLoading;
import pw.hwk.tutorial.data.Getters;
import pw.hwk.tutorial.data.Setters;
import pw.hwk.tutorial.enums.ViewType;
import pw.hwk.tutorial.util.TutorialUtils;
import pw.hwk.tutorial.util.Updater;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ServerTutorial extends JavaPlugin {

    private static ServerTutorial instance;

    private Map<String, Location> startLoc = new HashMap<>();
    private Map<String, ItemStack[]> inventories = new HashMap<>();
    private Map<String, Boolean> flight = new HashMap<>();
    private EndTutorial endTutorial = new EndTutorial(this);

    @Override
    public void onEnable() {
        instance = this;

        this.getServer().getPluginManager().registerEvents(new TutorialListener(), this);
        this.getCommand("tutorial").setExecutor(new TutorialMainCommand());
        this.saveDefaultConfig();

        DataLoading.getDataLoading().loadData();
        DataLoading.getDataLoading().loadPlayerData();
        Caching.getCaching().casheAllData();
        Caching.getCaching().cacheConfigs();
        Caching.getCaching().cachePlayerData();

        this.checkUpdate();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    /**
     * Checks for update
     */
    private void checkUpdate() {
        if (getConfig().get("auto-update") == null) {
            getConfig().set("auto-update", true);
            saveConfig();
            Caching.getCaching().reCacheConfigs();
        }
        if (getConfig().getBoolean("auto-update")) {
            final ServerTutorial plugin = this;
            final File file = this.getFile();

            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    Updater updater = new Updater(plugin, 69090, file, Updater.UpdateType.DEFAULT, false);
                    if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
                        getLogger().log(Level.INFO, "Successfully updated ServerTutorial to version {0} for next restart!", updater.getLatestName());
                    }
                }
            });
        }
    }

    /**
     * Starts tutorial
     *
     * @param tutorialName tutorial name
     * @param player       Player
     */
    public void startTutorial(String tutorialName, Player player) {
        String name = player.getName();
        if (DataLoading.getDataLoading().getData().getConfigurationSection("tutorials") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a tutorial first! /tutorial create <message>");
            return;
        }
        Tutorial tut = Getters.getGetters().getTutorial(tutorialName);
        if (tut == null) {
            player.sendMessage("Invalid tutorial");
            return;
        }
        if (DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." + tutorialName + ".views") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a view first! /tutorial addview <tutorial name>");
            return;
        }

        this.startLoc.put(name, player.getLocation());
        this.addInventory(name, player.getInventory().getContents());
        this.addFlight(name, player.getAllowFlight());
        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);
        Caching.getCaching().setGameMode(player.getUniqueId(), player.getGameMode());
        player.setGameMode(tut.getGameMode());
        this.initializeCurrentView(name);
        Setters.getSetters().addCurrentTutorial(name, tutorialName);
        Setters.getSetters().addToTutorial(name);
        for (Player online : this.getServer().getOnlinePlayers()) {
            online.hidePlayer(player);
        }
        Caching.getCaching().setTeleport(player.getUniqueId(), true);
        player.teleport(Getters.getGetters().getTutorialView(tutorialName, name).getLocation());
        if (Getters.getGetters().getTutorial(tutorialName).getViewType() == ViewType.TIME) {
            Getters.getGetters().getTutorialTimeTask(tutorialName, name);
        }
        TutorialUtils.getTutorialUtils().messageUtils(player);
        StartTutorialEvent event = new StartTutorialEvent(player, Getters.getGetters().getTutorial(tutorialName));
        this.getServer().getPluginManager().callEvent(event);
        if (DataLoading.getDataLoading().getPlayerData().get("players." + Caching.getCaching().getUUID(player)) == null) {
            DataLoading.getDataLoading().getPlayerData().set("players." + Caching.getCaching().getUUID(player) + ".seen", "true");
            DataLoading.getDataLoading().getPlayerData().set("players." + Caching.getCaching().getUUID(player) + ".tutorials." + tutorialName, "false");
            DataLoading.getDataLoading().savePlayerData();
            Caching.getCaching().reCachePlayerData();
        } else if (DataLoading.getDataLoading().getPlayerData().get("players." + Caching.getCaching().getUUID(player) + ".tutorials." + tutorialName) == null) {
            DataLoading.getDataLoading().getPlayerData().set("players." + Caching.getCaching().getUUID(player) + ".tutorials." + tutorialName, "false");
            DataLoading.getDataLoading().savePlayerData();
            Caching.getCaching().reCachePlayerData();
        }
    }

    /**
     * Removes player from tutorial
     *
     * @param name Player name
     */
    public void removeFromTutorial(String name) {
        Caching.getCaching().playerInTutorial().remove(name);
        this.startLoc.remove(name);
        Caching.getCaching().currentTutorial().remove(name);
        Caching.getCaching().currentTutorialView().remove(name);
        this.flight.remove(name);
    }

    public void initializeCurrentView(String name) {
        Caching.getCaching().currentTutorialView().put(name, 1);
    }

    public void incrementCurrentView(String name) {
        TutorialView fromTutorialView = Getters.getGetters().getTutorialView(name);
        Caching.getCaching().currentTutorialView().put(name, Getters.getGetters().getCurrentView(name) + 1);
        TutorialView toTutorialView = Getters.getGetters().getTutorialView(name);
        @SuppressWarnings("deprecation") ViewSwitchEvent event = new ViewSwitchEvent(Bukkit.getPlayerExact(name), fromTutorialView, toTutorialView, Getters.getGetters().getCurrentTutorial(name));
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

    public EndTutorial getEndTutorial() {
        return endTutorial;
    }

    public void removeTutorial(String tutorialName) {
        DataLoading.getDataLoading().getData().set("tutorials." + tutorialName, null);
        DataLoading.getDataLoading().saveData();
        Caching.getCaching().reCasheTutorials();
    }

    public void removeTutorialView(String tutorialName, int viewID) {
        int viewsCount = Getters.getGetters().getTutorial(tutorialName).getTotalViews();
        DataLoading.getDataLoading().getData().set("tutorials." + tutorialName + ".views." + viewID, null);
        DataLoading.getDataLoading().saveData();
        if (viewsCount != viewID) {
            for (String vID : DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." + tutorialName + ".views").getKeys(false)) {
                int currentID = Integer.parseInt(vID);
                int newViewID = Integer.parseInt(vID) - 1;
                String message = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".views." + currentID + ".message");
                String messageType = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".views." + currentID + ".messagetype");
                String location = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + ".views." + currentID + ".location");
                DataLoading.getDataLoading().getData().set("tutorials." + tutorialName + ".views." + newViewID + ".message", message);
                DataLoading.getDataLoading().getData().set("tutorials." + tutorialName + ".views." + newViewID + ".messagetype", messageType);
                DataLoading.getDataLoading().getData().set("tutorials." + tutorialName + ".views." + newViewID + ".location", location);
                DataLoading.getDataLoading().getData().set("tutorials." + tutorialName + ".views." + currentID, null);
            }
        }
        DataLoading.getDataLoading().saveData();
        Caching.getCaching().reCasheTutorials();
    }

    public static ServerTutorial getInstance() {
        return instance;
    }

}
