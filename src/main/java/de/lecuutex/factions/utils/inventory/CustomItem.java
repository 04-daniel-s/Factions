package de.lecuutex.factions.utils.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Getter
@Setter
public class CustomItem {
    private ItemStack itemStack;
    private Consumer<Player> consumer;

    public CustomItem(ItemStack itemStack, Consumer<Player> consumer) {
        this.itemStack = itemStack;
        this.consumer = consumer;
    }
}
