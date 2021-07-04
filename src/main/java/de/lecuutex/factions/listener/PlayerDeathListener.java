package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.FactionConfig;
import de.lecuutex.factions.utils.faction.FactionHandler;
import de.lecuutex.factions.utils.players.BasePlayer;
import de.lecuutex.factions.utils.raid.RaidHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerDeathListener implements Listener {
    private final FactionConfig config = Factions.getInstance().getFactionConfig();
    private final FactionHandler factionHandler = Factions.getInstance().getFactionHandler();
    private final RaidHandler raidHandler = Factions.getInstance().getRaidHandler();
    private final String prefix = Factions.getInstance().getPrefix();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDroppedExp(0);
        if (!(event.getEntity() instanceof Player)) return;

        event.setDeathMessage("");
        Player player = event.getEntity();
        player.setGameMode(GameMode.SURVIVAL);

        if (factionHandler.getFactionByPlayer(player) != null && raidHandler.getRaids().containsKey(factionHandler.getFactionByPlayer(player).getId()) && raidHandler.getRaids().get(factionHandler.getFactionByPlayer(player).getId()).getPlayers().contains(player.getName())) {
            Factions.getInstance().getRaidHandler().getRaids().get(Factions.getInstance().getFactionHandler().getFactionByPlayer(player).getId()).getPlayers().remove(player.getName());
            Factions.getInstance().getFactionHandler().sendFactionMessage(factionHandler.getFactionPlayerName(factionHandler.getFactionPlayer(player)) + " §7ist gestorben und ist kein Teilnehmer mehr.", Factions.getInstance().getFactionHandler().getFactionByPlayer(player));
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.getInstance(), () -> {
            player.spigot().respawn();
            killPlayer(player);
            player.teleport(config.getSpawn());
        }, 1);

    }

    private void killPlayer(Player player) {
        HashMap<String, String> combatPlayers = Factions.getInstance().getBasePlayerHandler().getCombatPlayers();
        HashMap<String, BasePlayer> basePlayers = Factions.getInstance().getBasePlayerHandler().getBasePlayers();
        BasePlayer deadPlayer = basePlayers.get(player.getName());
        player.setHealth(player.getMaxHealth());

        if (combatPlayers.containsKey(player.getName())) { // Key leavt
            for (Map.Entry<String, String> entry : combatPlayers.entrySet()) {
                if (!entry.getKey().equals(player.getName())) continue;

                BasePlayer key = basePlayers.get(entry.getKey());

                if (!entry.getValue().equals(" ")) {
                    BasePlayer value = basePlayers.get(entry.getValue());
                    value.addMoney((int) (deadPlayer.getMoney() * 0.02));
                    value.addEXP((int) (deadPlayer.getExp() * 0.05) * deadPlayer.getExpMultiplier());
                    Bukkit.getPlayer(entry.getValue()).setLevel(value.getLevel());
                    Bukkit.getPlayer(entry.getValue()).setExp(value.getExp() / Factions.getInstance().getBasePlayerHandler().getLevels().get(value.getLevel()));

                    Bukkit.getPlayer(entry.getValue()).sendMessage(Factions.getInstance().getPrefix() + "§6§l+" + ((int) (deadPlayer.getExp() * 0.05)));
                    Bukkit.getPlayer(entry.getValue()).sendMessage(Factions.getInstance().getPrefix() + "Du hast §6" + ((int) (deadPlayer.getMoney() * 0.02)) + "$§7 erhalten!");
                } else {
                    Factions.getInstance().getBasePlayerHandler().getEnderpearlCooldown().remove(player.getName());
                }

                basePlayers.remove(player.getName());
                key.removeEXP((int) (deadPlayer.getExp() * 0.05));
                key.removeMoney((int) (deadPlayer.getMoney() * 0.02));

                if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(entry.getKey()))) {
                    Bukkit.broadcastMessage(Factions.getInstance().getPrefix() + "§4" + player.getName() + " §chat sich im Kampf ausgeloggt! §7(§6-" + ((int) (key.getMoney() * 0.02)) + "$§7)");
                }
            }

        } else if (combatPlayers.containsValue(player.getName())) { // value leavt
            for (Map.Entry<String, String> entry : combatPlayers.entrySet()) {
                if (!entry.getValue().equals(player.getName())) continue;

                BasePlayer value = basePlayers.get(entry.getValue());
                BasePlayer key = basePlayers.get(entry.getKey());
                Player keyPlayer = Bukkit.getPlayer(entry.getKey());

                key.addMoney((int) (value.getMoney() * 0.02));
                int multiplier = key.getExpMultiplier();
                //TODO Server multiplier hinzufügen
                //TODO Flaggen XP hinzufügen

                if (Factions.getInstance().getFactionHandler().getFactionByPlayer(Bukkit.getPlayer(entry.getKey())) != null) {
                    multiplier += Factions.getInstance().getFactionHandler().getFactionByPlayer(Bukkit.getPlayer(entry.getKey())).getExpMultiplier();
                }

                Factions.getInstance().addXP(keyPlayer, (int) (value.getExp() * 0.05) * multiplier);

                keyPlayer.sendMessage("§6§l+" + ((int) (value.getExp() * 0.05) * multiplier));
                keyPlayer.sendMessage(Factions.getInstance().getPrefix() + "Du hast §6" + ((int) (value.getMoney() * 0.02)) + "$§7 erhalten!");
                keyPlayer.setLevel(key.getLevel());
                keyPlayer.setExp(key.getExp() / Factions.getInstance().getBasePlayerHandler().getLevels().get(key.getLevel()));

                if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(entry.getValue()))) {
                    Bukkit.broadcastMessage(Factions.getInstance().getPrefix() + "§4" + player.getName() + " §chat sich im Kampf ausgeloggt! §7(§6-" + (value.getMoney() * 0.02) + "$§7)");
                }

                value.removeEXP((int) (value.getExp() * 0.05));
                value.removeMoney((int) (value.getMoney() * 0.02));
            }
        }

        player.setLevel(deadPlayer.getLevel());
        player.setExp((float) deadPlayer.getExp() / Factions.getInstance().getBasePlayerHandler().getLevels().get(deadPlayer.getLevel()));
    }
}
