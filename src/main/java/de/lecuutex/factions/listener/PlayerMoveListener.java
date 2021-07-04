package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.faction.Faction;
import de.lecuutex.factions.utils.faction.FactionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerMoveListener implements Listener {
    FactionHandler factionHandler = Factions.getInstance().getFactionHandler();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Faction currentFaction = factionHandler.getFactionByLocation(player.getLocation());

        if (currentFaction != null) {
            if (!factionHandler.isInLocation(currentFaction.getHomes().get(factionHandler.getFactionLocationName()), event.getTo(), factionHandler.getRaidAreaRadius())) {
                Vector v1 = event.getFrom().toVector();
                Vector v2 = event.getTo().toVector();
                Vector v = new Vector(v1.getX() - v2.getX(), v1.getY() - v2.getY(), v1.getZ() - v2.getZ());
                player.setVelocity(v.normalize().multiply(1));
            }
        }
    }
}
