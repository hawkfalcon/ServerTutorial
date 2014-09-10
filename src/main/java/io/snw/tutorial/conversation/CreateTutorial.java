package io.snw.tutorial.conversation;

import io.snw.tutorial.ServerTutorial;
import io.snw.tutorial.api.CreateTutorialEvent;
import io.snw.tutorial.data.Caching;
import io.snw.tutorial.data.DataLoading;
import io.snw.tutorial.data.Getters;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class CreateTutorial {

    private static ServerTutorial plugin = ServerTutorial.getInstance();
    private String name;
    private Player player;

    private ConversationFactory factory;

    public void createNewTutorial(Player player, String tutorialName) {
        this.name = tutorialName;
        this.player = player;
        this.factory = new ConversationFactory(plugin)
                .withModality(true)
                        //.withPrefix(new Prefix())
                .withFirstPrompt(new Welcome())
                .withEscapeSequence("/quit")
                .withTimeout(60)
                .thatExcludesNonPlayersWithMessage("You must be in game!");
        factory.buildConversation(player).begin();
    }

    private class Welcome extends MessagePrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            context.setSessionData("name", name);
            context.setSessionData("player", player.getName());
            return ChatColor.translateAlternateColorCodes('&', "&6-------------------------------\n&8>&fWelcome to the &bServerTutorial&f tutorial creation!\n&8>&7This will guide you through creating a new Tutorial\n&8>&7Currently creating new tutorial with name: &b" + name + "!");
        }

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return new ChooseViewType();
        }
    }

    private class ChooseViewType extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>&7First, choose a ViewType: CLICK or TIME\n&8>&6CLICK &7- cycle though by clicking\n&8>&6TIME &7- automated with a timer\n&8>&7>&6> &7Type a ViewType to continue!");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("time") || input.equalsIgnoreCase("click")) {
                context.setSessionData("viewtype", input.toUpperCase());
                if (input.equalsIgnoreCase("time")) {
                    return new TimeLength();
                } else {
                    return new EndMessage();
                }
            }
            return new ChooseViewType();
        }
    }

    private class TimeLength extends NumericPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>&7>&6> &7Type how long should each view last (in seconds):");
        }

        @Override
        public Prompt acceptValidatedInput(ConversationContext context, Number input) {
            if (input.intValue() > 0) {
                context.setSessionData("timelength", input.intValue());
            } else {
                context.setSessionData("timelength", "10");
            }
            return new EndMessage();
        }
    }

    private class EndMessage extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>&7>&6> &7Type what message this tutorial should have at the end:");

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
            return ChatColor.translateAlternateColorCodes('&', "&8>&7The Tutorial &b" + name + "&7 has been successfully created!\n&8>&7It is a &f" + context.getSessionData("viewtype").toString() + " &7based tutorial with end message &f" + context.getSessionData("endmessage").toString() + "&7!\n&6-------------------------------");
        }

        @Override
        public Prompt getNextPrompt(ConversationContext context) {
            writeNewTutorial(name, context.getSessionData("viewtype").toString(), context.getSessionData("endmessage").toString(), context.getSessionData("timelength"), context.getSessionData("player").toString());
            return END_OF_CONVERSATION;
        }
    }

    private class Prefix implements ConversationPrefix {

        @Override
        public String getPrefix(ConversationContext context) {
            return ChatColor.AQUA + "[" + ChatColor.GRAY + "Tutorial" + ChatColor.AQUA + "] " + ChatColor.WHITE;
        }
    }

    public void writeNewTutorial(String name, String viewType, String endMessage, Object timeLength, String playerName) {
        DataLoading.getDataLoading().getData().set("tutorials." + name + ".viewtype", viewType);
        if (timeLength != null) {
            DataLoading.getDataLoading().getData().set("tutorials." + name + ".timelength", timeLength.toString());
        } else {
            DataLoading.getDataLoading().getData().set("tutorials." + name + ".timelength", "0");
        }
        DataLoading.getDataLoading().getData().set("tutorials." + name + ".endmessage", endMessage);
        DataLoading.getDataLoading().getData().set("tutorials." + name + ".item", "stick");
        DataLoading.getDataLoading().saveData();
        Caching.getCaching().reCasheTutorials();
        CreateTutorialEvent event = new CreateTutorialEvent(plugin.getServer().getPlayer(playerName), Getters.getGetters().getTutorial(name));
        plugin.getServer().getPluginManager().callEvent(event);
    }
}