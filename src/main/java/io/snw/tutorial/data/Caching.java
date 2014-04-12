
package io.snw.tutorial.data;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.Tutorial;
import io.snw.tutorial.TutorialConfigs;
import io.snw.tutorial.TutorialView;
import io.snw.tutorial.enums.MessageType;
import io.snw.tutorial.enums.ViewType;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Material;

public class Caching {

    ServerTutorial plugin;
    private ArrayList<String> tutorialNames = new ArrayList<String>();
    private HashMap<String, Tutorial> tutorials = new HashMap<String, Tutorial>();
    private HashMap<String, TutorialConfigs> configs = new HashMap<String, TutorialConfigs>();
    //player name, tutorial name
    private HashMap<String, String> currentTutorial = new HashMap<String, String>();
    private HashMap<String, Integer> currentTutorialView = new HashMap<String, Integer>();
    private ArrayList<String> playerInTutorial = new ArrayList<String>();
    
    public Caching(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void casheAllData() {
        if (plugin.dataLoad().getData().getString("tutorials") == null) {
            return;
        }
        for (String tutorialName : plugin.dataLoad().getData().getConfigurationSection("tutorials").getKeys(false)) {
            this.tutorialNames.add(tutorialName.toLowerCase());
            HashMap<Integer, TutorialView> tutorialViews = new HashMap<Integer, TutorialView>();
            if (plugin.dataLoad().getData().getConfigurationSection("tutorials." + tutorialName + ".views") != null) {
                for (String vID : plugin.dataLoad().getData().getConfigurationSection("tutorials." + tutorialName + ".views").getKeys(false)) {
                    int viewID = Integer.parseInt(vID);
                    MessageType messageType = MessageType.valueOf(plugin.dataLoad().getData().getString("tutorials." + tutorialName + ".views." + viewID + ".messagetype", "META"));
                    TutorialView view = new TutorialView(viewID, plugin.dataLoad().getData().getString("tutorials." + tutorialName + ".views." + viewID + ".message", "No message written"), plugin.getTutorialUtils().getLocation(tutorialName, viewID), messageType);
                    tutorialViews.put(viewID, view);
                }
            }
            ViewType viewType = ViewType.valueOf(plugin.dataLoad().getData().getString("tutorials." + tutorialName + ".viewtype", "CLICK"));
            String timeLengthS = plugin.dataLoad().getData().getString("tutorials." + tutorialName + ".timelength", "10");
            int timeLength = Integer.parseInt(timeLengthS);
            String endMessage = plugin.dataLoad().getData().getString("tutorials." + tutorialName + ".endmessage", "Sample end message");
            Material item = Material.matchMaterial(plugin.dataLoad().getData().getString("tutorials." + tutorialName + ".item", "stick"));
            Tutorial tutorial = new Tutorial(tutorialName, tutorialViews, viewType, timeLength, endMessage, item);
            plugin.setters().addTutorial(tutorialName, tutorial);
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

    public ArrayList<String> playerInTutorial() {
        return playerInTutorial;
    }

    public void cacheConfigs() {
        TutorialConfigs configOptions = new TutorialConfigs(plugin.getConfig().getBoolean("auto-update"), plugin.getConfig().getString("sign"), plugin.getConfig().getBoolean("first_join"), plugin.getConfig().getString("first_join_tutorial"), 
        plugin.getConfig().getBoolean("rewards"), plugin.getConfig().getBoolean("exp_countdown"), plugin.getConfig().getDouble("view_money"), Float.valueOf(plugin.getConfig().getString("view_exp")), plugin.getConfig().getDouble("tutorial_money"), Float.valueOf(plugin.getConfig().getString("tutorial_exp")), 
        plugin.getConfig().getBoolean("per_tutorial_money"), plugin.getConfig().getBoolean("per_tutorial_exp"), plugin.getConfig().getBoolean("per_view_money"), plugin.getConfig().getBoolean("per_view_exp"));
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
}
