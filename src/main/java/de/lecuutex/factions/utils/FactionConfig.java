package de.lecuutex.factions.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class FactionConfig {

    private File file = new File("plugins/Factions/factionsConfig.yml");
    private YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

    public FactionConfig() {
        if (file.exists()) return;
        new File("plugins/Factions").mkdir();

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        configuration.set("currentlocation.x", 0);
        configuration.set("currentlocation.z", 0);
        configuration.set("xp-boost", 0);

        configuration.set("spawn.world", "world");
        configuration.set("spawn.x", 0);
        configuration.set("spawn.y", 100);
        configuration.set("spawn.z", 0);

        configuration.set("warzoneA.world", "world");
        configuration.set("warzoneA.x", 0);
        configuration.set("warzoneA.y", 0);
        configuration.set("warzoneA.z", 0);

        configuration.set("warzoneB.world", "world");
        configuration.set("warzoneB.x", 0);
        configuration.set("warzoneB.y", 256);
        configuration.set("warzoneB.z", 0);

        configuration.set("baseplayerhandler.attackdamageupgrade", 3);
        configuration.set("baseplayerhandler.attackspeedupgrade", 4);
        configuration.set("baseplayerhandler.movementspeedupgrade", 1.25);
        configuration.set("baseplayerhandler.healthupgrade", 3);
        configuration.set("baseplayerhandler.armorupgrade", 3);
        configuration.set("baseplayerhandler.epcooldown", 15);

        configuration.set("factionhandler.baseprotectiontime", 1000 * 60 * 60 * 60 * 24 * 2);
        configuration.set("factionhandler.factionlocationname", "standardvalue");
        configuration.set("factionhandler.baseenergy", 30);
        configuration.set("factionhandler.basemembersize", 4);
        configuration.set("factionhandler.factionradius", 80);
        configuration.set("factionhandler.raidarearadius", 30);

        configuration.set("raidhandler.neededenergy", 8);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getSpawn() {
        return new Location(Bukkit.getWorld(getString("spawn.world")), getDouble("spawn.x"), getDouble("spawn.y"), getDouble("spawn.z"));
    }

    public int getInt(String string) {
        return configuration.getInt(string);
    }

    public String getString(String string) {
        return configuration.getString(string);
    }

    public double getDouble(String string) {
        return configuration.getDouble(string);
    }
}
