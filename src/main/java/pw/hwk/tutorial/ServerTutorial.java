package pw.hwk.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pw.hwk.tutorial.api.StartTutorialEvent;
import pw.hwk.tutorial.api.ViewSwitchEvent;
import pw.hwk.tutorial.commands.TutorialMainCommand;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.DataLoading;
import pw.hwk.tutorial.data.TutorialManager;
import pw.hwk.tutorial.data.TutorialPlayer;
import pw.hwk.tutorial.enums.ViewType;
import pw.hwk.tutorial.util.TutorialUtils;
import pw.hwk.tutorial.util.Updater;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class ServerTutorial extends JavaPlugin {

    private static ServerTutorial instance;

    private Map<UUID, TutorialPlayer> tutorialPlayers = new HashMap<>();

    private EndTutorial endTutorial = new EndTutorial(this);

    public ServerTutorial() {}

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
        for (String players : TutorialManager.getManager().getAllInTutorial()) {
            Player player = getServer().getPlayer(players);
            getEndTutorial().endTutorial(player);
        }
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
                        getLogger().log(Level.INFO, "Successfully updated ServerTutorial to version {0} for next " +
                                "restart!", updater.getLatestName());
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
        UUID uuid = Caching.getCaching().getUUID(player);
        if (DataLoading.getDataLoading().getData().getConfigurationSection("tutorials") == null) {
            player.sendMessage(ChatColor.RED + "You need to set up a tutorial first! /tutorial create <name>");
            return;
        }
        Tutorial tut = TutorialManager.getManager().getTutorial(tutorialName);
        if (tut == null) {
            player.sendMessage("Invalid tutorial");
            return;
        }
        if (DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." + tutorialName + ".views") ==
                null) {
            player.sendMessage(ChatColor.RED + "You need to set up a view first! /tutorial addview <name>");
            return;
        }

        TutorialPlayer tutorialPlayer = new TutorialPlayer(player);
        tutorialPlayer.clearPlayer(player);
        addTutorialPlayer(uuid, tutorialPlayer);

        player.setGameMode(tut.getGameMode());
        this.initializeCurrentView(name);
        TutorialManager.getManager().addCurrentTutorial(name, tutorialName);
        TutorialManager.getManager().addToTutorial(name);

        Caching.getCaching().setTeleport(player, true);
        player.teleport(TutorialManager.getManager().getTutorialView(tutorialName, name).getLocation());
        if (TutorialManager.getManager().getTutorial(tutorialName).getViewType() == ViewType.TIME) {
            TutorialManager.getManager().getTutorialTimeTask(tutorialName, name);
        }
        TutorialUtils.getTutorialUtils().messageUtils(player);
        StartTutorialEvent event = new StartTutorialEvent(player, TutorialManager.getManager().getTutorial(tutorialName));
        this.getServer().getPluginManager().callEvent(event);

        DataLoading.getDataLoading().getPlayerData().set("players." + uuid + ".tutorials." + tutorialName, "false");
        DataLoading.getDataLoading().savePlayerData();
    }

    /**
     * Removes player from tutorial
     *
     * @param name Player name
     */
    public void removeFromTutorial(String name) {
        Caching.getCaching().playerInTutorial().remove(name);
        Caching.getCaching().currentTutorial().remove(name);
        Caching.getCaching().currentTutorialView().remove(name);
    }

    public TutorialPlayer getTutorialPlayer(UUID uuid) {
        return tutorialPlayers.get(uuid);
    }

    public void addTutorialPlayer(UUID uuid, TutorialPlayer tutorialPlayer) {
        this.tutorialPlayers.put(uuid, tutorialPlayer);
    }

    public void removeTutorialPlayer(Player player) {
        this.tutorialPlayers.remove(Caching.getCaching().getUUID(player));
    }

    public void initializeCurrentView(String name) {
        Caching.getCaching().currentTutorialView().put(name, 1);
    }

    public void incrementCurrentView(String name) {
        TutorialView fromTutorialView = TutorialManager.getManager().getTutorialView(name);
        Caching.getCaching().currentTutorialView().put(name, TutorialManager.getManager().getCurrentView(name) + 1);
        TutorialView toTutorialView = TutorialManager.getManager().getTutorialView(name);
        ViewSwitchEvent event = new ViewSwitchEvent(Bukkit.getPlayerExact(name), fromTutorialView, toTutorialView,
                TutorialManager.getManager().getCurrentTutorial(name));
        Bukkit.getServer().getPluginManager().callEvent(event);
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
        int viewsCount = TutorialManager.getManager().getTutorial(tutorialName).getTotalViews();
        DataLoading.getDataLoading().getData().set("tutorials." + tutorialName + ".views." + viewID, null);
        DataLoading.getDataLoading().saveData();
        if (viewsCount != viewID) {
            for (String vID : DataLoading.getDataLoading().getData().getConfigurationSection("tutorials." +
                    tutorialName + ".views").getKeys(false)) {
                int currentID = Integer.parseInt(vID);
                int newViewID = Integer.parseInt(vID) - 1;
                String message = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + "" +
                        ".views." + currentID + ".message");
                String messageType = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + "" +
                        ".views." + currentID + ".messagetype");
                String location = DataLoading.getDataLoading().getData().getString("tutorials." + tutorialName + "" +
                        ".views." + currentID + ".location");
                DataLoading.getDataLoading().getData().set("tutorials." + tutorialName + ".views." + newViewID + "" +
                        ".message", message);
                DataLoading.getDataLoading().getData().set("tutorials." + tutorialName + ".views." + newViewID + "" +
                        ".messagetype", messageType);
                DataLoading.getDataLoading().getData().set("tutorials." + tutorialName + ".views." + newViewID + "" +
                        ".location", location);
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
