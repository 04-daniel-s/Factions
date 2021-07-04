package de.lecuutex.factions.commands;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.faction.FactionHandler;
import de.lecuutex.factions.utils.raid.RaidHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    private final RaidHandler raidHandler = Factions.getInstance().getRaidHandler();
    private final FactionHandler factionHandler = Factions.getInstance().getFactionHandler();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("spawn")) {
            Player player = (Player) sender;
            if (raidHandler.getRaids().containsKey(factionHandler.getFactionByPlayer(player).getId())) {
                player.sendMessage(Factions.getInstance().getPrefix()+ "Diesen Befehl kannst du im Raid nicht benutzen!");
                return true;
            }

            player.sendMessage(Factions.getInstance().getPrefix() + "Du wirst zum Spawn teleportiert...");
            player.teleport(factionHandler.getConfig().getSpawn());
        }
        return true;
    }
}
