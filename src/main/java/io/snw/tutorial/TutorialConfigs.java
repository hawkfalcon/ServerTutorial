
package io.snw.tutorial;

/**
 *
 * @author Frostalf
 */
public class TutorialConfigs {
    
    private boolean autoUpdate;
    private String signSetting;
    private boolean firstJoin;
    private String firstJoinTutorial;
    private boolean rewards;
    private boolean expCountdown;
    private double viewMoney;
    private float viewExp;
    private double tutorialMoney;
    private float tutorialExp;
    private boolean perTutorialMoney;
    private boolean perTutorialExp;
    private boolean perViewExp;
    private boolean perViewMoney;
    
    
    public TutorialConfigs(boolean autoUpdate, String signSetting, boolean firstJoin, String firstJoinTutorial, boolean rewards, boolean expCountdown, double viewMoney, float viewExp, 
            double tutorialMoney, float tutorialExp, boolean perTutorialMoney, boolean perTutorialExp, boolean perViewExp, boolean perViewMoney) {
        
        this.autoUpdate = autoUpdate;
        this.signSetting = signSetting;
        this.firstJoin = firstJoin;
        this.firstJoinTutorial = firstJoinTutorial;
        this.rewards = rewards;
        this.expCountdown = expCountdown;
        this.viewMoney = viewMoney;
        this.viewExp = viewExp;
        this.tutorialMoney = tutorialMoney;
        this.tutorialExp = tutorialExp;
        this.perTutorialMoney = perTutorialMoney;
        this.perTutorialExp = perTutorialExp;
        this.perViewExp = perViewExp;
        this.perViewMoney = perViewMoney;
    }
    
    public boolean getUpdate() {
        return this.autoUpdate;
    }
    
    public String signSetting() {
        return this.signSetting;
    }
    
    public boolean firstJoin() {
        return this.firstJoin;
    }
    
    public String firstJoinTutorial() {
        return this.firstJoinTutorial;
    }
    
    public boolean getRewards() {
        return this.rewards;
    }
    
    public boolean getExpCountdown() {
        return this.expCountdown;
    }
    
    public double getViewMoney() {
        return this.viewMoney;
    }
    
    public float getViewExp() {
        return this.viewExp;
    }
    
    public double getTutorialMoney() {
        return this.tutorialMoney;
    }
    
    public float getTutorialExp() {
        return this.tutorialExp;
    }
    
    public boolean getPerTutorialMoney() {
        return this.perTutorialMoney;
    }
    
    public boolean getPerViewMoney() {
        return this.perViewMoney;
    }
    
    public boolean getPerTutorialExp() {
        return this.perTutorialExp;
    }
    
    public boolean getPerViewExp() {
        return this.perViewExp;
    }

}
