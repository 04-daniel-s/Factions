package de.lecuutex.factions.utils.faction;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class Faction {
    private String id;
    private String name;
    private String creator;
    private ArrayList<FactionPlayer> member;
    private ArrayList<FactionRank> ranks;
    private ArrayList<String> raidedBy;
    private HashMap<String,Location> homes;
    private int elo;
    private int expMultiplier;
    private int protectionTime;
    private int raidEnergy;
    private int money;
    private int level; // erweitert slots
    private int slots;

    public Faction(String id, String name, String creator, ArrayList<FactionPlayer> member, ArrayList<FactionRank> ranks, ArrayList<String> raidedBy, HashMap<String,Location> homes, int elo, int expMultiplier, int protectionTime, int raidEnergy, int money, int level, int slots) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.member = member;
        this.ranks = ranks;
        this.raidedBy = raidedBy;
        this.homes = homes;
        this.elo = elo;
        this.expMultiplier = expMultiplier;
        this.protectionTime = protectionTime;
        this.raidEnergy = raidEnergy;
        this.money = money;
        this.level = level;
        this.slots = slots;
    }
}
