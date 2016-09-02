package pw.hwk.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.api.EndTutorialEvent;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.TutorialManager;
import pw.hwk.tutorial.data.TutorialPlayer;
import pw.hwk.tutorial.enums.CommandType;
import pw.hwk.tutorial.util.TutorialUtils;

public class EndTutorial {


    private ServerTutorial plugin;

    public EndTutorial(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void endTutorial(Player player) {
        Tutorial tutorial = TutorialManager.getManager().getCurrentTutorial(player.getName());
        endTutorialPlayer(player, tutorial.getEndMessage());
        EndTutorialEvent event = new EndTutorialEvent(player, tutorial);

        plugin.getServer().getPluginManager().callEvent(event);

        TutorialManager.getManager().setSeenTutorial(Caching.getCaching().getUUID(player), tutorial.getName());

        String command = tutorial.getCommand();
        CommandType type = tutorial.getCommandType();
        if (type == CommandType.NONE || command == null || command.isEmpty()) {
            return;
        }
        if (command.startsWith("/")) {
            command = command.replaceFirst("/", "");
        }
        command = command.replace("%player%", player.getName());

        switch (type) {
            case PLAYER:
                Bukkit.dispatchCommand(player, command);
                break;
            case CONSOLE:
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                break;
        }
    }

    public void endTutorialPlayer(final Player player, String endMessage) {
        player.sendMessage(TutorialUtils.color(endMessage));
        player.closeInventory();

        Caching.getCaching().setTeleport(player, true);

        TutorialPlayer tutorialPlayer = plugin.getTutorialPlayer(Caching.getCaching().getUUID(player));
        tutorialPlayer.restorePlayer(player);

        plugin.removeTutorialPlayer(player);
        plugin.removeFromTutorial(player.getName());
    }

    public void reloadEndTutorial(Player player) {
        Tutorial tutorial = TutorialManager.getManager().getCurrentTutorial(player.getName());
        endTutorialPlayer(player, tutorial.getEndMessage());
    }
}
