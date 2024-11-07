package com.example.savesthekunti.Activity;

import java.io.Serializable;

public class Level implements Serializable {
    public int levelNumber;
    public int monsterMiniHp;
    public int monsterMiniDamage;
    public int bossAmbaHp;
    public int rudalDurability;
    public int rudalDamage;

    // Constructor, getters, and setters
    public Level(int levelNumber, int monsterMiniHp, int monsterMiniDamage, int bossAmbaHp, int rudalDurability, int rudalDamage) {
        this.levelNumber = levelNumber;
        this.monsterMiniHp = monsterMiniHp;
        this.monsterMiniDamage = monsterMiniDamage;
        this.bossAmbaHp = bossAmbaHp;
        this.rudalDurability = rudalDurability;
        this.rudalDamage = rudalDamage;
    }

    // Getters
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

    public int getRudalDurability() {
        return rudalDurability;
    }

    public int getRudalDamage() {
        return rudalDamage;
    }
}