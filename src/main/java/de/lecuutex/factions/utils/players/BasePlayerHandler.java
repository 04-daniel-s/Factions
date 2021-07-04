package de.lecuutex.factions.utils.players;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.FactionConfig;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
public class BasePlayerHandler {
    private final FactionConfig config = Factions.getInstance().getFactionConfig();

    private final HashMap<String, BasePlayer> basePlayers = new HashMap<>();

    private final HashMap<String, String> combatPlayers = new HashMap<>();

    private final ArrayList<String> enderpearlCooldown = new ArrayList<>();

    private final HashMap<Integer, Integer> levels = new HashMap<>();

    private final int attackDamageUpgrade = config.getInt("baseplayerhandler.attackdamageupgrade");

    private final int attackSpeedUpgrade = config.getInt("baseplayerhandler.attackspeedupgrade");

    private final double movementSpeedUpgrade = config.getDouble("baseplayerhandler.movementspeedupgrade");

    private final int healthUpgrade = config.getInt("baseplayerhandler.attackdamageupgrade");

    private final int armorUpgrade = config.getInt("baseplayerhandler.armorupgrade");

    private final int epCooldown = config.getInt("baseplayerhandler.epcooldown");
}
