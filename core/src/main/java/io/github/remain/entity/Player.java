package io.github.remain.entity;

public class Player {
    private int lvl = 0;
    private int xp = 0;
    private int xptolvlup = 100;


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
    public int getXptolvlup() {
        return xptolvlup;
    }

}
