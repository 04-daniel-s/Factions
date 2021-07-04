package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.faction.FactionHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class BlockExplodeListener implements Listener {
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getLocation().getBlock().getType() == Material.WATER) return;
        FactionHandler factionHandler = Factions.getInstance().getFactionHandler();
        Location location = event.getLocation();
        ArrayList<Block> blocks = new ArrayList<>();

        factionHandler.getFactions().forEach(f -> event.blockList().removeIf(block -> factionHandler.isInRaidArea(f, block.getLocation())));

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    Location loc = location.clone().add(new Vector(i, j, k));
                    if (loc.getBlock().getType() == Material.AIR) continue;
                    blocks.add(loc.getBlock());
                }
            }
        }

        for (Block block : blocks) {
            if (block.getType() == Material.OBSIDIAN) {

                if (!block.hasMetadata("obsidian")) {
                    block.setMetadata("obsidian", new FixedMetadataValue(Factions.getInstance(), 0));
                }

                if (block.getMetadata("obsidian").get(0).asInt() < 4) {
                    block.setMetadata("obsidian", new FixedMetadataValue(Factions.getInstance(), block.getMetadata("obsidian").get(0).asInt() + 1));
                } else {
                    block.getLocation().getBlock().setType(Material.AIR);
                }

            }
        }
    }
}
