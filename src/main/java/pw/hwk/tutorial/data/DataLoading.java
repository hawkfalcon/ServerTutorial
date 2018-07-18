package pw.hwk.tutorial.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.hwk.tutorial.ServerTutorial;

import java.io.File;
import java.io.IOException;

public class DataLoading {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private static DataLoading instance;
    private File dataFile;
    private YamlConfiguration data;
    private File playerDataFile;
    private YamlConfiguration playerData;
    private File tempDataFile;
    private YamlConfiguration tempData;

    public void loadData() {
        File f = new File(plugin.getDataFolder(), "data.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataFile = f;
        data = YamlConfiguration.loadConfiguration(f);
        if (plugin.getConfig().contains("tutorials")) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("tutorials");
            data.set("tutorials", section);
            saveData();
            plugin.getConfig().set("tutorials", null);
            plugin.saveConfig();
        }
    }

    public void loadPlayerData() {
        File f = new File(plugin.getDataFolder(), "players.yml");
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

    public void loadTempData() {
        File f = new File(plugin.getDataFolder(), "tempdata.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tempDataFile = f;
        tempData = YamlConfiguration.loadConfiguration(f);
    }

    public YamlConfiguration getPlayerData() {
        return this.playerData;
    }

    public YamlConfiguration getData() {
        return this.data;
    }

    public YamlConfiguration getTempData() {
        return this.tempData;
    }

    public void saveTempData() {
        try {
            tempData.save(tempDataFile);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save temp data :(");
        }
    }

    public void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save data :(");
        }
    }

    public void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save player data :(");
        }
    }

    public static DataLoading getDataLoading() {
        if (instance == null) {
            instance = new DataLoading();
        }
        return instance;
    }
}
