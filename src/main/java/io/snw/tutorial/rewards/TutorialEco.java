
package io.snw.tutorial.rewards;

import io.snw.tutorial.ServerTutorial;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class TutorialEco {

    ServerTutorial plugin;
    private static Economy econ = null;

    public TutorialEco(ServerTutorial plugin) {
        this.plugin = plugin;
    }
/**
 * 
 * @return boolean returns true if vault is enabled and there is an economy plugin, otherwise false if either is not present 
 */
    public boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.econ = rsp.getProvider();
        return this.econ != null;
    }
/**
 * 
 * @return Economy Economy vault found 
 */
    public Economy getEcon() {
        return this.econ;
    }
}
