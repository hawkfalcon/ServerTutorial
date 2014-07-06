
package io.snw.tutorial.data;

import io.snw.tutorial.ServerTutorial;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.MessagePrompt;
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
            if(input.equalsIgnoreCase("autoupdater")) {
                return new AutoUpdater();
            } else if(input.equalsIgnoreCase("metrics")) {
                return new Metrics();
            } else if(input.equalsIgnoreCase("sign text")) {
                return new SignText();
            } else if(input.equalsIgnoreCase("first join")) {
                return new FirstJoin();
            } else if(input.equalsIgnoreCase("first join tutorial")) {
                return new FirstJoinTutorial();
            } else if(input.equalsIgnoreCase("rewards")) {
                return new Rewards();
            } else if(input.equalsIgnoreCase("exp countdown")) {
                return new ExpCountDown();
            } else if(input.equalsIgnoreCase("view money")) {
                return new ViewMoney();
            } else if(input.equalsIgnoreCase("view exp")) {
                return new ViewExp();
            } else if(input.equalsIgnoreCase("tutorial money")) {
                return new TutorialMoney();
            } else if(input.equalsIgnoreCase("tutorial exp")) {
                return new TutorialExp();
            } else if(input.equalsIgnoreCase("per tutorial money")) {
                return new PerTutorialMoney();
            } else if(input.equalsIgnoreCase("per tutorial exp")) {
                return new PerTutorialExp();
            } else if(input.equalsIgnoreCase("per view money")) {
                return new PerViewMoney();
            } else if(input.equalsIgnoreCase("per view exp")) {
                return new PerViewExp();
            } else if(input.equalsIgnoreCase("cancel")) {
                return END_OF_CONVERSATION;
            }
            return new ConfigOption();
        }
    }

    private class AutoUpdater extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>Current Setting for Auto-Updater: " + String.valueOf(plugin.getters().getConfigs().getUpdate()) + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                plugin.getConfig().set("auto-update", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
                return new Done();
            } else if (input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new AutoUpdater();
        }
    }

    private class Metrics extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>Current Setting for Metrics: " + String.valueOf(plugin.getters().getConfigs().getMetrics()) + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                plugin.getConfig().set("auto-update", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
                return new Done();
            } else if (input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Metrics();
        }
    }

    private class SignText extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>Current Setting for Sign Text: " + String.valueOf(plugin.getters().getConfigs().signSetting()) + "\n&8>Type what you want the Sign Text to be, also add any color codes. Can only be 15 characters long");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(!input.toLowerCase().equalsIgnoreCase("cancel") && input.length() <= 15) {
                plugin.getConfig().set("sign", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
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
            return ChatColor.translateAlternateColorCodes('&', "&8>Current Setting for First Join: " + String.valueOf(plugin.getters().getConfigs().firstJoin()) + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                plugin.getConfig().set("first_join", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
                return new Done();
            } else if(input.toLowerCase().equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new FirstJoin();
        }
    }

    private class FirstJoinTutorial extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>Current Setting for Auto-Updater: " + String.valueOf(plugin.getters().getConfigs().firstJoinTutorial()) + "\n&8>Valid inputs is a Tutorial name that exists or Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(!input.equalsIgnoreCase("cancel")) {
                if(plugin.caching().tutorial().containsKey(input)) {
                    plugin.getConfig().set("first_join_tutorial", input);
                    plugin.saveConfig();
                    plugin.caching().reCacheConfigs();
                    return new Done();
                }
            } else if(input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new FirstJoinTutorial();
        }
    }

    private class Rewards extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>Current Setting for Rewards: " + String.valueOf(plugin.getters().getConfigs().getRewards()) + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                plugin.getConfig().set("rewards", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
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
            return ChatColor.translateAlternateColorCodes('&', "Current Setting for Exp CountDown: " + String.valueOf(plugin.getters().getConfigs().getExpCountdown()) + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                plugin.getConfig().set("exp_countdown", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
                return new Done();
            } else if(input.toLowerCase().equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new ExpCountDown();
        }
    }

    private class ViewMoney extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "Current Setting for View Money: " + String.valueOf(plugin.getters().getConfigs().getPerViewMoney()) + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.toLowerCase().equalsIgnoreCase("true") || input.toLowerCase().equalsIgnoreCase("false")) {
                plugin.getConfig().set("view_money", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
                return new Done();
            } else if(input.toLowerCase().equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new ViewMoney();
        }
    }

    private class ViewExp extends StringPrompt {
        
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "Current Setting for View Exp: " + String.valueOf(plugin.getters().getConfigs().getPerViewExp()) + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                plugin.getConfig().set("view_exp", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
            } else if(input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Done();
        }
    }

    private class TutorialMoney extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "Current Setting for Turtorial Money: " + String.valueOf(plugin.getters().getConfigs().getPerTutorialMoney()) + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                plugin.getConfig().set("tutorial_money", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
            } else if(input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Done();
        }
    }

    private class TutorialExp extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "Current Setting for Tutorial Exp: " + String.valueOf(plugin.getters().getConfigs().getPerTutorialExp()) + "\n&8>Valid inputs are True, False, Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                plugin.getConfig().set("tutorial_exp", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
            } else if(input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Done();
        }
    }

    private class PerTutorialMoney extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "Current Setting for Turtorial Money Reward: " + String.valueOf(plugin.getters().getConfigs().getTutorialMoney()) + "\n&8>Valid inputs is a Number including decimal values or Cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(!Double.isNaN(Double.valueOf(input))) {
                plugin.getConfig().set("per_tutorial_money", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
            } else if (input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Done();
        }
    }

    private class PerTutorialExp extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "Current Setting for Turtorial Exp Reward: " + String.valueOf(plugin.getters().getConfigs().getTutorialExp()) + "\n&8>Valid inputs is a Number with 1 decimal place(Ex: 1.1) or cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(!Float.isNaN(Float.valueOf(input))) {
                plugin.getConfig().set("per_tutorial_exp", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
            } else if(input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Done();
        }
    }

    private class PerViewMoney extends StringPrompt {
        
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "Current Setting for View Money Reward: " + String.valueOf(plugin.getters().getConfigs().getViewMoney()) + "\n&8>Valid inputs a number or cancel(cancels changing the setting)");
        }
        
        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(!Double.isNaN(Double.valueOf(input))) {
                plugin.getConfig().set("per_view_money", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
            } else if(input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Done();
        }
    }

    private class PerViewExp extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "Current Setting for View Exp Reward: " + String.valueOf(plugin.getters().getConfigs().getViewExp()) + "\n&8>Valid inputs is a Number with 1 decimal place(Ex: 1.1) or cancel(cancels changing the setting)");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(!Float.isNaN(Float.valueOf(input))) {
                plugin.getConfig().set("per_view_exp", input);
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
            } else if(input.equalsIgnoreCase("cancel")) {
                return new ConfigOption();
            }
            return new Done();
        }
    }
    
    private class Done extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', "&8>Are you done changing config options? \n&8Valid inputs is yes or no.");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("yes")) {
                plugin.saveConfig();
                plugin.caching().reCacheConfigs();
                return END_OF_CONVERSATION;
            } else if (input.equalsIgnoreCase("no")) {
                return new ConfigOption();
            }
            return new Done();
        }
    }
}