package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.players.BasePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        HashMap<String, String> combatPlayers = Factions.getInstance().getBasePlayerHandler().getCombatPlayers();
        ArrayList<String> enderpearlCooldown = Factions.getInstance().getBasePlayerHandler().getEnderpearlCooldown();

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Block block = event.getClickedBlock();
        Action action = event.getAction();
        String displayName = "";
        if (itemStack != null) displayName = itemStack.getItemMeta().getDisplayName();

        AtomicInteger id = new AtomicInteger();
        AtomicInteger cooldown = new AtomicInteger(15);

        if (block != null) {
            if (block.getType() == Material.ANVIL) event.setCancelled(true);
            if (block.getType() == Material.CHIPPED_ANVIL) event.setCancelled(true);
            if (block.getType() == Material.DAMAGED_ANVIL) event.setCancelled(true);
            if (block.getType() == Material.ENCHANTING_TABLE) event.setCancelled(true);
        }

        if (action == Action.RIGHT_CLICK_AIR) {
            if (itemStack.getType() == Material.ENDER_PEARL) {

                if (enderpearlCooldown.contains(player.getName())) {
                    player.sendMessage(Factions.getInstance().getPrefix() + "Deine Enderperle hat einen Cooldown.");
                    event.setCancelled(true);
                    return;
                }

                if (!combatPlayers.containsKey(player.getName()) && !combatPlayers.containsValue(player.getName())) {
                    combatPlayers.put(player.getName(), " ");
                    enderpearlCooldown.add(player.getName());
                    player.sendMessage(Factions.getInstance().getPrefix() + "Du befindest dich nun im Kampf!");

                    id.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(Factions.getInstance(), () -> {
                        if (cooldown.decrementAndGet() == 0) {
                            enderpearlCooldown.remove(player.getName());
                            combatPlayers.remove(player.getName());
                            Bukkit.getScheduler().cancelTask(id.get());
                            player.sendMessage(Factions.getInstance().getPrefix() + "Du befindest dich nicht mehr im Kampf!");
                        }
                    }, 0, 20));
                } else {
                    enderpearlCooldown.add(player.getName());
                    id.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(Factions.getInstance(), () -> {
                        if (cooldown.decrementAndGet() == 0) {
                            enderpearlCooldown.remove(player.getName());
                            Bukkit.getScheduler().cancelTask(id.get());
                        }
                    }, 0, 20));
                }
            }
        }

        if (itemStack != null && itemStack.getType() == Material.EXPERIENCE_BOTTLE) {
            event.setCancelled(true);
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 0.1F, 1);

                if (displayName.equals("25 XP")) {
                    Factions.getInstance().addXP(player, 25);
                    player.sendMessage("§6§l+" + 25 + " XP");
                } else if (displayName.contains("75 XP")) {
                    Factions.getInstance().addXP(player, 75);
                    player.sendMessage("§6§l+" + 75 + " XP");
                } else if (displayName.contains("125 XP")) {
                    Factions.getInstance().addXP(player, 125);
                    player.sendMessage("§6§l+" + 125 + " XP");
                } else if (displayName.contains("200 XP")) {
                    Factions.getInstance().addXP(player, 200);
                    player.sendMessage("§6§l+" + 200 + " XP");
                }
            }
        }
    }
}
