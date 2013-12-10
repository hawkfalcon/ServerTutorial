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
        @Override
        public String getPromptText(ConversationContext context) {
            context.setSessionData("name", name);
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

        @Override
        public String getPromptText(ConversationContext context) {
            return "Choose a ViewType: CLICK or TIME\nCLICK - cycle though by clicking\nTIME - automated with a timer";
        }

        @Override
        public Prompt acceptValidatedInput(ConversationContext context, String input) {
            context.setSessionData("viewtype", input);
            if (input.equalsIgnoreCase("time")) {
                return new TimeLength();
            } else {
                return new EndMessage();
            }
        }
    }

    private class TimeLength extends NumericPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return "How long should each view last?";

        }

        @Override
        public Prompt acceptValidatedInput(ConversationContext context, Number input) {
            if(input.intValue() < 0){
                context.setSessionData("timelength", input);
            } else {
                context.setSessionData("timelength", "10");
            }
            return new EndMessage();
        }
    }

    private class EndMessage extends StringPrompt {
        @Override
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
        @Override
        public String getPromptText(ConversationContext context) {
            return "The Tutorial " + name + " has been successfully created as a " + context.getSessionData("viewtype").toString() + " based tutorial with end message " + context.getSessionData("endmessage").toString() + "!";
        }

        @Override
        public Prompt getNextPrompt(ConversationContext context) {
            writeNewTutorial(name, context.getSessionData("viewtype").toString(), context.getSessionData("endmessage").toString(), context.getSessionData("timelength").toString());
            return END_OF_CONVERSATION;
        }
    }

    private class Prefix implements ConversationPrefix {

        @Override
        public String getPrefix(ConversationContext context) {
            return ChatColor.AQUA + "[" + ChatColor.GRAY + "Tutorial" + ChatColor.AQUA + "] " + ChatColor.WHITE;
        }
    }

    public void writeNewTutorial(String name, String viewType, String endMessage, String timeLength) {
        plugin.getConfig().set("tutorials." + name + ".viewtype", viewType);
        if (timeLength != null) {
            plugin.getConfig().set("tutorials." + name + ".timelength", timeLength);
        } else {
            plugin.getConfig().set("tutorials." + name + ".timelength", "0");
        }
        plugin.getConfig().set("tutorials." + name + ".endmessage", endMessage);
        plugin.getConfig().set("tutorials." + name + ".item", "stick");
        plugin.saveConfig();
        plugin.reCasheTutorials();
    }
}