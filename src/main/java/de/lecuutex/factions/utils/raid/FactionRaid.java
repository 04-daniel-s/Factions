package de.lecuutex.factions.utils.raid;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class FactionRaid {
    private String raidingFaction;
    private String raidedFaction;
    private ArrayList<String> players;
    private int time;
    private RaidState state;

    public FactionRaid(String raidingFaction, String raidedFaction, ArrayList<String> players, RaidState state) {
        this.raidingFaction = raidingFaction;
        this.raidedFaction = raidedFaction;
        this.players = players;
        this.state = state;
        time = state.getTime();
    }
}
