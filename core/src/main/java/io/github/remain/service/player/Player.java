package io.github.remain.service.player;
import io.github.remain.service.player.Player;

public class Player {
    private int lvl = 0;
    private int xp = 0;
    private int xptolvlup = 100;
    private float health = 100;
    private float maxHealth = 100;



    public void addxp(int amount){
        xp += amount;
        while (xp >= xptolvlup){
            lvlup();
        }
    }

    private void lvlup(){
        xp -= xptolvlup;
        lvl++;
        xptolvlup += 50;
    }

    public int getLvl() {
        return lvl;
    }
    public int getXp() {
        return xp;
    }
    public int getXptoNextLevel() {
        return xptolvlup;
    }

    public float getHealth() { return health; }
public float getMaxHealth() { return maxHealth; }


}

