package io.snw.tutorial;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class CreateTutorial {

    ServerTutorial plugin;
    String name;

    public CreateTutorial(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    private ConversationFactory factory;

    public void createNewTutorial(Player player, String tutorialName) {
        this.name = tutorialName;
        this.factory = new ConversationFactory(plugin)
                .withModality(true)
                .withPrefix(new Prefix())
                .withFirstPrompt(new Welcome())
                .withEscapeSequence("/quit")
                .withTimeout(10)
                .thatExcludesNonPlayersWithMessage("You must be in game!");
        factory.buildConversation(player).begin();
    }

    private class Welcome extends MessagePrompt {
        public String getPromptText(ConversationContext context) {
            return "This will guide you through creating a new Tutorial (" + name + ")!";
        }

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return new ChooseViewType();
        }
    }

    private class ChooseViewType extends FixedSetPrompt {
        public ChooseViewType() {
            super("CLICK", "TIME");
        }

        public String getPromptText(ConversationContext context) {
            return "Choose a ViewType: CLICK or TIME\nCLICK - cycle though by clicking\nTIME - automated with a timer";
        }

        @Override
        public Prompt acceptValidatedInput(ConversationContext context, String input) {
            context.setSessionData("viewtype", input);
            return new EndMessage();
        }
    }

    private class EndMessage extends StringPrompt {
        public String getPromptText(ConversationContext context) {
            return "Choose what end message this tutorial should have:";

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            context.setSessionData("endmessage", input);
            return new FinishMessage();
        }
    }

    private class FinishMessage extends MessagePrompt {
        public String getPromptText(ConversationContext context) {
            return "The Tutorial " + name + " has been successfully created as a " + context.getSessionData("viewtype").toString() + " based tutorial with end message " + context.getSessionData("endmessage").toString() + "!";
        }

        @Override
        public Prompt getNextPrompt(ConversationContext context) {
            writeNewTutorial(name, context.getSessionData("viewtype").toString(), context.getSessionData("endmessage").toString());
            return Prompt.END_OF_CONVERSATION;
        }
    }

    private class Prefix implements ConversationPrefix {

        public String getPrefix(ConversationContext context) {
            return ChatColor.AQUA + "[" + ChatColor.GRAY + "Tutorial" + ChatColor.AQUA + "] " + ChatColor.WHITE;
        }
    }

    public void writeNewTutorial(String name, String viewType, String endMessage) {
        plugin.getConfig().set("tutorials." + name + ".viewtype", viewType);
        plugin.getConfig().set("tutorials." + name + ".endmessage", endMessage);
        plugin.getConfig().set("tutorials." + name + ".item", "stick");
        plugin.saveConfig();
        plugin.reCasheTutorials();
    }
}