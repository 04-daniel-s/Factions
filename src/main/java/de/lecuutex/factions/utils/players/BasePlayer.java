package de.lecuutex.factions.utils.players;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasePlayer {
    private String uuid;
    private String name;
    int money;
    int expMultiplier;
    private int level;
    private int exp;
    private int skillpoints;
    private int health;
    private int armor;
    private int attackDamage;
    private int attackSpeed;
    private int movementSpeed;

    public BasePlayer(String uuid, String name, int money, int health, int armor, int expMultiplier, int level, int exp, int skillpoints, int attackDamage, int attackSpeed, int movementSpeed) {
        this.uuid = uuid;
        this.name = name;
        this.level = level;
        this.exp = exp;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.health = health;
        this.movementSpeed = movementSpeed;
        this.armor = armor;
        this.skillpoints = skillpoints;
        this.money = money;
        this.expMultiplier = expMultiplier;
    }

    public void addMoney(int i) {
        money += i;
    }

    public void removeEXP(int i) {
        exp -= i;
    }

    public void addEXP(int i) {
        exp += i;
    }

    public void removeMoney(int i) {
        money -= i;
    }
}
