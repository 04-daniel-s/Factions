package de.lecuutex.factions.utils.faction;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.FactionConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
public class FactionHandler {
    FactionConfig config = Factions.getInstance().getFactionConfig();

    private final ArrayList<Faction> factions = new ArrayList<>();

    private final HashMap<Integer, Integer> factionLevels = new HashMap<>();

    private final int xpBoost = config.getInt("xp-boost");

    private final int baseProtectionTime = config.getInt("factionhandler.baseprotectiontime");

    private final String factionLocationName = config.getString("factionhandler.factionlocationname");

    private final int baseEnergy = config.getInt("factionhandler.baseenergy");

    private final int baseMemberSize = config.getInt("factionhandler.basemembersize");

    private final int factionRadius = config.getInt("factionhandler.factionradius");

    private final int raidAreaRadius = factionRadius + config.getInt("factionhandler.raidarearadius");

    private final int current = config.getInt("currentfactions");

    private final HashMap<String, String> factionInvites = new HashMap<>();

    public Faction getFactionByPlayer(Player player) {
        for (Faction f : getFactions()) {
            for (FactionPlayer factionPlayer : f.getMember()) {
                if (!factionPlayer.getName().equals(player.getName())) continue;
                return f;
            }
        }
        return null;
    }

    public FactionRank getRankByPlayer(Player player) {
        for (FactionPlayer factionPlayer : getFactionByPlayer(player).getMember()) {
            if (!factionPlayer.getName().equals(player.getName())) continue;
            return factionPlayer.getRank();
        }
        return null;
    }

    public Faction getFactionByFactionName(String name) {
        for (Faction f : getFactions()) {
            if (!f.getName().equalsIgnoreCase(name)) continue;
            return f;
        }
        return null;
    }

    public Faction getFactionByFactionID(String id) {
        for (Faction f : getFactions()) {
            if (!f.getId().equals(id)) continue;
            return f;
        }
        return null;
    }

    public FactionPlayer getFactionPlayer(Player player) {
        for (Faction faction : getFactions()) {
            for (FactionPlayer factionPlayer : faction.getMember()) {
                if (!factionPlayer.getName().equals(player.getName())) continue;
                return factionPlayer;
            }
        }

        return null;
    }

    public FactionRank getRankByID(String factionID, int id) {
        for (Faction faction : factions) {
            if (!faction.getId().equals(factionID)) continue;
            for (FactionRank rank : faction.getRanks()) {
                if (rank.getId() != id) continue;
                return rank;
            }
        }
        return null;
    }

    public boolean isInFaction(Faction faction, Location location) {
        int radius = getFactionRadius();
        return isInLocation(faction, location, radius);
    }

    public boolean isInRaidArea(Faction faction, Location location) {
        int radius = getRaidAreaRadius();
        return isInLocation(faction, location, radius) && !isInFaction(faction, location);
    }

    private boolean isInLocation(Faction faction, Location location, int radius) {
        return isInLocation(faction.getHomes().get(getFactionLocationName()), location, radius);
    }

    public boolean isInLocation(Location loc, Location playerLoc, int radius) {
        Location min = loc.clone().add(-radius, 0, -radius);
        Location max = loc.clone().add(radius, 0, radius);
        min.setY(0);
        max.setY(256);

        return playerLoc.toVector().isInAABB(min.toVector(), max.toVector());
    }

    public Faction getFactionByLocation(Location location) {
        for (Faction faction : getFactions()) {
            if (!isInLocation(faction, location, getRaidAreaRadius())) continue;
            return faction;
        }
        return null;
    }

    public void sendFactionMessage(String message, Faction faction) {
        if (faction != null) {
            for (FactionPlayer member : faction.getMember()) {
                if (Bukkit.getPlayer(member.getName()) == null) continue;
                if (!Bukkit.getPlayer(member.getName()).isOnline()) continue;
                Bukkit.getPlayer(member.getName()).sendMessage(Factions.getInstance().getFactionChat() + message);
            }
        }
    }

    public String getFactionPlayerName(FactionPlayer factionPlayer) {
        return factionPlayer.getRank().getName() + " §7| " + factionPlayer.getRank().getName().substring(0, 2) + factionPlayer.getName();
    }
}
