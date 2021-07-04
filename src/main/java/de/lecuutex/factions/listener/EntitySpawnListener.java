package de.lecuutex.factions.listener;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Monster || event.getEntity() instanceof Animals && !event.getEntity().isCustomNameVisible()) {
            event.setCancelled(true);
        }
    }
}
