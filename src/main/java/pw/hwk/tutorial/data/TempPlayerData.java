/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.hwk.tutorial.data;

import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pw.hwk.tutorial.ServerTutorial;
import pw.hwk.tutorial.util.Base64Serialize;

/**
 *
 * @author Frostalf
 */
public class TempPlayerData {

    private OfflinePlayer player;
    private UUID playerUUID;
    private Inventory inventory;
    private GameMode gameMode;
    private ItemStack[] armorItemStack;

    private static ServerTutorial plugin = ServerTutorial.getInstance();

    public TempPlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.player = plugin.getServer().getOfflinePlayer(playerUUID);
        getTempPlayerData();
    }

    private void getTempPlayerData() {
        this.inventory = Base64Serialize.fromBase64(DataLoading.getDataLoading().getTempData().getString("players." + this.playerUUID + ".inventory"));
        this.gameMode = GameMode.valueOf(DataLoading.getDataLoading().getTempData().getString("players." + this.playerUUID + ".gamemode"));
        this.armorItemStack = Base64Serialize.itemStackArrayFromBase64(DataLoading.getDataLoading().getTempData().getString("players." + this.playerUUID + ".armor"));
    }

    public Inventory getPlayerInventory() {
        return this.inventory;
    }

    public ItemStack[] getPlayerArmor() {
        return this.armorItemStack;
    }

    public GameMode getPlayerGameMode() {
        return this.gameMode;
    }

    public void restorePlayer() {
        if (player.isOnline()) {
            Player onlinePlayer = (Player)player;
            onlinePlayer.getInventory().setContents(getPlayerInventory().getContents());
            onlinePlayer.getInventory().setArmorContents(getPlayerArmor());
            onlinePlayer.setGameMode(getPlayerGameMode());
        }
    }
}
