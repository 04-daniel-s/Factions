package de.lecuutex.factions.utils.faction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactionRank {
    private String factionID;
    private int id;
    private String uuid;
    private String name;
    private boolean level;
    private boolean clanChat;
    private boolean build;
    private boolean openChests;
    private boolean promote;
    private boolean demote;
    private boolean withdraw;
    private boolean rename;
    private boolean invite;
    private boolean kick;
    private boolean acceptTpa;
    private boolean startWar;
    private boolean modifyRanks;
    private boolean raid;

    public FactionRank(String uuid, int id, String factionID, String name, boolean level, boolean clanChat, boolean build, boolean openChests, boolean promote, boolean demote, boolean withdraw, boolean rename, boolean invite, boolean kick, boolean acceptTpa, boolean startWar, boolean modifyRanks, boolean raid) {
        this.id = id;
        this.factionID = factionID;
        this.uuid = uuid;
        this.name = name;
        this.level = level;
        this.clanChat = clanChat;
        this.build = build;
        this.openChests = openChests;
        this.promote = promote;
        this.demote = demote;
        this.withdraw = withdraw;
        this.rename = rename;
        this.invite = invite;
        this.kick = kick;
        this.acceptTpa = acceptTpa;
        this.startWar = startWar;
        this.modifyRanks = modifyRanks;
        this.raid = raid;
    }
}
