package de.lecuutex.factions.utils.inventory;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.ItemBuilder;
import de.lecuutex.factions.utils.backpacks.Backpack;
import de.lecuutex.factions.utils.faction.Faction;
import de.lecuutex.factions.utils.faction.FactionHandler;
import de.lecuutex.factions.utils.faction.FactionRank;
import de.lecuutex.factions.utils.players.BasePlayer;
import de.lecuutex.factions.utils.raid.RaidHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

public class InventoryHandler {

    private final FactionHandler factionHandler = Factions.getInstance().getFactionHandler();
    //TODO: Beschreibung zu allen Items

    private final RaidHandler raidHandler = Factions.getInstance().getRaidHandler();

    public Inventory getSkillInventory(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "§d§lSkill");
        BasePlayer bp = Factions.getInstance().getBasePlayerHandler().getBasePlayers().get(p.getName());
        fillGlasses(inventory);

        inventory.setItem(0, new ItemBuilder(Material.BOOK).setDisplayName(" ").setLore("§c§lLevel up to get Skillpoints").build());
        if (bp.getSkillpoints() > 0)
            inventory.setItem(0, new ItemBuilder(Material.WRITABLE_BOOK).setDisplayName("§e§lSkillpoints").setAmount(bp.getSkillpoints()).build());
        //TODO: Fortschrittbalken fehlt
        inventory.setItem(8, new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("§a" + p.getName() + "'s Stats").setSkullOwner(p).setLore("§7Nächstes Level: §d" + (Factions.getInstance().getBasePlayerHandler().getLevels().get(bp.getLevel()) - bp.getExp()) + " §dXP", "", "§7Attack Damage: §6" + bp.getAttackDamage() + " §7(§e" + bp.getAttackDamage() * Factions.getInstance().getBasePlayerHandler().getAttackDamageUpgrade() + "§e% §7Damage)", "§7Attack Speed: §6" + bp.getAttackSpeed() + " §7(§e" + bp.getAttackSpeed() * Factions.getInstance().getBasePlayerHandler().getAttackSpeedUpgrade() + "§e% §7Attack Speed)", "§7Movement Speed: §6" + bp.getMovementSpeed() + " §7(§e"+ bp.getMovementSpeed() * Factions.getInstance().getBasePlayerHandler().getMovementSpeedUpgrade() + "§e% §7Movement Speed)", "§7Health: §6" + bp.getHealth() + " §7(§e" + bp.getHealth() * Factions.getInstance().getBasePlayerHandler().getHealthUpgrade() + "§e% §7Health)", "§7Armor: §6" + bp.getArmor() + " §7(§e" + bp.getArmor() * Factions.getInstance().getBasePlayerHandler().getArmorUpgrade() + "§e% §7Armor)").build());

        inventory.setItem(10, new ItemBuilder(Material.BARRIER).setDisplayName("§4Attack Damage").setLore("§c§lClick to unlock").build());
        inventory.setItem(11, new ItemBuilder(Material.BARRIER).setDisplayName("§4Attack Speed").setLore("§c§lClick to unlock").build());
        inventory.setItem(12, new ItemBuilder(Material.BARRIER).setDisplayName("§4Movement Speed").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setLore("§c§lClick to unlock").build());
        inventory.setItem(14, new ItemBuilder(Material.BARRIER).setDisplayName("§4Health").setLore("§c§lClick to unlock").build());
        inventory.setItem(15, new ItemBuilder(Material.BARRIER).setDisplayName("§4Armor").setLore("§c§lClick to unlock").build());

