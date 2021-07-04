package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.faction.Faction;
import de.lecuutex.factions.utils.faction.FactionHandler;
import de.lecuutex.factions.utils.faction.FactionPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Location blockLocation = event.getBlockPlaced().getLocation();
        FactionHandler factionHandler = Factions.getInstance().getFactionHandler();
        Faction faction = factionHandler.getFactionByPlayer(event.getPlayer());
        event.setCancelled(true);

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(false);
        }

        if (faction != null) {
            if (factionHandler.isInFaction(faction, blockLocation)) {
                FactionPlayer fp = faction.getMember().stream().filter(v -> v.getName().equals(event.getPlayer().getName())).findFirst().get();
                if (fp.getRank().isBuild()) {
                    event.setCancelled(false);
                }
            }

            for (Faction f : factionHandler.getFactions()) {
                if (!factionHandler.isInRaidArea(f, blockLocation)) continue;
                for (FactionPlayer factionPlayer : f.getMember()) {
                    if (factionPlayer.getName().equalsIgnoreCase(event.getPlayer().getName())) continue;
                    event.setCancelled(false);
                }
            }
        }
    }
}
