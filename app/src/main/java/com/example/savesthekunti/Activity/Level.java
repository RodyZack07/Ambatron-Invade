package com.example.savesthekunti.Activity;

import java.io.Serializable;

public class Level implements Serializable {
    public int levelNumber;
    public int monsterMiniHp;
    public int monsterMiniDamage;
    public int bossAmbaHp;
    public int bossImageSrc;

    // Constructor, getters, and setters
    public Level(int levelNumber, int monsterMiniHp, int monsterMiniDamage, int bossAmbaHp, int bossImageSrc ) {
        this.levelNumber = levelNumber;
        this.monsterMiniHp = monsterMiniHp;
        this.monsterMiniDamage = monsterMiniDamage;
        this.bossAmbaHp = bossAmbaHp;
        this.bossImageSrc = bossImageSrc;
    }

    // Getters
    public int getBossImageSrc() {
        return bossImageSrc;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public int getMonsterMiniHp() {
        return monsterMiniHp;
    }

    public int getMonsterMiniDamage() {
        return monsterMiniDamage;
    }

    public int getBossAmbaHp() {
        return bossAmbaHp;
    }

}
