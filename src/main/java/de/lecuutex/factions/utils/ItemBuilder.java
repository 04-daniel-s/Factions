package de.lecuutex.factions.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta itemMeta;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material);
        this.item.setAmount(amount);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... text) {
        itemMeta.setLore(Arrays.asList(text));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder setSkullOwner(Player player) {
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setOwner(player.getName());

        return this;
    }

    public ItemBuilder setUnbreakable() {
        itemMeta.setUnbreakable(true);
        return  this;
    }

    public ItemBuilder setColor(Color color) {
        ((LeatherArmorMeta) itemMeta).setColor(color);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int i) {
        itemMeta.addEnchant(enchantment, i, true);
        return this;
    }
    public ItemBuilder addItemFlags(ItemFlag... flag) {
        itemMeta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder addAttribute(Attribute attribute, double amount) {
        itemMeta.addAttributeModifier(attribute, new AttributeModifier(attribute.getKey().getKey(), amount, AttributeModifier.Operation.ADD_NUMBER));
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }
}
