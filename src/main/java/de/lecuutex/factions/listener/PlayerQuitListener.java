package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage("");
        Player player = event.getPlayer();
    }
}
