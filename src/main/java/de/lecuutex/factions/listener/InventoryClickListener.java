package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.inventory.CustomItem;
import de.lecuutex.factions.utils.players.BasePlayer;
import de.lecuutex.factions.utils.inventory.CustomInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;

        String title = event.getView().getTitle();
        ItemStack currentItem = event.getCurrentItem();
        String itemName = currentItem.getItemMeta().getDisplayName();
        Player player = (Player) event.getWhoClicked();
        BasePlayer basePlayer = Factions.getInstance().getBasePlayerHandler().getBasePlayers().get(player.getName());

        if (title.equals("§d§lSkill")) {
            event.setCancelled(true);

            double baseAttackDamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getDefaultValue();
            double baseAttackSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getDefaultValue();
            double baseMovementSpeed = 0.1;
            double baseHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
            double baseArmor = 2;

            if (event.getSlot() < 10 || event.getSlot() > 15) return;

            if (basePlayer.getSkillpoints() == 0 || event.getCurrentItem().getItemMeta().getLore().contains("§c§lDu hast die maximale Stufe erreicht!")) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.1F, 1);
                return;
            }

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1F, 1);

            if (currentItem.getType() == Material.STONE_SWORD || itemName.contains("Attack Damage")) {
                basePlayer.setAttackDamage(basePlayer.getAttackDamage() + 1);
                basePlayer.setSkillpoints(basePlayer.getSkillpoints() - 1);
                player.openInventory(Factions.getInstance().getInventoryHandler().getSkillInventory(player));
                player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(baseAttackDamage + (baseAttackDamage * ((float) basePlayer.getAttackDamage() * Factions.getInstance().getBasePlayerHandler().getAttackDamageUpgrade() / 100)));

            } else if (currentItem.getType() == Material.FEATHER || itemName.contains("Attack Speed")) {
                basePlayer.setAttackSpeed(basePlayer.getAttackSpeed() + 1);
                basePlayer.setSkillpoints(basePlayer.getSkillpoints() - 1);
                player.openInventory(Factions.getInstance().getInventoryHandler().getSkillInventory(player));
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(baseAttackSpeed + (baseAttackSpeed * ((float) basePlayer.getAttackSpeed() * Factions.getInstance().getBasePlayerHandler().getAttackSpeedUpgrade() / 100)));

            } else if (currentItem.getType() == Material.GOLDEN_BOOTS || itemName.contains("Movement Speed")) {
                basePlayer.setMovementSpeed(basePlayer.getMovementSpeed() + 1);
                basePlayer.setSkillpoints(basePlayer.getSkillpoints() - 1);
                player.openInventory(Factions.getInstance().getInventoryHandler().getSkillInventory(player));
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(baseMovementSpeed + (baseMovementSpeed * ((float) basePlayer.getMovementSpeed() * Factions.getInstance().getBasePlayerHandler().getMovementSpeedUpgrade() / 100)));

            } else if (currentItem.getType() == Material.APPLE || itemName.contains("Health")) {
                basePlayer.setHealth(basePlayer.getHealth() + 1);
                basePlayer.setSkillpoints(basePlayer.getSkillpoints() - 1);
                player.openInventory(Factions.getInstance().getInventoryHandler().getSkillInventory(player));
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(baseHealth + (baseHealth * ((float) basePlayer.getHealth() * Factions.getInstance().getBasePlayerHandler().getHealthUpgrade() / 100)));

            } else if (currentItem.getType() == Material.IRON_CHESTPLATE || itemName.contains("Armor")) {
                basePlayer.setArmor(basePlayer.getArmor() + 1);
                basePlayer.setSkillpoints(basePlayer.getSkillpoints() - 1);
                player.openInventory(Factions.getInstance().getInventoryHandler().getSkillInventory(player));
                player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(baseArmor * ((float) basePlayer.getArmor() * Factions.getInstance().getBasePlayerHandler().getArmorUpgrade() / 100));

            }
        }
        acceptConsumer(event);
    }

    private void acceptConsumer(InventoryClickEvent event) {
        for (CustomInventory customInventory : Factions.getInstance().getCustomInventoryHandler().getCustomInventories().values()) {
            if (!customInventory.getTitle().equals(event.getView().getTitle())) continue;
            for (Map.Entry<Integer, CustomItem> entry : customInventory.getItems().entrySet()) {
                if (entry.getKey() == event.getSlot()) {
                    entry.getValue().getConsumer().accept((Player) event.getWhoClicked());
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}

