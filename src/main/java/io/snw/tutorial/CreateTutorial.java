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
                        //.withPrefix(new Prefix())
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
            return ChatColor.translateAlternateColorCodes('&', "&6-------------------------------\n&8>&fWelcome to the &bServerTutorial&f tutorial creation!\n&8>&7This will guide you through creating a new Tutorial\n&8>&7Currently creating new tutorial with name: &b" + name + "!");
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
            return ChatColor.translateAlternateColorCodes('&', "&8>&7First, choose a ViewType: CLICK or TIME\n&8>&6CLICK &7- cycle though by clicking\n&8>&6TIME &7- automated with a timer\n&8>&7>&6> &7Type a ViewType to continue!");
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
            return ChatColor.translateAlternateColorCodes('&', "&8>&7>&6> &7Type how long should each view last (in seconds):");

        }

        @Override
        public Prompt acceptValidatedInput(ConversationContext context, Number input) {
            if (input.intValue() < 0) {
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
            writeNewTutorial(name, context.getSessionData("viewtype").toString(), context.getSessionData("endmessage").toString(), context.getSessionData("timelength"));
            return END_OF_CONVERSATION;
        }
    }

    private class Prefix implements ConversationPrefix {

        @Override
        public String getPrefix(ConversationContext context) {
            return ChatColor.AQUA + "[" + ChatColor.GRAY + "Tutorial" + ChatColor.AQUA + "] " + ChatColor.WHITE;
        }
    }

    public void writeNewTutorial(String name, String viewType, String endMessage, Object timeLength) {
        plugin.getConfig().set("tutorials." + name + ".viewtype", viewType);
        if (timeLength != null) {
            plugin.getConfig().set("tutorials." + name + ".timelength", timeLength.toString());
        } else {
            plugin.getConfig().set("tutorials." + name + ".timelength", "0");
        }
        plugin.getConfig().set("tutorials." + name + ".endmessage", endMessage);
        plugin.getConfig().set("tutorials." + name + ".item", "stick");
        plugin.saveConfig();
        plugin.reCasheTutorials();
    }
}