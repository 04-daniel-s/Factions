package de.lecuutex.factions.commands;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.faction.Faction;
import de.lecuutex.factions.utils.faction.FactionHandler;
import de.lecuutex.factions.utils.raid.FactionRaid;
import de.lecuutex.factions.utils.raid.RaidHandler;
import de.lecuutex.factions.utils.raid.RaidState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RaidCommand implements CommandExecutor {
    private final FactionHandler factionHandler = Factions.getInstance().getFactionHandler();
    private final RaidHandler raidHandler = Factions.getInstance().getRaidHandler();
    private final String prefix = Factions.getInstance().getPrefix();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionHandler.getFactionByPlayer(player);
        ArrayList<Faction> factions = factionHandler.getFactions();
        HashMap<String, FactionRaid> raids = raidHandler.getRaids();
        FactionRaid raid = null;

        if (args.length < 1) {
            if (faction == null) {
                player.sendMessage(prefix + "Dazu musst du in einer Faction sein.");
                return true;
            }

            Random random = new Random();
            Faction f;

            do {
                f = factions.get(random.nextInt(factions.size()));
            } while (f.getId().equals(faction.getId()) && f.getProtectionTime() > System.currentTimeMillis());

            player.openInventory(Factions.getInstance().getInventoryHandler().getRaidMenu(f));

        } else {
            if (faction == null) {
                player.sendMessage(prefix + "Du bist in keiner Faction.");
                return true;
            }

            if (raids.containsKey(faction.getId())) {
                raid = raids.get(faction.getId());
            }

            if (args[0].equalsIgnoreCase("join")) {
                if (!raids.containsKey(faction.getId()) && raids.get(faction.getId()).getState() != RaidState.WAITING)
                    return true;
                if (raid.getPlayers().contains(player.getName())) return true;

                if (Factions.getInstance().getBasePlayerHandler().getCombatPlayers().containsKey(player.getName())) {
                    player.sendMessage(prefix + "Du kannst im Kampf nicht beitreten.");
                    return true;
                }

                raid.getPlayers().add(player.getName());
                raidHandler.teleportToRaidArea(player, faction);
                factionHandler.sendFactionMessage(factionHandler.getFactionPlayerName(factionHandler.getFactionPlayer(player)) + " §7ist dem Raid beigetreten.", faction);

            } else if (args[0].equalsIgnoreCase("leave")) {
                for (Map.Entry<String, FactionRaid> raidEntry : raids.entrySet()) {
                    if (!raidEntry.getValue().getPlayers().contains(player.getName())) return true;

                    if (Factions.getInstance().getBasePlayerHandler().getCombatPlayers().containsKey(player.getName())) {
                        player.sendMessage(prefix + "Du kannst im Kampf nicht verlassen.");
                        return true;
                    }

                    raidEntry.getValue().getPlayers().remove(player.getName());
                    player.teleport(faction.getHomes().get(factionHandler.getFactionLocationName()));
                    player.setGameMode(GameMode.SURVIVAL);
                    factionHandler.sendFactionMessage(factionHandler.getFactionPlayerName(factionHandler.getFactionPlayer(player)) + " §7hat den Raid verlassen.", faction);

                    if (raidEntry.getValue().getPlayers().size() == 0) {
                        raids.remove(faction.getId());
                    }
                }

            } else if (args[0].equalsIgnoreCase("scout")) {
                Random random = new Random();
                Faction f;

                do {
                    f = factions.get(random.nextInt(factions.size()));
                } while (f.getId().equals(faction.getId()));

                raidHandler.startScouting(player, f);
            } else if (args[0].equalsIgnoreCase("start")) {
                if (raid == null) return true;
                raidHandler.startRaid(faction.getId());
            }
        }

        return true;
    }
}
