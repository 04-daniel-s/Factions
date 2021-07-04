package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.FactionConfig;
import de.lecuutex.factions.utils.players.BasePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        HashMap<String, String> combatPlayers = Factions.getInstance().getBasePlayerHandler().getCombatPlayers();
        Player target = (Player) event.getEntity();
        Player damager = null;

        AtomicInteger combatTime = new AtomicInteger(Factions.getInstance().getBasePlayerHandler().getEpCooldown());
        AtomicInteger id = new AtomicInteger();

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) return;
            damager = (Player) ((Projectile) event.getDamager()).getShooter();
        }

        if (damager != null) {
            Player finalDamager = damager;

            if (!combatPlayers.containsKey(target.getName()) && !combatPlayers.containsValue(target.getName())) {
                if (damager.getName().equalsIgnoreCase(target.getName())) return;
                combatPlayers.put(target.getName(), damager.getName());
                target.sendMessage(Factions.getInstance().getPrefix() + "§cDu befindest dich nun im Kampf!");
                finalDamager.sendMessage(Factions.getInstance().getPrefix() + "§cDu befindest dich nun im Kampf!");


                id.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(Factions.getInstance(), () -> {
                    if (!finalDamager.getName().equals(combatPlayers.get(target.getName())))
                        Bukkit.getScheduler().cancelTask(id.get());
                    if (combatTime.get() == 0) {
                        combatPlayers.remove(target.getName());
                        Bukkit.getScheduler().cancelTask(id.get());

                        if (combatPlayers.get(target.getName()).equals(finalDamager.getName())) {
                            finalDamager.sendMessage(Factions.getInstance().getPrefix() + "§aDu befindest dich nicht mehr im Kampf!");
                            target.sendMessage(Factions.getInstance().getPrefix() + "§aDu befindest dich nicht mehr im Kampf!");
                        }
                        //TODO Cooldown anzeigen
                    }
                    combatTime.getAndDecrement();
                }, 0, 20));

            }
        }
    }
}
