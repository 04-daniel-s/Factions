package de.lecuutex.factions.utils.faction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactionPlayer {
    private String name;
    private FactionRank rank;
    private String uuid;
    private String factionID;

    public FactionPlayer(String uuid,String name, FactionRank rank, String factionID) {
        this.name = name;
        this.rank = rank;
        this.factionID = factionID;
        this.uuid = uuid;
    }
}
