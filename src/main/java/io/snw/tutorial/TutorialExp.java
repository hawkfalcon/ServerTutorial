
package io.snw.tutorial;


public class TutorialExp {
    
    private float exp;
    private String playerName;
    
    public TutorialExp(String playerName, float exp) {
        this.playerName = playerName;
        this.exp = exp;
    }
    
    public float getExp() {
        return this.exp;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }

}
