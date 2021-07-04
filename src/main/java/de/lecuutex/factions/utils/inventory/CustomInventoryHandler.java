package de.lecuutex.factions.utils.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Listener;

import java.util.HashMap;

@Getter
@Setter
public class CustomInventoryHandler implements Listener {
    private HashMap<String, CustomInventory> customInventories = new HashMap<>();

}
