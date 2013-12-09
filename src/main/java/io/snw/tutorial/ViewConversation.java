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
        @Override
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

        @Override
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
        @Override
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
        @Override
        public String getPromptText(ConversationContext context) {
            return "The view for tutorial " + name + " has been successfully created as a " + context.getSessionData("messagetype").toString() + " based view with message " + context.getSessionData("message").toString() + "!";
        }

        @Override
        public Prompt getNextPrompt(ConversationContext context) {
            Player player = (Player) context.getForWhom();
            String location = plugin.getServer().getPlayer(player.getName()).getLocation().getWorld().getName() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getX() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getY() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getZ() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getYaw() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getPitch();
            String message = context.getSessionData("message").toString();
            String messageType = context.getSessionData("messagetype").toString();
            String name = context.getSessionData("name").toString();
            int viewID = 1;
            while (plugin.getConfig().get("tutorials." + context.getSessionData("name") + ".views." + viewID) != null) {
                viewID++;
            }
            try {
                plugin.getConfig().set("tutorials." + name + ".views." + viewID + ".message", message);
                plugin.getConfig().set("tutorials." + name + ".views." + viewID + ".messagetype", messageType);
                plugin.getConfig().set("tutorials." + name + ".views" + viewID + ".location", location);
                plugin.saveConfig();
                plugin.reCasheTutorials();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return END_OF_CONVERSATION;
        }
    }

    private class Prefix implements ConversationPrefix {

        @Override
        public String getPrefix(ConversationContext context) {
            return ChatColor.AQUA + "[" + ChatColor.GRAY + "Tutorial" + ChatColor.AQUA + "] " + ChatColor.WHITE;
        }
    }
}