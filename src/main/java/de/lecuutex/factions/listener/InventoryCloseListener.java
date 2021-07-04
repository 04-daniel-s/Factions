package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.backpacks.Backpack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();

        if (event.getView().getTitle().equals("§6Backpack")) {
            Factions.getInstance().getBackpackHandler().getBackpacks().put(player.getUniqueId().toString(), new Backpack(player.getUniqueId().toString(), player.getName(), inventory.getSize(), inventory.getContents()));
        }

    }
}
