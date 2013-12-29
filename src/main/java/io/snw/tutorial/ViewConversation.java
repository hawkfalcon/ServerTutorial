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
                        // .withPrefix(new Prefix())
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
            return ChatColor.translateAlternateColorCodes('&', "&6-------------------------------\n&8>&fWelcome to the &bServerTutorial&f view creation!\n&8>&7This will guide you adding a view to your tutorial\n&8>&7Currently adding view to the tutorial &b" + name + "&7!");
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
            return ChatColor.translateAlternateColorCodes('&', "&8>&7First, Choose a MessageType: META or TEXT\n&8>&6META &7- uses the item name\n&8>&6TEXT &7- normal text message\n&8>&7>&6> &7Type a MessageType to continue!");
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
            return ChatColor.translateAlternateColorCodes('&', "&8>&7>&6> &7Type what message this view should have:");


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
            return ChatColor.translateAlternateColorCodes('&', "&8>&7The view for tutorial &b" + name + "&7 has been successfully created!\n&8>&7It is a &f" + context.getSessionData("messagetype").toString() + " &7based view with message &f" + context.getSessionData("message").toString() + "&7!\n&6-------------------------------");
        }

        @Override
        public Prompt getNextPrompt(ConversationContext context) {
            Player player = (Player) context.getForWhom();
            String location = plugin.getServer().getPlayer(player.getName()).getLocation().getWorld().getName() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getX() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getY() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getZ() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getYaw() + "," + plugin.getServer().getPlayer(player.getName()).getLocation().getPitch();
            String message = context.getSessionData("message").toString();
            String messageType = context.getSessionData("messagetype").toString();
            String name = context.getSessionData("name").toString();
            int viewID = 1;
            while (plugin.getData().get("tutorials." + context.getSessionData("name") + ".views." + viewID) != null) {
                viewID++;
            }
            try {
                plugin.getData().set("tutorials." + name + ".views." + viewID + ".message", message);
                plugin.getData().set("tutorials." + name + ".views." + viewID + ".messagetype", messageType);
                plugin.getData().set("tutorials." + name + ".views." + viewID + ".location", location);
                plugin.saveData();
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