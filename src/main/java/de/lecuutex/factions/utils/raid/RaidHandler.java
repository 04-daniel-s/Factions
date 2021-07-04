package de.lecuutex.factions.utils.raid;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.FactionConfig;
import de.lecuutex.factions.utils.faction.Faction;
import de.lecuutex.factions.utils.faction.FactionHandler;
import de.lecuutex.factions.utils.faction.FactionPlayer;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class RaidHandler {
    //TODO Logs löschen können
    private final FactionConfig config = Factions.getInstance().getFactionConfig();

    private final int neededEnergy = config.getInt("raidhandler.neededenergy");

    private final String prefix = Factions.getInstance().getPrefix();

    private final FactionHandler factionHandler = Factions.getInstance().getFactionHandler();

    private final HashMap<String, FactionRaid> raids = new HashMap<>();

    public void startScouting(Player player, Faction faction) {
        Faction raider = factionHandler.getFactionByPlayer(player);
        FactionPlayer raiderPlayer = factionHandler.getFactionPlayer(player);

        if (raider == null) {
            player.sendMessage(prefix + "Du bist in keiner Faction.");
            return;
        }
        if (!raiderPlayer.getRank().isRaid() && !raiderPlayer.getUuid().equals(raider.getCreator())) {
            player.sendMessage(prefix + "Dazu hast du keine Rechte!");
            return;
        }
        if (getRaids().containsKey(raider.getId())) {
            player.sendMessage(prefix + "Du kannst nur eine Faction zeitgleich raiden.");
            return;
        }
        if (raider.getProtectionTime() >= System.currentTimeMillis()) {
            player.sendMessage(prefix + "Du kannst in der Schutzzeit keinen Raid starten.");
            return;
        }
        if (raider.getRaidEnergy() < neededEnergy) {
            player.sendMessage(prefix + "Die Faction benötigt mindestens §c" + getNeededEnergy() + " Energie, §7um einen Raid zu starten.");
            return;
        }

        ArrayList<String> players = new ArrayList<>();
        players.add(player.getName());
        raids.put(raider.getId(), new FactionRaid(faction.getId(), raider.getId(), players, RaidState.SCOUTING));

        player.teleport(faction.getHomes().get(factionHandler.getFactionLocationName()));
        factionHandler.sendFactionMessage(factionHandler.getFactionPlayerName(raiderPlayer) + " §7sucht nach einer gegnerischen Faction...", factionHandler.getFactionByFactionID(raiderPlayer.getFactionID()));

        player.setGameMode(GameMode.SPECTATOR);
        player.sendTitle("§aErkunde die Faction", "§7Du hast " + String.format("%02d:%02d", RaidState.SCOUTING.getTime() / 60, RaidState.SCOUTING.getTime() % 60) + " Minuten Zeit!", 10, 50, 10);
    }

    public void startWaiting(String raidingFaction) {
        FactionRaid raid = raids.get(raidingFaction);
        raid.setState(RaidState.WAITING);
        raid.setTime(RaidState.WAITING.getTime());
        factionHandler.getFactionByFactionID(raidingFaction).setRaidEnergy(factionHandler.getFactionByFactionID(raidingFaction).getRaidEnergy()-getNeededEnergy());

        TextComponent component = new TextComponent(" \n " + Factions.getInstance().getFactionChat() + "§lDie Faction §e" + factionHandler.getFactionByFactionID(raid.getRaidedFaction()).getName() + "§7 wird nun geraidet. Klicke hier, um beizutreten." + " \n ");
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/raid join"));

        factionHandler.getFactionByFactionID(raidingFaction).getMember().stream().filter(v -> Bukkit.getPlayer(v.getName()).isOnline()).forEach(v -> {
            Player p = Bukkit.getPlayer(v.getName());
            p.spigot().sendMessage(component);
        });

        raid.getPlayers().forEach(v -> Bukkit.getPlayer(v).setGameMode(GameMode.ADVENTURE));
        Faction raidedFaction = factionHandler.getFactionByFactionID(raid.getRaidedFaction());
        raidedFaction.getRaidedBy().add(raidingFaction);
    }

    public void startRaid(String raidingFaction) {
        FactionRaid raid = raids.get(raidingFaction);
        raid.setState(RaidState.RUNNING);
        raid.setTime(RaidState.RUNNING.getTime());

        for (String player : raid.getPlayers()) {
            Player p = Bukkit.getPlayer(player);
            p.setGameMode(GameMode.SURVIVAL);
            p.sendTitle("§cDer Raid wurde gestartet!", "", 10, 50, 10);
            teleportToRaidArea(p,factionHandler.getFactionByFactionID(raid.getRaidedFaction()));
        }
    }

    public void stopRaid(String raidingFaction) {
        FactionRaid raid = raids.get(raidingFaction);
        for (String player : raid.getPlayers()) {
            Player p = Bukkit.getPlayer(player);
            p.teleport(factionHandler.getFactionByFactionID(raidingFaction).getHomes().get(factionHandler.getFactionLocationName()));
        }

        factionHandler.sendFactionMessage("Der Raid gegen §e" + factionHandler.getFactionByFactionID(raid.getRaidedFaction()).getName() + "§7 wurde beendet.",factionHandler.getFactionByFactionID(raidingFaction));
        raids.remove(raidingFaction);
    }

    public RaidHandler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Factions.getInstance(), () -> {
            for (Map.Entry<String, FactionRaid> raids : getRaids().entrySet()) {
                String uuid = raids.getKey();
                FactionRaid raid = raids.getValue();


                if (raid.getState() == RaidState.SCOUTING) {
                    updateTime(raid,"§aErkunde §e" + factionHandler.getFactionByFactionID(raid.getRaidedFaction()).getName() + "§a für §2" + String.format("%02d:%02d", raid.getTime() / 60, raid.getTime() % 60) + " Minuten");

                    if (raid.getTime() == 0) {
                        startWaiting(uuid);
                    }

                } else if (raid.getState() == RaidState.WAITING) {
                    updateTime(raid,"§aDu hast noch §2" + String.format("%02d:%02d", raid.getTime() / 60, raid.getTime() % 60) + " Minuten §aVorbereitungszeit");

                    if (raid.getTime() == 0) {
                        startRaid(uuid);
                    }

                } else if (raid.getState() == RaidState.RUNNING) {
                    updateTime(raid,"§cDer Raid endet in §4" + String.format("%02d:%02d", raid.getTime() / 60, raid.getTime() % 60) + " Minuten");
                    if (raid.getTime() == 0) {
                        stopRaid(uuid);
                    }

                }
            }
        }, 0, 20);
    }

    public void updateTime(FactionRaid raid, String text) {
        if (raid.getTime() > 0) {
            raid.setTime(raid.getTime() - 1);

            for (String player : raid.getPlayers()) {
                Bukkit.getPlayer(player).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
            }
        }
    }

    public void teleportToRaidArea(Player player, Faction faction) {
        FactionRaid raid = Factions.getInstance().getRaidHandler().getRaids().get(faction.getId());
        player.teleport(factionHandler.getFactionByFactionID(raid.getRaidedFaction()).getHomes().get(factionHandler.getFactionLocationName()).clone().add(factionHandler.getRaidAreaRadius(), 0, factionHandler.getRaidAreaRadius()));
    }
}
