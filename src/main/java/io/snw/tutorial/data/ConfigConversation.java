
package io.snw.tutorial.data;

import io.snw.tutorial.ServerTutorial;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import static org.bukkit.conversations.Prompt.END_OF_CONVERSATION;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConfigConversation {

    ServerTutorial plugin;
    private ConversationFactory factory;

    public ConfigConversation(ServerTutorial plugin) {
        this.plugin = plugin;
    }

    public void editConfig(Player player) {
        this.factory = new ConversationFactory(plugin)
                .withModality(true)
                .withFirstPrompt(new Welcome())
                .withEscapeSequence("/quit")
                .withTimeout(60)
                .thatExcludesNonPlayersWithMessage("You must be in game!");
        factory.buildConversation(player).begin();
    }

    private class Welcome extends MessagePrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&6-------------------------------\n&8>&fWelcome to the &bServerTutorial&f config Editor!\n&8>&7This will guide you in editing config options!");
        }

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return new ConfigOption();
        }
    }

    private class ConfigOption extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>&7First,type which config option you want to change\n&8>AutoUpdater, Sign Text, First Join, First Join Tutorial\n&8>Rewards, Exp Countdown, View Money, View Exp\n&8>Tutorial Money, Tutorial Exp, Per Tutorial Money, Per Tutorial Exp\n&8>Per View Money, Per View Exp");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.toLowerCase().equalsIgnoreCase("autoupdater")) {
                return new AutoUpdater();
            } else if(input.toLowerCase().equalsIgnoreCase("sign text")) {
                return new SignText();
            } else if(input.toLowerCase().equalsIgnoreCase("first join")) {
                return new FirstJoin();
            } else if(input.toLowerCase().equalsIgnoreCase("first join tutorial")) {
                return new FirstJoinTutorial();
            } else if(input.toLowerCase().equalsIgnoreCase("rewards")) {
                return new Rewards();
            } else if(input.toLowerCase().equalsIgnoreCase("exp countdown")) {
                return new ExpCountDown();
            } else if(input.toLowerCase().equalsIgnoreCase("view money")) {
                return new ViewMoney();
            } else if(input.toLowerCase().equalsIgnoreCase("view exp")) {
                return new ViewExp();
            } else if(input.toLowerCase().equalsIgnoreCase("tutorial money")) {
                return new TutorialMoney();
            } else if(input.toLowerCase().equalsIgnoreCase("tutorial exp")) {
                return new TutorialExp();
            } else if(input.toLowerCase().equalsIgnoreCase("per tutorial money")) {
                return new PerTutorialMoney();
            } else if(input.toLowerCase().equalsIgnoreCase("per tutorial exp")) {
                return new PerTutorialExp();
            } else if(input.toLowerCase().equalsIgnoreCase("per view money")) {
                return new PerViewMoney();
            } else if(input.toLowerCase().equalsIgnoreCase("per view exp")) {
                return new PerViewExp();
            } else if(input.toLowerCase().equalsIgnoreCase("cancel")) {
                return END_OF_CONVERSATION;
            }
            return new ConfigOption();
        }
    }

    private class AutoUpdater extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>Current Setting for Auto-Updater: " + String.valueOf(plugin.getters().getConfigs().getUpdate() + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)"));
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.toLowerCase().equalsIgnoreCase("true") || input.toLowerCase().equalsIgnoreCase("false")) {
                context.setSessionData("autoupdate", input.toLowerCase());
                return new Done();
            } else if (input.toLowerCase().equals("cancel")) {
                return new ConfigOption();
            }
            return new AutoUpdater();
        }
    }

    private class SignText extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>Current Setting for Sign Text: " + String.valueOf(plugin.getters().getConfigs().signSetting() + "\n&8>Type what you want the Sign Text to be, also add any color codes."));
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(!input.toLowerCase().equalsIgnoreCase("cancel")) {
                context.setSessionData("signtext", input);
                return new Done();
            } else if(input.toLowerCase().equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new SignText();
        }
    }

    private class FirstJoin extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.toLowerCase().equalsIgnoreCase("true") || input.toLowerCase().equalsIgnoreCase("false")) {
                context.setSessionData("firstjoin", input.toLowerCase());
                return new Done();
            } else if(input.toLowerCase().equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Done();
        }
    }

    private class FirstJoinTutorial extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(!input.toLowerCase().equalsIgnoreCase("cancel")) {
                context.setSessionData("firstjointutorial", input);
                return new Done();
            } else if(input.toLowerCase().equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new FirstJoinTutorial();
        }
    }

    private class Rewards extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.toLowerCase().equalsIgnoreCase("true") || input.toLowerCase().equalsIgnoreCase("false")) {
                context.setSessionData("rewards", input.toLowerCase());
                return new Done();
            } else if(input.toLowerCase().equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Rewards();
        }
    }

    private class ExpCountDown extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "Current input for ExpCountDown: " + plugin.getConfig());
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.toLowerCase().equalsIgnoreCase("true") || input.toLowerCase().equalsIgnoreCase("false")) {
                context.setSessionData("expcountdown", input.toLowerCase());
                return new Done();
            } else if(input.toLowerCase().equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return END_OF_CONVERSATION;
        }
    }

    private class ViewMoney extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }
        
        @Override
        public Prompt acceptValidatedInput(ConversationContext context, Number input) {
            if(isNumberValid(context, input.floatValue())) {
                context.setSessionData("viewmoney", input.floatValue());
            }
            return END_OF_CONVERSATION;
        }
    }

    private class ViewExp extends StringPrompt {
        
        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            return END_OF_CONVERSATION;
        }
    }

    private class TutorialMoney extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            return END_OF_CONVERSATION;
        }
    }

    private class TutorialExp extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            return END_OF_CONVERSATION;
        }
    }

    private class PerTutorialMoney extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            return END_OF_CONVERSATION;
        }
    }

    private class PerTutorialExp extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            return END_OF_CONVERSATION;
        }
    }

    private class PerViewMoney extends StringPrompt {
        
        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }
        
        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            return END_OF_CONVERSATION;
        }
    }

    private class PerViewExp extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            return END_OF_CONVERSATION;
        }
    }
    
    private class Done extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return "nothing";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            return END_OF_CONVERSATION;
        }
    }
}