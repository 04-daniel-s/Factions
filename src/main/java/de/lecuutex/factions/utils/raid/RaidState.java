package de.lecuutex.factions.utils.raid;

import lombok.Getter;

@Getter
public enum RaidState {
    SCOUTING(90),
    WAITING(60),
    RUNNING(60 * 45);

    private final int time;

    RaidState(int time) {
        this.time = time;
    }
}
