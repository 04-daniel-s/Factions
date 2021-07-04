package de.lecuutex.factions.utils.backpacks;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Setter
@Getter
public class Backpack {
    private String uuid;
    private String owner;
    private int size;
    private ItemStack[] items;

    public Backpack(String uuid, String owner, int size, ItemStack[] items) {
        this.uuid = uuid;
        this.owner = owner;
        this.size = size;
        this.items = items;
    }
}
