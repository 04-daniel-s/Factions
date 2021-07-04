package de.lecuutex.factions.commands;

import de.lecuutex.factions.Factions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;
        player.openInventory(Factions.getInstance().getInventoryHandler().getSkillInventory(player));

        return true;
    }
}
