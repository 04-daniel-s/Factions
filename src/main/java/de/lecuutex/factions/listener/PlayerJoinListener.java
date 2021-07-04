package de.lecuutex.factions.listener;

import de.lecuutex.factions.Factions;
import de.lecuutex.factions.utils.FactionConfig;
import de.lecuutex.factions.utils.ItemBuilder;
import de.lecuutex.factions.utils.faction.Faction;
import de.lecuutex.factions.utils.faction.FactionHandler;
import de.lecuutex.factions.utils.players.BasePlayer;
import de.lecuutex.factions.utils.raid.RaidHandler;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final FactionConfig config = Factions.getInstance().getFactionConfig();
    private final String prefix = Factions.getInstance().getPrefix();
    private final FactionHandler factionHandler = Factions.getInstance().getFactionHandler();
    private final RaidHandler raidHandler = Factions.getInstance().getRaidHandler();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        Player player = event.getPlayer();
        BasePlayer basePlayer = Factions.getInstance().getBasePlayerHandler().getBasePlayers().get(player.getName());
        Faction faction = null;

        player.teleport(config.getSpawn());
        player.setFoodLevel(20);

        player.setLevel(basePlayer.getLevel());
        player.setExp((float) basePlayer.getExp() / Factions.getInstance().getBasePlayerHandler().getLevels().get(basePlayer.getLevel()));
        setAttributes(player);

        player.getInventory().addItem(new ItemBuilder(Material.EXPERIENCE_BOTTLE, 64).setDisplayName("200 XP").build());
        player.getInventory().addItem(new ItemBuilder(Material.EXPERIENCE_BOTTLE, 64).setDisplayName("25 XP").build());
        player.getInventory().addItem(new ItemBuilder(Material.EXPERIENCE_BOTTLE, 64).setDisplayName("75 XP").build());
        player.getInventory().addItem(new ItemBuilder(Material.EXPERIENCE_BOTTLE, 64).setDisplayName("125 XP").build());

        if (factionHandler.getFactionByPlayer(player) != null) {
            faction = factionHandler.getFactionByPlayer(player);
            if (raidHandler.getRaids().containsKey(faction.getId())) {
                player.teleport(factionHandler.getFactionByFactionID(raidHandler.getRaids().get(faction.getId()).getRaidedFaction()).getHomes().get(factionHandler.getFactionLocationName()).clone().add(factionHandler.getRaidAreaRadius(), 0, factionHandler.getRaidAreaRadius()));
            }

            factionHandler.sendFactionMessage(factionHandler.getFactionPlayerName(factionHandler.getFactionPlayer(player)) + "§7" + " ist nun online.", factionHandler.getFactionByPlayer(player));
        }

    }

    private void setAttributes(Player player) {
        BasePlayer basePlayer = Factions.getInstance().getBasePlayerHandler().getBasePlayers().get(player.getName());
        double baseAttackDamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getDefaultValue();
        double baseAttackSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getDefaultValue();
        double baseMovementSpeed = 0.1;
        double baseHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
        double baseArmor = 2;

        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(baseAttackDamage + (baseAttackDamage * ((float) basePlayer.getAttackDamage() * Factions.getInstance().getBasePlayerHandler().getAttackDamageUpgrade() / 100)));
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(baseAttackSpeed + (baseAttackSpeed * ((float) basePlayer.getAttackSpeed() * Factions.getInstance().getBasePlayerHandler().getAttackSpeedUpgrade() / 100)));
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(baseMovementSpeed + (baseMovementSpeed * ((float) basePlayer.getMovementSpeed() * Factions.getInstance().getBasePlayerHandler().getMovementSpeedUpgrade() / 100)));
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(baseHealth + (baseHealth * ((float) basePlayer.getHealth() * Factions.getInstance().getBasePlayerHandler().getHealthUpgrade() / 100)));
        player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(baseArmor * ((float) basePlayer.getArmor() * Factions.getInstance().getBasePlayerHandler().getArmorUpgrade() / 100));
    }
}
