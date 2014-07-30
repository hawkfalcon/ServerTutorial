
package io.snw.tutorial.rewards;

import io.snw.tutorial.ServerTutorial;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class TutorialEco {

    private Economy econ;

/**
 * 
 * @return boolean returns true if vault is enabled and there is an economy plugin, otherwise false if either is not present 
 */
    public boolean setupEconomy() {
        return getEcon() != null;
    }
    
    public Economy getEcon() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp != null) {
            return rsp.getProvider();
        }
        return null;
    }
}
