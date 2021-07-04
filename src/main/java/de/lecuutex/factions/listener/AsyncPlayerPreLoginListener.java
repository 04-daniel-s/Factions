package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class AsyncPlayerPreLoginListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        Factions.getInstance().registerPlayer(event.getUniqueId().toString(), event.getName());
    }
}
