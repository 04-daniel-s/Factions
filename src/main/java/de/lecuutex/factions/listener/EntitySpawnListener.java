package de.lecuutex.factions.listener;

import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Mob && !event.getEntity().isCustomNameVisible()) {
            event.setCancelled(true);
        }
    }
}
