package de.lecuutex.factions.commands;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.FactionConfig;
import de.lecuutex.factions.utils.faction.Faction;
import de.lecuutex.factions.utils.faction.FactionHandler;
import de.lecuutex.factions.utils.faction.FactionPlayer;
import de.lecuutex.factions.utils.faction.FactionRank;
import de.lecuutex.factions.utils.players.BasePlayer;
import de.lecuutex.factions.utils.raid.RaidHandler;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class FactionCommand implements CommandExecutor {

    private final String prefix = Factions.getInstance().getPrefix();

    private final String insufficientPerms = prefix + "Dazu hast du keine Rechte!";

    private final FactionHandler factionHandler = Factions.getInstance().getFactionHandler();

    private final RaidHandler raidHandler = Factions.getInstance().getRaidHandler();

    private final FactionConfig config = Factions.getInstance().getFactionConfig();

    //TODO Namefarbe der Rangfarbe anpassen bspw. beim Einladen
    //TODO /f sethome

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        if (args.length >= 1) {
            Faction faction = factionHandler.getFactionByPlayer(player);
            FactionRank rank = null;

            if (faction == null) {
                if (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("accept") && !args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(prefix + "§cDu bist in keiner Faction.");
                    return true;
                }
            } else {
                rank = factionHandler.getRankByPlayer(player);
            }

            if (args[0].equalsIgnoreCase("home")) {
                player.teleport(faction.getHomes().get("home"));

            } else if (args[0].equalsIgnoreCase("delete")) {
                //TODO: Delete funktioniert nicht
                if (!faction.getCreator().equalsIgnoreCase(player.getUniqueId().toString())) {
                    player.sendMessage(insufficientPerms);
                    return true;
                }

                if (raidHandler.getRaids().entrySet().stream().anyMatch(v -> !v.getValue().getRaidedFaction().equalsIgnoreCase(faction.getId()))) {
                    player.sendMessage(prefix + "Du kannst die Faction während eines Raids nicht löschen!");
                    return true;
                }

                player.sendMessage(prefix + "Du hast die Faction §e" + faction.getName() + " §aerfolgreich gelöscht!");
                deleteFaction(faction);

            } else if (args[0].equalsIgnoreCase("info")) {
                player.openInventory(Factions.getInstance().getInventoryHandler().getFaction(faction));

            } else if (args[0].equalsIgnoreCase("upgrade")) {
                TextComponent message = new TextComponent(prefix + "§aIch möchte die Faction aufstufen. §7[§dBESTÄTIGEN§7]");
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§d" + factionHandler.getFactionLevels().get(faction.getLevel() + 1) + "$ §7➜ §dLevel §d" + (faction.getLevel() + 1)).bold(true).create()));
                message.setBold(true);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f level"));
                player.spigot().sendMessage(message);

            } else if (args[0].equalsIgnoreCase("level")) {
                if (!rank.isLevel() && !faction.getCreator().equalsIgnoreCase(player.getUniqueId().toString())) {
                    player.sendMessage(insufficientPerms);
                    return true;
                }

                if (faction.getLevel() == factionHandler.getFactionLevels().size()) {
                    player.sendMessage(prefix + "Deine Faction ist auf dem maximalen Level.");
                    return true;
                }

                BasePlayer bp = Factions.getInstance().getBasePlayerHandler().getBasePlayers().get(player.getName());
                int upgradeCost = factionHandler.getFactionLevels().get(faction.getLevel() + 1);

                if (bp.getMoney() < upgradeCost) {
                    player.sendMessage(prefix + "Dazu hast du zu wenig Geld!");
                    return true;
                }

                bp.setMoney(bp.getMoney() - upgradeCost);
                faction.setLevel(faction.getLevel() + 1);
                faction.setSlots(factionHandler.getBaseMemberSize() + faction.getLevel());

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                player.sendMessage(prefix + "Du hast die Faction gelevelt. §7[§d" + faction.getLevel() + "§7]");
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage("");
                }

            } else if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (faction != null) {
                        player.sendMessage(prefix + "Du bist bereits in einer Faction");
                        return true;
                    }
                    if (args[1].length() > 10 || args[1].length() < 3) {
                        player.sendMessage(prefix + "Der Name muss mindestens 3 und darf maximal 10 Zeichen lang sein.");
                        return true;
                    }
                    for (Faction f : factionHandler.getFactions()) {
                        if (f.getName().equalsIgnoreCase(args[1])) {
                            player.sendMessage(prefix + "Dieser Name ist nicht verfügbar.");
                            return true;
                        }
                    }

                    createFaction(args[1], player);
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (faction.getMember().stream().noneMatch(v -> v.getName().equalsIgnoreCase(args[1]))) {
                        player.sendMessage(prefix + "Dieser Spieler ist nicht in deiner Faction.");
                        return true;
                    }
                    if ((!rank.isKick() && !faction.getCreator().equalsIgnoreCase(player.getUniqueId().toString())) || faction.getCreator().equalsIgnoreCase(Bukkit.getPlayer(args[1]).getUniqueId().toString())) {
                        player.sendMessage(insufficientPerms);
                        return true;
                    }
                    if (player.getName().equalsIgnoreCase(args[1])) {
                        player.sendMessage(prefix + "Du kannst dich nicht selbst rauswerfen.");
                        return true;
                    }

                    faction.getMember().remove(factionHandler.getFactionPlayer(Bukkit.getPlayer(args[1])));

                    if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))) {
                        if (factionHandler.isInFaction(faction, Bukkit.getPlayer(args[1]).getLocation()) || factionHandler.isInRaidArea(faction, Bukkit.getPlayer(args[1]).getLocation())) {
                            Bukkit.getPlayer(args[1]).teleport(config.getSpawn());
                        }
                    }

                    sendFactionMessage(faction, prefix + "§c" + player.getName() + " hat §e" + Bukkit.getPlayer(args[1]).getName() + " §caus der Faction geworfen!");

                } else if (args[0].equalsIgnoreCase("promote")) {
                    FactionPlayer currentPlayer = factionHandler.getFactionPlayer(Bukkit.getPlayer(args[1]));
                    FactionRank currentRank = currentPlayer.getRank();

                    if ((!rank.isPromote() && !faction.getCreator().equalsIgnoreCase(player.getUniqueId().toString())) || rank.getId() >= currentRank.getId()) {
                        player.sendMessage(insufficientPerms);
                        return true;
                    }
                    for (FactionPlayer factionPlayer : faction.getMember()) {
                        if (!factionPlayer.getName().equalsIgnoreCase(args[1])) {
                            player.sendMessage(prefix + "Dieser Spieler ist nicht in deiner Faction.");
                            return true;
                        }
                    }
                    if (currentRank.getId() == 0) {
                        player.sendMessage(prefix + "Er hat bereits den höchsten Rang!");
                        return true;
                    }

                    FactionRank upgradedRank = factionHandler.getRankByID(faction.getId(), currentRank.getId() - 1);
                    currentPlayer.setRank(upgradedRank);
                    player.sendMessage(prefix + "Du hast " + Bukkit.getPlayer(args[0]).getName() + " zu " + upgradedRank.getName() + " befördert!");

                    sendFactionMessage(faction, prefix + player.getName() + " hat " + Bukkit.getPlayer(args[1]).getName() + " zu " + upgradedRank.getName() + " befördert!");

                } else if (args[0].equalsIgnoreCase("demote")) {
                    FactionPlayer currentPlayer = factionHandler.getFactionPlayer(Bukkit.getPlayer(args[1]));
                    FactionRank currentRank = currentPlayer.getRank();

                    if ((!rank.isDemote() && !player.getUniqueId().toString().equalsIgnoreCase(faction.getCreator())) || currentPlayer.getUuid().equals(faction.getCreator()) || rank.getId() >= currentRank.getId()) {
                        player.sendMessage(insufficientPerms);
                        return true;
                    }

                    if (currentRank.getId() == 3) {
                        player.sendMessage(prefix + "Er hat bereits den niedrigsten Rang!");
                        return true;
                    }

                    for (FactionPlayer factionPlayer : faction.getMember()) {
                        if (!factionPlayer.getName().equalsIgnoreCase(args[1])) {
                            player.sendMessage(prefix + "§cDieser Spieler ist nicht in deiner Faction.");
                            return true;
                        }
                    }

                    FactionRank upgradedRank = factionHandler.getRankByID(faction.getId(), currentRank.getId() + 1);
                    currentPlayer.setRank(upgradedRank);
                    player.sendMessage(prefix + "Du hast §e" + Bukkit.getPlayer(args[0]).getName() + " §czu " + upgradedRank.getName() + " degradiert!");

                    sendFactionMessage(faction, prefix + "§c" + player.getName() + " hat §e" + Bukkit.getPlayer(args[1]).getName() + " §czu " + upgradedRank.getName() + " §cdegradiert!");

                } else if (args[0].equalsIgnoreCase("invite")) {
                    if (!rank.isInvite() && !player.getUniqueId().toString().equalsIgnoreCase(faction.getCreator())) {
                        player.sendMessage(insufficientPerms);
                        return true;
                    }
                    if (faction.getSlots() <= faction.getMember().size()) {
                        player.sendMessage(prefix + "Deine Faction ist voll!");
                        return true;
                    }
                    if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))) {
                        player.sendMessage(prefix + "Dieser Spieler ist nicht online!");
                        return true;
                    }
                    if (factionHandler.getFactionInvites().containsKey(Bukkit.getPlayer(args[1]).getName())) {
                        player.sendMessage(prefix + "Dieser Spieler hat eine ausstehende Einladung.");
                        return true;
                    }
                    if (factionHandler.getFactionByPlayer(Bukkit.getPlayer(args[1])) != null) {
                        player.sendMessage(prefix + "Der Spieler ist bereits in einer Faction!");
                        return true;
                    }

                    sendFactionMessage(faction, prefix + player.getName() + " hat " + Bukkit.getPlayer(args[1]).getName() + " in die Faction eingeladen!");
                    factionHandler.getFactionInvites().put(Bukkit.getPlayer(args[1]).getName(), faction.getName());

                    TextComponent message = new TextComponent(prefix + "Du wurdest in die Faction §e" + faction.getName() + " eingeladen. \n" + prefix + "Möchtest du dieser Faction beitreten? §7[§dBESTÄTIGEN§7]");
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept " + faction.getName()));
                    Bukkit.getPlayer(args[1]).spigot().sendMessage(message);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.getInstance(), () -> {
                        factionHandler.getFactionInvites().remove(args[1]);
                        sendFactionMessage(faction, prefix + "Die Einladung an " + Bukkit.getPlayer(args[1]).getName() + " ist verfallen!");
                        Bukkit.getPlayer(args[1]).sendMessage(prefix + "Die Einladung ist verfallen!");
                    }, 60 * 20);

                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (!factionHandler.getFactionInvites().containsKey(player.getName())) {
                        player.sendMessage(prefix + "Du hast von dieser Faction keine Einladung!");
                        return true;
                    }

                    if (!args[1].equalsIgnoreCase(factionHandler.getFactionInvites().get(player.getName()))) {
                        player.sendMessage(prefix + "Du hast von dieser Faction keine Einladung!");
                        return true;
                    }

                    Faction f = factionHandler.getFactionByFactionName(args[1]);
                    f.getMember().add(new FactionPlayer(player.getName(), player.getName(), factionHandler.getRankByID(f.getId(), 3), f.getId()));
                    sendFactionMessage(f, prefix + player.getName() + " ist der Faction beigetreten.");
                }
            }
        }
        return true;
    }

    private void sendFactionMessage(Faction faction, String message) {
        for (FactionPlayer factionPlayer : faction.getMember()) {
            if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(factionPlayer.getName()))) continue;
            Bukkit.getPlayer(factionPlayer.getName()).sendMessage(message);
        }
    }

    private void createFaction(String fName, Player player) {
        Location loc = new Location(Bukkit.getWorld("factions"), Factions.getInstance().getFactionConfig().getConfiguration().getDouble("currentlocation.x"), 20, Factions.getInstance().getFactionConfig().getConfiguration().getDouble("currentlocation.z"));
        Factions.getInstance().loadSchmatic(loc, "factionspawn.schem");
        Bukkit.getScheduler().runTaskAsynchronously(Factions.getInstance(), () -> player.sendMessage(Factions.getInstance().registerFaction(fName, player, UUID.randomUUID().toString(), loc)));
        player.teleport(loc);

        Location l = loc.clone();
        l.add(2000, 0, 0);

        if (l.getX() >= 50000) {
            l = l.add(-50000, 0, 2000);
        }

        Factions.getInstance().getFactionConfig().getConfiguration().set("currentlocation.x", l.getX());
        Factions.getInstance().getFactionConfig().getConfiguration().set("currentlocation.z", l.getZ());

        try {
            Factions.getInstance().getFactionConfig().getConfiguration().save(Factions.getInstance().getFactionConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFaction(Faction faction) {
        Bukkit.getScheduler().runTaskAsynchronously(Factions.getInstance(), () -> {
            try {

                PreparedStatement preparedStatement = Factions.getInstance().getMySQL().getConn().prepareStatement("DELETE FROM factions WHERE id = ?");
                preparedStatement.setString(1, faction.getId());
                preparedStatement.execute();

                PreparedStatement preparedStatement1 = Factions.getInstance().getMySQL().getConn().prepareStatement("DELETE FROM factionplayers WHERE factionid = ?");
                preparedStatement1.setString(1, faction.getId());
                preparedStatement1.execute();

                PreparedStatement preparedStatement2 = Factions.getInstance().getMySQL().getConn().prepareStatement("DELETE FROM factionranks WHERE factionid = ?");
                preparedStatement2.setString(1, faction.getId());
                preparedStatement2.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (factionHandler.isInFaction(faction, onlinePlayer.getLocation()) || factionHandler.isInRaidArea(faction, onlinePlayer.getLocation())) {
                onlinePlayer.teleport(config.getSpawn());
            }
        }

        Factions.getInstance().loadSchmatic(faction.getHomes().get(Factions.getInstance().getFactionHandler().getFactionLocationName()), "air.schem");
        Factions.getInstance().getFactionHandler().getFactions().remove(faction);
    }
}
