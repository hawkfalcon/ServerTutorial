package pw.hwk.tutorial.conversation;

import pw.hwk.tutorial.ServerTutorial;
import pw.hwk.tutorial.api.CreateTutorialEvent;
import pw.hwk.tutorial.data.Caching;
import pw.hwk.tutorial.data.DataLoading;
import pw.hwk.tutorial.data.Getters;
import pw.hwk.tutorial.enums.CommandType;
import pw.hwk.tutorial.enums.ViewType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import pw.hwk.tutorial.util.TutorialUtils;

public class CreateTutorial {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private static CreateTutorial instance;
    private String name;
    private Player player;

    public void createNewTutorial(Player player, String tutorialName) {
        this.name = tutorialName;
        this.player = player;
        ConversationFactory factory = new ConversationFactory(plugin).withModality(true)
                //.withPrefix(new Prefix())
                .withFirstPrompt(new Welcome()).withEscapeSequence("/quit").withTimeout(60).thatExcludesNonPlayersWithMessage("You must be in game!");
        factory.buildConversation(player).begin();
    }

    private class Welcome extends MessagePrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            context.setSessionData("name", name);
            context.setSessionData("player", player.getName());
            return TutorialUtils.color("&6-------------------------------\n" + "&8>&fWelcome to the &bServerTutorial&f tutorial creation!\n" + "&8>&7This will guide you through creating a new Tutorial\n" + "&8>&7Currently creating new tutorial with name: &b" + name + "!");
        }

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return new ChooseViewType();
        }
    }

    private class ChooseViewType extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return TutorialUtils.color("&8>&7First, choose a ViewType: CLICK or TIME\n" + "&8>&6CLICK &7- cycle though by clicking\n" + "&8>&6TIME &7- automated with a timer\n" + "&8>&7>&6> &7Type a ViewType to continue!");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            try {
                ViewType type = ViewType.valueOf(input.toUpperCase());
                context.setSessionData("viewtype", type.toString().toUpperCase());
                if (type == ViewType.TIME) {
                    return new TimeLength();
                } else {
                    return new GamemodeMessage();
                }
            } catch (IllegalArgumentException e) {
                return new ChooseViewType();
            }
        }
    }

    private class TimeLength extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return TutorialUtils.color("&8>&7>&6> &7Type how long should each view last (in seconds):");
        }

        @Override
        public Prompt acceptValidatedInput(ConversationContext context, Number input) {
            if (input.intValue() > 0) {
                context.setSessionData("timelength", input.intValue());
            } else {
                context.setSessionData("timelength", "10");
            }
            return new GamemodeMessage();
        }
    }

    private class GamemodeMessage extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            String s = "&8>&7Choose a gamemode for the tutorial (it will be restored after the tutorial)\n";

            for (GameMode gm : GameMode.values()) {
                s += "&8>&6" + gm.toString().toUpperCase() + "\n";
            }

            s += "&8>&7>&6> &7Type a gamemode to continue:";
            return TutorialUtils.color(s);
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            try {
                GameMode gm = GameMode.valueOf(input.toUpperCase());
                context.setSessionData("gamemode", gm.toString().toUpperCase());
                return new EndCommandTypeMessage();
            } catch (IllegalArgumentException e) {
                return new GamemodeMessage();
            }
        }
    }

    private class EndCommandTypeMessage extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return TutorialUtils.color("&8>&7Choose a type for the end-command: PLAYER, SUDO, CONSOLE or NONE\n" + "&8>&6PLAYER &7- the tutorial player will execute the command\n" + "&8>&6SUDO &7- like PLAYER, but the user will execute it with * permission\n" + "&8>&6CONSOLE &7- execute command as console\n" + "&8>&6NONE &7- Dont execute any commands\n" + "&8>&7>&6> &7Type a Command Type to continue:");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            try {
                CommandType type = CommandType.valueOf(input.toUpperCase());
                context.setSessionData("commandtype", type.toString().toUpperCase());

                if (type == CommandType.NONE) {
                    context.setSessionData("command", "");
                    return new EndMessage();
                }

                return new EndCommandMessage();
            } catch (IllegalArgumentException e) {
                return new EndCommandTypeMessage();
            }
        }
    }


    private class EndCommandMessage extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return TutorialUtils.color("&8>&7>&6> &7Type what command should be executed when the tutorial ends (use %player% as placeholder):");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input == null || input.trim().isEmpty()) {
                return new EndCommandMessage();
            }
            context.setSessionData("command", input.trim());
            return new EndMessage();
        }
    }

    private class EndMessage extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return TutorialUtils.color("&8>&7>&6> &7Type what message this tutorial should have at the end:");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            context.setSessionData("endmessage", input);
            return new FinishMessage();
        }
    }

    private class FinishMessage extends MessagePrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return TutorialUtils.color("&8>&7The Tutorial &b" + name + "&7 has been successfully created!\n" + "&8>&7It is a &f" + context.getSessionData("viewtype").toString() + " &7based tutorial with end message &f" + context.getSessionData("endmessage").toString() + "&7!\n" + "&6-------------------------------");
        }

        @Override
        public Prompt getNextPrompt(ConversationContext context) {
            writeNewTutorial(name, context.getSessionData("viewtype").toString(), context.getSessionData("endmessage").toString(), context.getSessionData("timelength"), context.getSessionData("player").toString(), context.getSessionData("command").toString(), context.getSessionData("commandtype").toString(), context.getSessionData("gamemode").toString());
            return END_OF_CONVERSATION;
        }
    }

    private class Prefix implements ConversationPrefix {

        @Override
        public String getPrefix(ConversationContext context) {
            return ChatColor.AQUA + "[" + ChatColor.GRAY + "Tutorial" + ChatColor.AQUA + "] " + ChatColor.WHITE;
        }
    }

    public void writeNewTutorial(String name, String viewType, String endMessage, Object timeLength, String playerName, String command, String commandType, String gamemode) {
        DataLoading.getDataLoading().getData().set("tutorials." + name + ".viewtype", viewType);
        if (timeLength != null) {
            DataLoading.getDataLoading().getData().set("tutorials." + name + ".timelength", timeLength.toString());
        } else {
            DataLoading.getDataLoading().getData().set("tutorials." + name + ".timelength", "0");
        }
        DataLoading.getDataLoading().getData().set("tutorials." + name + ".endmessage", endMessage);
        DataLoading.getDataLoading().getData().set("tutorials." + name + ".item", "STICK");
        DataLoading.getDataLoading().getData().set("tutorials." + name + ".endcommand", command);
        DataLoading.getDataLoading().getData().set("tutorials." + name + ".endcommandtype", commandType);
        DataLoading.getDataLoading().getData().set("tutorials." + name + ".gamemode", gamemode);
        DataLoading.getDataLoading().saveData();
        Caching.getCaching().reCasheTutorials();
        CreateTutorialEvent event = new CreateTutorialEvent(plugin.getServer().getPlayer(playerName), Getters.getGetters().getTutorial(name));
        plugin.getServer().getPluginManager().callEvent(event);
    }

    public static CreateTutorial getCreateTutorial() {
        if (instance == null) {
            instance = new CreateTutorial();
        }
        return instance;
    }
}