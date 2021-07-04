package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.faction.Faction;
import de.lecuutex.factions.utils.faction.FactionHandler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class BlockPistonRetractListener implements Listener {
    @EventHandler
    public void onRetract(BlockPistonRetractEvent event) {
        FactionHandler factionHandler = Factions.getInstance().getFactionHandler();

        for (Block block : event.getBlocks()) {
            Location blockLocation = block.getLocation();
            for (Faction faction : factionHandler.getFactions()) {
                if (factionHandler.isInRaidArea(faction, event.getBlock().getLocation()) && !factionHandler.isInRaidArea(faction, blockLocation)) {
                    event.setCancelled(true);
                }
            }
        }

    }
}
