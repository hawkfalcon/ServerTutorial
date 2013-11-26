package io.snw.tutorial;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class ViewConversation {

    ServerTutorial plugin;
    String name;

    public ViewConversation(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    private ConversationFactory factory;

    public void createNewView(Player player, String tutorialName) {
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
            context.setSessionData("name", name);
            return "This will guide you through creating a new View for the tutorial " + name;
        }

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return new ChooseMessageType();
        }
    }

    private class ChooseMessageType extends FixedSetPrompt {
        public ChooseMessageType() {
            super("TEXT", "META");
        }

        public String getPromptText(ConversationContext context) {
            return "Choose a MessageType: META or TEXT\nMETA - uses the item name\nTEXT - normal text message";
        }

        @Override
        public Prompt acceptValidatedInput(ConversationContext context, String input) {
            context.setSessionData("messagetype", input);
            return new Message();
        }
    }

    private class Message extends StringPrompt {
        public String getPromptText(ConversationContext context) {
            return "Choose what message this view should have:\nColor codes are supported!";

        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            context.setSessionData("message", input);
            return new FinishMessage();
        }
    }

    private class FinishMessage extends MessagePrompt {
        public String getPromptText(ConversationContext context) {
            return "The view for tutorial " + context.getSessionData("name").toString() + " has been successfully created as a " + context.getSessionData("messagetype").toString() + " based view with message " + context.getSessionData("message").toString() + "!";
        }

        @Override
        public Prompt getNextPrompt(ConversationContext context) {
            writeNewView(((Player) context.getForWhom()).getName(), context.getSessionData("name").toString(), context.getSessionData("messageType").toString(), context.getSessionData("message").toString());
            return Prompt.END_OF_CONVERSATION;
        }
    }

    private class Prefix implements ConversationPrefix {

        public String getPrefix(ConversationContext context) {
            return ChatColor.AQUA + "[" + ChatColor.GRAY + "Tutorial" + ChatColor.AQUA + "] " + ChatColor.WHITE;
        }
    }

    public void writeNewView(String playername, String name, String messageType, String message) {
        int viewID = 1;
        while (this.plugin.getConfig().get("tutorials." + name + ".views." + viewID) != null) {
            viewID++;
        }
        plugin.getConfig().set("tutorials." + name + ".views." + viewID + ".message", message);
        plugin.getConfig().set("tutorials." + name + ".views." + viewID + ".type", messageType);
        plugin.getTutorialUtils().saveLoc(name, viewID, plugin.getServer().getPlayerExact(playername).getLocation());
        plugin.saveConfig();
        plugin.reCasheTutorials();
    }
}