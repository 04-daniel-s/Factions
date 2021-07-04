package de.lecuutex.factions.commands;

import de.lecuutex.factions.Factions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackpackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        Factions.getInstance().registerBackpack(player, 3 * 9);
        player.openInventory(Factions.getInstance().getInventoryHandler().getBackpack(player));
        return true;
    }
}
