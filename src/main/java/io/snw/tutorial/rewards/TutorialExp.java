
package io.snw.tutorial.rewards;


public class TutorialExp {

    private int exp;
    private String playerName;
    
    public TutorialExp(String playerName, int exp) {
        this.playerName = playerName;
        this.exp = exp;
    }

    public int getExp() {
        return this.exp;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}
