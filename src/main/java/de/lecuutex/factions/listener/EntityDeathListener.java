package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity().getKiller() != null || !(event.getEntity().getKiller() instanceof Player)) return;
        Player player = event.getEntity().getKiller();
        event.setDroppedExp(0);

        //TODO: alle Monster eintragen
        int xp = 5;
        if(event.getEntity().getType() == EntityType.BLAZE) xp+=5;
        Factions.getInstance().addXP(player,xp);
    }
}
