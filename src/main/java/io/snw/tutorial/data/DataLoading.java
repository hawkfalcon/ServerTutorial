
package io.snw.tutorial.data;

import io.snw.tutorial.ServerTutorial;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataLoading {

    ServerTutorial plugin;
    private File dataFile;
    private YamlConfiguration data;
    private File playerDataFile;
    private YamlConfiguration playerData;
    
    
    public DataLoading(ServerTutorial plugin) {
        this.plugin = plugin;
    }
    
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
}
