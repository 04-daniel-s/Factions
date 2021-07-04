package de.lecuutex.factions.utils.inventory;

import de.lecuutex.factions.Factions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;

@Getter
@Setter
public class CustomInventory {
    private Inventory inventory;
    private HashMap<Integer, CustomItem> items = new HashMap<>();
    private String title;
    private int size;

    public CustomInventory(int size, String title) {
        this.title = title;
        this.size = size;

        inventory = Bukkit.createInventory(null, size, title);
    }

    public CustomInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public CustomInventory setItem(int slot, ItemStack itemStack, Consumer<Player> consumer) {
        this.getItems().put(slot, new CustomItem(itemStack, consumer));
        this.getInventory().setItem(slot,itemStack);
        return this;
    }

}
