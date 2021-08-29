package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String prefix = Factions.getInstance().getPrefix();

        if (event.getMessage().startsWith("/help")) {
            event.setCancelled(true);
            player.sendMessage(prefix + "§m---------- §cFactions §m----------");
            player.sendMessage(prefix + "/Spawn - Teleportiere dich zum Spawn");
            player.sendMessage(prefix + "/Factions help - Öffne das Factions-Menü");
            player.sendMessage(prefix + "/Raid help - Öffne das Raid-Menü");
            player.sendMessage(prefix + "/Skill - Erhöhe deine Fertigkeiten");
            player.sendMessage(prefix + "/Backpack - Öffne dein Backpack");
        }
    }
}