        if (bp.getAttackDamage() > 0)
            inventory.setItem(10, new ItemBuilder(Material.STONE_SWORD).setDisplayName("§aUpgrade Attack Damage").setLore("", "   §7§lNow           §6§lNEXT", "  §6§l+" + bp.getAttackDamage() * Factions.getInstance().getBasePlayerHandler().getAttackDamageUpgrade() + "%" + "    §2§l>§a§l>     §6§l+" + (bp.getAttackDamage() * Factions.getInstance().getBasePlayerHandler().getAttackDamageUpgrade() + Factions.getInstance().getBasePlayerHandler().getAttackDamageUpgrade()) + "%", " §7§lDamage       §7§lDamage ", "").setAmount(bp.getAttackDamage()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build());
        if (bp.getAttackSpeed() > 0)
            inventory.setItem(11, new ItemBuilder(Material.FEATHER).setDisplayName("§aUpgrade Attack Speed").setLore("", "   §7§lNow           §6§lNEXT", "  §6§l+" + bp.getAttackSpeed() * Factions.getInstance().getBasePlayerHandler().getAttackSpeedUpgrade() + "%" + "    §2§l>§a§l>     §6§l+" + (bp.getAttackSpeed() * Factions.getInstance().getBasePlayerHandler().getAttackDamageUpgrade() + Factions.getInstance().getBasePlayerHandler().getAttackDamageUpgrade()) + "%", "  §7§lSpeed        §7§lSpeed  ", "").setAmount(Factions.getInstance().getBasePlayerHandler().getBasePlayers().get(p.getName()).getAttackSpeed()).build());
        if (bp.getMovementSpeed() > 0)
            inventory.setItem(12, new ItemBuilder(Material.GOLDEN_BOOTS).setDisplayName("§aUpgrade Movement Speed").setLore("", "   §7§lNow           §6§lNEXT", "  §6§l+" + bp.getMovementSpeed() * Factions.getInstance().getBasePlayerHandler().getMovementSpeedUpgrade() + "%" + "    §2§l>§a§l>     §6§l+" + (bp.getMovementSpeed() * Factions.getInstance().getBasePlayerHandler().getMovementSpeedUpgrade() + Factions.getInstance().getBasePlayerHandler().getMovementSpeedUpgrade()) + "%", " §7§lDamage       §7§lDamage ", "").setAmount(bp.getMovementSpeed()).build());
        if (bp.getHealth() > 0)
            inventory.setItem(14, new ItemBuilder(Material.APPLE).setDisplayName("§aUpgrade Health").setLore("", "    §7§lNow           §6§lNEXT", "   §6§l+" + bp.getHealth() * Factions.getInstance().getBasePlayerHandler().getHealthUpgrade() + "%" + "    §2§l>§a§l>     §6§l+" + (bp.getHealth() * Factions.getInstance().getBasePlayerHandler().getHealthUpgrade() + Factions.getInstance().getBasePlayerHandler().getHealthUpgrade()) + "%", " §7§lDefense      §7§lDefense ", "").setAmount(bp.getHealth()).build());
        if (bp.getArmor() > 0)
            inventory.setItem(15, new ItemBuilder(Material.IRON_CHESTPLATE).setDisplayName("§aUpgrade Armor").setLore("", "    §7§lNow           §6§lNEXT", "   §6§l+" + bp.getArmor() * Factions.getInstance().getBasePlayerHandler().getArmorUpgrade() + "%" + "    §2§l>§a§l>     §6§l+" + (bp.getArmor() * Factions.getInstance().getBasePlayerHandler().getArmorUpgrade() + Factions.getInstance().getBasePlayerHandler().getArmorUpgrade()) + "%", " §7§lDefense      §7§lDefense ", "").setAmount(bp.getArmor()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build());

        if (bp.getAttackDamage() == 60)
            inventory.setItem(10, new ItemBuilder(inventory.getItem(10)).setLore("§c§lDu hast die maximale Stufe erreicht!").build());
        if (bp.getAttackSpeed() == 60)
            inventory.setItem(11, new ItemBuilder(inventory.getItem(11)).setLore("§c§lDu hast die maximale Stufe erreicht!").build());
        if (bp.getMovementSpeed() == 40)
            inventory.setItem(12, new ItemBuilder(inventory.getItem(12)).setLore("§c§lDu hast die maximale Stufe erreicht!").build());
        if (bp.getHealth() == 60)
            inventory.setItem(14, new ItemBuilder(inventory.getItem(14)).setLore("§c§lDu hast die maximale Stufe erreicht!").build());
        if (bp.getArmor() == 60)
            inventory.setItem(15, new ItemBuilder(inventory.getItem(15)).setLore("§c§lDu hast die maximale Stufe erreicht!").build());

        return inventory;
    }

    private void fillGlasses(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build());
        }
    }

    public Inventory getFactionSettings(FactionRank factionRank) {
        CustomInventory customInventory = new CustomInventory(6 * 9, factionRank.getName());
        fillCustomInventory(customInventory);

        customInventory.setItem(18, new ItemBuilder(getMaterialByBoolean(factionRank.isClanChat())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(1 + 18, new ItemBuilder(getMaterialByBoolean(factionRank.isInvite())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(2 + 18, new ItemBuilder(getMaterialByBoolean(factionRank.isKick())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(3 + 18, new ItemBuilder(getMaterialByBoolean(factionRank.isBuild())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(4 + 18, new ItemBuilder(getMaterialByBoolean(factionRank.isOpenChests())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(5 + 18, new ItemBuilder(getMaterialByBoolean(factionRank.isPromote())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(6 + 18, new ItemBuilder(getMaterialByBoolean(factionRank.isDemote())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(7 + 18, new ItemBuilder(getMaterialByBoolean(factionRank.isAcceptTpa())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(8 + 18, new ItemBuilder(getMaterialByBoolean(factionRank.isStartWar())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(2 + 36, new ItemBuilder(getMaterialByBoolean(factionRank.isWithdraw())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(3 + 36, new ItemBuilder(getMaterialByBoolean(factionRank.isRename())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(4 + 36, new ItemBuilder(getMaterialByBoolean(factionRank.isRaid())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(5 + 36, new ItemBuilder(getMaterialByBoolean(factionRank.isLevel())).setDisplayName(" ").build(), player -> {
        });
        customInventory.setItem(6 + 36, new ItemBuilder(getMaterialByBoolean(factionRank.isModifyRanks())).setDisplayName(" ").build(), player -> {
        });

        customInventory.setItem(9, new ItemBuilder(Material.PAPER).setDisplayName("§7Clanchat").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setClanChat(!factionRank.isClanChat());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(1 + 9, new ItemBuilder(Material.GREEN_DYE).setDisplayName("§7Einladen").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setInvite(!factionRank.isInvite());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(2 + 9, new ItemBuilder(Material.RED_DYE).setDisplayName("§7Rauswerfen").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setKick(!factionRank.isKick());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(3 + 9, new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("§7Bauen").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setBuild(!factionRank.isBuild());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(4 + 9, new ItemBuilder(Material.CHEST).setDisplayName("§7Kisten öffnen").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setOpenChests(!factionRank.isOpenChests());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(5 + 9, new ItemBuilder(Material.REDSTONE).setDisplayName("§7Befördern").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setPromote(!factionRank.isPromote());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(6 + 9, new ItemBuilder(Material.GUNPOWDER).setDisplayName("§7Degradieren").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setDemote(!factionRank.isDemote());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(7 + 9, new ItemBuilder(Material.BARRIER).setDisplayName("§7Tpa akzeptieren").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setAcceptTpa(!factionRank.isAcceptTpa());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(8 + 9, new ItemBuilder(Material.GOLDEN_SWORD).setDisplayName("§7Factionkampf").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setStartWar(!factionRank.isStartWar());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(2 + 27, new ItemBuilder(Material.GOLD_INGOT).setDisplayName("§7Geld abheben").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setWithdraw(!factionRank.isWithdraw());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(3 + 27, new ItemBuilder(Material.FEATHER).setDisplayName("§7Namen ändern").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setRename(!factionRank.isRename());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(4 + 27, new ItemBuilder(Material.ENDER_EYE).setDisplayName("§7Raid suchen").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setRaid(!factionRank.isRaid());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(5 + 27, new ItemBuilder(Material.EXPERIENCE_BOTTLE).setDisplayName("§7Level erhöhen").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() >= factionRank.getId() || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setLevel(!factionRank.isLevel());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(6 + 27, new ItemBuilder(Material.WRITTEN_BOOK).setDisplayName("§7Rechte modifizieren").build(), player -> {
            FactionRank playerRank = factionHandler.getRankByPlayer(player);
            if ((playerRank.getId() > 0 || factionRank.getId() == 0 || !playerRank.isModifyRanks()) && !factionHandler.getFactionByPlayer(player).getCreator().equalsIgnoreCase(player.getUniqueId().toString()))
                return;
            factionRank.setModifyRanks(!factionRank.isModifyRanks());
            player.openInventory(getFactionSettings(factionRank));
        });

        customInventory.setItem(53, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§7Zurück").build(), player -> player.openInventory(Factions.getInstance().getInventoryHandler().getRanks(Factions.getInstance().getFactionHandler().getFactionByFactionID(factionRank.getFactionID()))));

        Factions.getInstance().getCustomInventoryHandler().getCustomInventories().put(customInventory.getTitle(), customInventory);
        return customInventory.getInventory();
    }

    public Inventory getRanks(Faction faction) {
        CustomInventory customInventory = new CustomInventory(9, "§cRänge");
        fillCustomInventory(customInventory);

        customInventory.setItem(1, new ItemBuilder(Material.RED_CONCRETE).setDisplayName("§cLeader").build(), player -> player.openInventory(getFactionSettings(Factions.getInstance().getFactionHandler().getRankByPlayer(Bukkit.getPlayer(faction.getCreator())))));
        customInventory.setItem(2, new ItemBuilder(Material.BLUE_CONCRETE).setDisplayName("§9Offizier").build(), player -> player.openInventory(getFactionSettings(faction.getRanks().stream().filter(v -> v.getId() == 1).findFirst().get())));
        customInventory.setItem(3, new ItemBuilder(Material.GREEN_CONCRETE).setDisplayName("§2Mitglied").build(), player -> player.openInventory(getFactionSettings(faction.getRanks().stream().filter(v -> v.getId() == 2).findFirst().get())));
        customInventory.setItem(4, new ItemBuilder(Material.LIME_CONCRETE).setDisplayName("§aTest-Mitglied").build(), player -> player.openInventory(getFactionSettings(faction.getRanks().stream().filter(v -> v.getId() == 3).findFirst().get())));
        customInventory.setItem(8, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§7Zurück").build(), player -> player.openInventory(getFaction(faction)));

        Factions.getInstance().getCustomInventoryHandler().getCustomInventories().put(customInventory.getTitle(), customInventory);
        return customInventory.getInventory();
    }

    public Inventory getMember(Faction faction) {
        CustomInventory customInventory = new CustomInventory(5 * 9, "§cMitglieder");
        fillCustomInventory(customInventory);

        for (int i = 0; i < faction.getMember().size(); i++) {
            //TODO Stats als Lore fehlen
            customInventory.setItem(i, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Bukkit.getPlayer(faction.getMember().get(i).getName())).setDisplayName("§7" + faction.getMember().get(i).getName()).build(), player -> {
            });
        }

        customInventory.setItem(44, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§7Zurück").build(), player -> player.openInventory(getFaction(faction)));

        Factions.getInstance().getCustomInventoryHandler().getCustomInventories().put(customInventory.getTitle(), customInventory);
        return customInventory.getInventory();
    }

    public Inventory getRaidLog(Faction faction) {
        CustomInventory customInventory = new CustomInventory(3 * 9, "§cRaid Log");
        fillCustomInventory(customInventory);

        for (int i = 0; i < customInventory.getSize() - 2; i++) {
            if (faction.getRaidedBy().size() == 0) continue;
            if (i > faction.getRaidedBy().size() - 1) continue;
            Faction raider = Factions.getInstance().getFactionHandler().getFactionByFactionID(faction.getRaidedBy().get(i));
            customInventory.setItem(i, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Bukkit.getPlayer(raider.getCreator())).setDisplayName("§c" + raider.getName()).build(), player -> {
            });
        }

        customInventory.setItem(26, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§7Zurück").build(), player -> player.openInventory(getFaction(faction)));
        Factions.getInstance().getCustomInventoryHandler().getCustomInventories().put(customInventory.getTitle(), customInventory);
        return customInventory.getInventory();
    }

    private void fillCustomInventory(CustomInventory customInventory) {
        for (int i = 0; i < customInventory.getInventory().getSize(); i++) {
            customInventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build(), player -> {
            });
        }
    }

    public Inventory getFaction(Faction f) {
        CustomInventory customInventory = new CustomInventory(27, "§cFaction");
        fillCustomInventory(customInventory);

        String[] lore = {"§aLeader: §e" + Bukkit.getPlayer(f.getCreator()).getName(), " ", "§aElo: §e" + f.getElo(), "§aBonus XP: §e* " + (f.getExpMultiplier() + 1), " ", "§aGeld: §e" + f.getMoney(), "§aLevel: §e" + f.getLevel(), "§aSlots: §e" + f.getSlots()};

        customInventory.setItem(4, new ItemBuilder(Material.TORCH).setDisplayName("§aHilfe").setLore("§7Weitere Commands:", "§7/f help").build(), player -> {
            player.performCommand("/f help");
            player.closeInventory();
        });

        customInventory.setItem(9, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Bukkit.getPlayer(f.getCreator())).setLore(lore).setDisplayName("§c" + f.getName()).build(), player -> {
        });
        customInventory.setItem(3 + 9, new ItemBuilder(Material.BOOK).setDisplayName("§7Ränge").build(), player -> player.openInventory(getRanks(f)));
        customInventory.setItem(4 + 9, new ItemBuilder(Material.MAP).setDisplayName("§7Raids").build(), player -> player.openInventory(getRaidLog(f)));
        customInventory.setItem(5 + 9, new ItemBuilder(Material.CHEST).setDisplayName("§7Mitglieder").build(), player -> player.openInventory(getMember(f)));
        customInventory.setItem(8 + 9, new ItemBuilder(Material.END_CRYSTAL).setDisplayName("§7Raid Energie").setAmount(f.getRaidEnergy()).build(), player -> {
        });

        Factions.getInstance().getCustomInventoryHandler().getCustomInventories().put(customInventory.getTitle(), customInventory);
        return customInventory.getInventory();
    }

    public Inventory getBackpack(Player player) {
        Backpack bp = Factions.getInstance().getBackpackHandler().getBackpackByPlayer(player);
        Inventory inventory = Bukkit.createInventory(null, bp.getSize(), "§6Backpack");

        inventory.setContents(bp.getItems());
        return inventory;
    }

    private Material getMaterialByBoolean(Boolean b) {
        return b ? Material.GREEN_STAINED_GLASS : Material.RED_STAINED_GLASS;
    }

    public Inventory getRaidMenu(Faction faction) {
        CustomInventory customInventory = new CustomInventory(3 * 9, "§6Raid Menü");
        fillCustomInventory(customInventory);

        customInventory.setItem(1 + 9, new ItemBuilder(Material.WRITABLE_BOOK).setDisplayName("§aRaidlog").setLore("§7Hier findest du die vergangenen Angriffe auf deine Faction.").build(), player -> {
            getRaidLog(faction);
        });

        customInventory.setItem(3 + 9, new ItemBuilder(Material.DIAMOND_SWORD).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§aRache").setLore("§7Räche dich an deinen Feinden!").build(), player -> {
        }); //TODO: Revenge Inventory

        customInventory.setItem(5 + 9, new ItemBuilder(Material.ENDER_EYE).setDisplayName("§aFaction suchen").setLore("§7Greife eine gegnerische Faction an.").build(), player -> {
            raidHandler.startScouting(player, faction);
        });

        customInventory.setItem(7 + 9, new ItemBuilder(Material.END_CRYSTAL).setDisplayName("§aRaid Energie").setLore("§7Zum Angreifen benötigst du Raid Energie. Pro Angriff verbrauchst du" + raidHandler.getNeededEnergy() + "Energie.").setAmount(faction.getRaidEnergy()).build(), player -> {
        });

        Factions.getInstance().getCustomInventoryHandler().getCustomInventories().put(customInventory.getTitle(), customInventory);
        return customInventory.getInventory();
    }
}