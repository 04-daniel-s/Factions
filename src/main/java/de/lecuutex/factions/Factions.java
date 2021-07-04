package de.lecuutex.factions;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import de.lecuutex.factions.commands.*;
import de.lecuutex.factions.listener.*;
import de.lecuutex.factions.utils.FactionConfig;
import de.lecuutex.factions.utils.MySQL;
import de.lecuutex.factions.utils.backpacks.Backpack;
import de.lecuutex.factions.utils.backpacks.BackpackHandler;
import de.lecuutex.factions.utils.faction.*;
import de.lecuutex.factions.utils.inventory.CustomInventoryHandler;
import de.lecuutex.factions.utils.inventory.InventoryHandler;
import de.lecuutex.factions.utils.players.BasePlayer;
import de.lecuutex.factions.utils.players.BasePlayerHandler;
import de.lecuutex.factions.utils.raid.RaidHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import scala.collection.SpecificIterableFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Factions extends JavaPlugin {

    @Getter
    private static Factions instance;

    private final String prefix = "§cFactions §7● §7";

    private final String factionChat = "§3§lFC §r§7● ";

    private final Gson gson = new Gson();

    private final MySQL mySQL = new MySQL();

    private final FactionConfig factionConfig = new FactionConfig();

    private BasePlayerHandler basePlayerHandler;

    private FactionHandler factionHandler;

    private final CustomInventoryHandler customInventoryHandler = new CustomInventoryHandler();

    private final BackpackHandler backpackHandler = new BackpackHandler();

    private InventoryHandler inventoryHandler;

    private RaidHandler raidHandler;

    @Override
    public void onEnable() {
        instance = this;

        //TODO: Die Member buggen rum da alle in einer Faction sind
        basePlayerHandler = new BasePlayerHandler();
        factionHandler = new FactionHandler();
        raidHandler = new RaidHandler();
        inventoryHandler = new InventoryHandler();
        //Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getWorlds().forEach(world -> world.getEntities().stream().filter(e -> e instanceof  Monster || e instanceof Animals)),0,1);

        if (Bukkit.getWorld("factions") == null) {
            WorldCreator worldCreator = new WorldCreator("factions");
            worldCreator.generator(new VoidChunkGenerator());
            worldCreator.createWorld();
        }

        for (int i = 1; i <= 99; i++) {
            basePlayerHandler.getLevels().put(i, 30 * (i * i));
        }

        for (int i = 0; i < 20; i++) {
            factionHandler.getFactionLevels().put(i, 10000 * (i * i));
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (basePlayerHandler.getBasePlayers().get(onlinePlayer.getName()).getSkillpoints() > 0) {
                    onlinePlayer.sendMessage(getPrefix() + "Du hast §6" + basePlayerHandler.getBasePlayers().get(onlinePlayer.getName()).getSkillpoints() + "§7 Skillpunkte.");
                }
            }

            saveAll();
        }, 0, 20 * 60 * 15);

        Bukkit.getPluginManager().registerEvents(new AsyncPlayerPreLoginListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockExplodeListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomInventoryHandler(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);
        Bukkit.getPluginManager().registerEvents(new WeatherChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPistonExtendListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPistonRetractListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntitySpawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);

        getCommand("skill").setExecutor(new SkillCommand());
        getCommand("faction").setExecutor(new FactionCommand());
        getCommand("backpack").setExecutor(new BackpackCommand());
        getCommand("raid").setExecutor(new RaidCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());

        try {
            getMySQL().getConn().prepareStatement("CREATE TABLE IF NOT EXISTS baseplayers(uuid VARCHAR(36) PRIMARY KEY, playername VARCHAR(16), money INT, expmultiplier INT, playerlevel INT, exp INT, skillpoints INT, health INT, armor INT, attackdamage INT, attackspeed INT, movementspeed INT)").execute();
            getMySQL().getConn().prepareStatement("CREATE TABLE IF NOT EXISTS factions(name VARCHAR(16) PRIMARY KEY, id VARCHAR(36), creator VARCHAR(36), member TEXT, ranks TEXT,elo INT, raidedBy TEXT, location TEXT, expMultiplier INT, protectionTime INT,raidEnergy INT,money INT, level INT, slots INT)").execute();
            getMySQL().getConn().prepareStatement("CREATE TABLE IF NOT EXISTS factionranks(uuid VARCHAR(36) PRIMARY KEY, id VARCHAR(36), name VARCHAR(36),factionid VARCHAR(36), clanChat TINYINT, build TINYINT, openChests TINYINT, promote TINYINT,demote TINYINT, withdraw TINYINT, renamefaction TINYINT,invite TINYINT,kick TINYINT, accepttpa TINYINT, startwar TINYINT,modifyranks TINYINT, level TINYINT, raid TINYINT)").execute();
            getMySQL().getConn().prepareStatement("CREATE TABLE IF NOT EXISTS factionplayers(uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(16), factionid VARCHAR(36), rankid INT)").execute();
            getMySQL().getConn().prepareStatement("CREATE TABLE IF NOT EXISTS backpacks(uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(16), size INT, items LONGTEXT)").execute();

            loadAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        saveAll();
    }

    private void loadAll() {
        loadBasePlayers();
        loadBackpacks();
        loadFactions(loadFactionPlayers(loadFactionRanks()), loadFactionRanks());
    }

    private void saveAll() {
        Bukkit.getWorld("factions").save();
        saveFactions();
        saveFactionPlayers();
        saveFactionRanks();
        savePlayers();
        saveBackpacks();
    }

    private void loadBasePlayers() {
        try {
            PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("SELECT * FROM baseplayers");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                basePlayerHandler.getBasePlayers().put(rs.getString("playername"), new BasePlayer(rs.getString("uuid"), rs.getString("playername"), rs.getInt("money"), rs.getInt("health"), rs.getInt("armor"), rs.getInt("expmultiplier"), rs.getInt("playerlevel"), rs.getInt("exp"), rs.getInt("skillpoints"), rs.getInt("attackdamage"), rs.getInt("attackspeed"), rs.getInt("movementspeed")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private ArrayList<FactionRank> loadFactionRanks() {
        ArrayList<FactionRank> ranks = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("SELECT * FROM factionranks");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                ranks.add(new FactionRank(rs.getString("uuid"), rs.getInt("id"), rs.getString("factionid"), rs.getString("name"), rs.getInt("level") == 1, rs.getInt("clanChat") == 1, rs.getInt("build") == 1, rs.getInt("openChests") == 1, rs.getInt("promote") == 1, rs.getInt("demote") == 1, rs.getInt("withdraw") == 1, rs.getInt("renamefaction") == 1, rs.getInt("invite") == 1, rs.getInt("kick") == 1, rs.getInt("accepttpa") == 1, rs.getInt("startwar") == 1, rs.getInt("modifyranks") == 1, rs.getInt("raid") == 1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ranks;
    }

    private ArrayList<FactionPlayer> loadFactionPlayers(ArrayList<FactionRank> factionRanks) {
        ArrayList<FactionPlayer> players = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("SELECT * FROM factionplayers");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String id = rs.getString("factionid");

                for (FactionRank factionRank : factionRanks) {
                    if (!factionRank.getFactionID().equals(id)) continue;
                    if (factionRank.getId() != rs.getInt("rankid")) continue;
                    players.add(new FactionPlayer(rs.getString("uuid"), rs.getString("name"), factionRank, id));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    private void loadFactions(ArrayList<FactionPlayer> factionPlayers, ArrayList<FactionRank> factionRanks) {
        try {
            PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("SELECT * FROM factions");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");

                ArrayList<String> raidedBy = new ArrayList<>(gson.fromJson(rs.getString("raidedby"), new TypeToken<ArrayList<String>>() {
                }.getType()));

                HashMap<String, String> homesString = new HashMap<>(gson.fromJson(rs.getString("location"), new TypeToken<HashMap<String, String>>() {
                }.getType()));

                HashMap<String, Location> homes = new HashMap<>();
                for (Map.Entry<String, String> entry : homesString.entrySet()) {
                    String[] strings = entry.getValue().split("#");
                    homes.put(entry.getKey(), new Location(Bukkit.getWorld(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3])));
                }
                factionHandler.getFactions().add(new Faction(id, rs.getString("name"), rs.getString("creator"), new ArrayList<>(factionPlayers.stream().filter(v -> v.getFactionID().equals(id)).collect(Collectors.toList())), factionRanks, raidedBy, homes, rs.getInt("elo"), rs.getInt("expmultiplier"), rs.getInt("protectionTime"), rs.getInt("raidenergy"), rs.getInt("money"), rs.getInt("level"), rs.getInt("slots")));
            }
        } catch (
                SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private void loadBackpacks() {
        try {
            PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("SELECT * FROM backpacks");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                ItemStack[] items = getBackpackHandler().itemStackArrayFromBase64(rs.getString("items"));
                Factions.getInstance().getBackpackHandler().getBackpacks().put(rs.getString("uuid"), new Backpack(rs.getString("uuid"), rs.getString("name"), rs.getInt("size"), items));
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    public void registerBackpack(Player player, int size) {
        if (!getBackpackHandler().getBackpacks().containsKey(player.getUniqueId().toString())) {
            Factions.getInstance().getBackpackHandler().getBackpacks().put(player.getUniqueId().toString(), new Backpack(player.getUniqueId().toString(), player.getName(), size, new ItemStack[]{}));
        }
    }

    public void registerPlayer(String uuid, String name) {
        try {
            PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("SELECT * FROM baseplayers WHERE uuid = ?");
            preparedStatement.setString(1, uuid);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                basePlayerHandler.getBasePlayers().put(name, new BasePlayer(uuid, name, rs.getInt("money"), rs.getInt("health"), rs.getInt("armor"), rs.getInt("expmultiplier"), rs.getInt("playerlevel"), rs.getInt("exp"), rs.getInt("skillpoints"), rs.getInt("attackdamage"), rs.getInt("attackspeed"), rs.getInt("movementspeed")));
            } else {
                basePlayerHandler.getBasePlayers().put(name, new BasePlayer(uuid, name, 1000, 0, 0, 1, 1, 2, 0, 0, 0, 0));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String registerFaction(String name, Player player, String factionID, Location location) {
        ArrayList<FactionRank> ranks = new ArrayList<>();

        ranks.add(new FactionRank(UUID.randomUUID().toString(), 0, factionID, "§cLeader", true, true, true, true, true, true, true, true, true, true, true, true, true, true));
        ranks.add(new FactionRank(UUID.randomUUID().toString(), 1, factionID, "§9Offizier", true, true, true, true, true, false, true, false, true, true, false, true, false, true));
        ranks.add(new FactionRank(UUID.randomUUID().toString(), 2, factionID, "§2Mitglied", false, true, true, true, false, false, false, false, true, false, false, false, false, true));
        ranks.add(new FactionRank(UUID.randomUUID().toString(), 3, factionID, "§aJunior Mitglied", false, true, true, false, false, false, false, false, false, false, false, false, false, true));

        ArrayList<FactionPlayer> member = new ArrayList<>();
        member.add(new FactionPlayer(player.getUniqueId().toString(), player.getName(), ranks.stream().filter(v -> v.getId() == 0).findFirst().get(), factionID));

        HashMap<String, Location> homes = new HashMap<>();
        homes.put("home", location);
        homes.put(factionHandler.getFactionLocationName(), location);

        factionHandler.getFactions().add(new Faction(factionID, name, player.getUniqueId().toString(), member, ranks, new ArrayList<>(), homes, 0, 0, factionHandler.getBaseProtectionTime(), factionHandler.getBaseEnergy(), 0, 0, factionHandler.getBaseMemberSize()));
        return Factions.getInstance().getPrefix() + "Du hast die Faction erstellt";
    }

    public void saveFactions() {
        try {
            for (Faction faction : factionHandler.getFactions()) {
                PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("INSERT INTO factions (id,name,creator,member,ranks,elo,raidedBy,location,expMultiplier,protectionTime,raidEnergy,money,level,slots) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id = ?,name= ?,creator= ?,member= ?,ranks= ?,elo= ?,raidedBy= ?,location= ?,expMultiplier= ?,protectionTime= ?,raidEnergy= ?,money= ?,level= ?,slots= ?");
                HashMap<String, Location> homes = faction.getHomes();
                HashMap<String, String> homesString = new HashMap<>();

                for (Map.Entry<String, Location> entry : homes.entrySet()) {
                    homesString.put(entry.getKey(), entry.getValue().getWorld().getName() + "#" + entry.getValue().getX() + "#" + entry.getValue().getY() + "#" + entry.getValue().getZ());
                }

                preparedStatement.setString(1, faction.getId());
                preparedStatement.setString(2, faction.getName());
                preparedStatement.setString(3, faction.getCreator());
                preparedStatement.setString(4, gson.toJson(faction.getMember()));
                preparedStatement.setString(5, gson.toJson(faction.getRanks()));
                preparedStatement.setInt(6, faction.getElo());
                preparedStatement.setString(7, gson.toJson(faction.getRaidedBy()));
                preparedStatement.setString(8, gson.toJson(homesString));
                preparedStatement.setInt(9, faction.getExpMultiplier());
                preparedStatement.setInt(10, faction.getProtectionTime());
                preparedStatement.setInt(11, faction.getRaidEnergy());
                preparedStatement.setInt(12, faction.getMoney());
                preparedStatement.setInt(13, faction.getLevel());
                preparedStatement.setInt(14, faction.getSlots());

                preparedStatement.setString(15, faction.getId());
                preparedStatement.setString(16, faction.getName());
                preparedStatement.setString(17, faction.getCreator());
                preparedStatement.setString(18, gson.toJson(faction.getMember()));
                preparedStatement.setString(19, gson.toJson(faction.getRanks()));
                preparedStatement.setInt(20, faction.getElo());
                preparedStatement.setString(21, gson.toJson(faction.getRaidedBy()));
                preparedStatement.setString(22, gson.toJson(homesString));
                preparedStatement.setInt(23, faction.getExpMultiplier());
                preparedStatement.setInt(24, faction.getProtectionTime());
                preparedStatement.setInt(25, faction.getRaidEnergy());
                preparedStatement.setInt(26, faction.getMoney());
                preparedStatement.setInt(27, faction.getLevel());
                preparedStatement.setInt(28, faction.getSlots());

                preparedStatement.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactionRanks() {
        try {
            for (Faction value : factionHandler.getFactions()) {
                for (FactionRank rank : value.getRanks()) {

                    PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("INSERT INTO factionranks (uuid,id, name, factionid, clanChat, build, openChests, promote, demote, withdraw, renamefaction, invite, kick, accepttpa, startwar, modifyranks, level) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE uuid = ?, id = ?, name = ?, factionid = ?, clanChat = ?, build = ?, openChests = ?, promote = ?, demote = ?, withdraw = ?, renamefaction = ?, invite = ?, kick = ?, accepttpa = ?, startwar = ?, modifyranks = ?, level = ?");

                    preparedStatement.setString(1, rank.getUuid());
                    preparedStatement.setInt(2, rank.getId());
                    preparedStatement.setString(3, rank.getName());
                    preparedStatement.setString(4, rank.getFactionID());
                    preparedStatement.setInt(5, booleanToInt(rank.isClanChat()));
                    preparedStatement.setInt(6, booleanToInt(rank.isBuild()));
                    preparedStatement.setInt(7, booleanToInt(rank.isOpenChests()));
                    preparedStatement.setInt(8, booleanToInt(rank.isPromote()));
                    preparedStatement.setInt(9, booleanToInt(rank.isDemote()));
                    preparedStatement.setInt(10, booleanToInt(rank.isWithdraw()));
                    preparedStatement.setInt(11, booleanToInt(rank.isRename()));
                    preparedStatement.setInt(12, booleanToInt(rank.isInvite()));
                    preparedStatement.setInt(13, booleanToInt(rank.isKick()));
                    preparedStatement.setInt(14, booleanToInt(rank.isAcceptTpa()));
                    preparedStatement.setInt(15, booleanToInt(rank.isStartWar()));
                    preparedStatement.setInt(16, booleanToInt(rank.isModifyRanks()));
                    preparedStatement.setInt(17, booleanToInt(rank.isLevel()));

                    preparedStatement.setString(18, rank.getUuid());
                    preparedStatement.setInt(19, rank.getId());
                    preparedStatement.setString(20, rank.getName());
                    preparedStatement.setString(21, rank.getFactionID());
                    preparedStatement.setInt(22, booleanToInt(rank.isClanChat()));
                    preparedStatement.setInt(23, booleanToInt(rank.isBuild()));
                    preparedStatement.setInt(24, booleanToInt(rank.isOpenChests()));
                    preparedStatement.setInt(25, booleanToInt(rank.isPromote()));
                    preparedStatement.setInt(26, booleanToInt(rank.isDemote()));
                    preparedStatement.setInt(27, booleanToInt(rank.isWithdraw()));
                    preparedStatement.setInt(28, booleanToInt(rank.isRename()));
                    preparedStatement.setInt(29, booleanToInt(rank.isInvite()));
                    preparedStatement.setInt(30, booleanToInt(rank.isKick()));
                    preparedStatement.setInt(31, booleanToInt(rank.isAcceptTpa()));
                    preparedStatement.setInt(32, booleanToInt(rank.isStartWar()));
                    preparedStatement.setInt(33, booleanToInt(rank.isModifyRanks()));
                    preparedStatement.setInt(34, booleanToInt(rank.isLevel()));

                    preparedStatement.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactionPlayers() {
        for (Faction value : factionHandler.getFactions()) {
            for (FactionPlayer factionPlayer : value.getMember()) {
                try {
                    PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("INSERT INTO factionplayers (uuid, name, factionid, rankid) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE uuid = ?, name = ?, factionid = ?, rankid = ?");
                    preparedStatement.setString(1, factionPlayer.getUuid());
                    preparedStatement.setString(2, factionPlayer.getName());
                    preparedStatement.setString(3, factionPlayer.getFactionID());
                    preparedStatement.setInt(4, factionPlayer.getRank().getId());

                    preparedStatement.setString(5, factionPlayer.getUuid());
                    preparedStatement.setString(6, factionPlayer.getName());
                    preparedStatement.setString(7, factionPlayer.getFactionID());
                    preparedStatement.setInt(8, factionPlayer.getRank().getId());

                    preparedStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveBackpacks() {
        for (Backpack backpack : Factions.getInstance().getBackpackHandler().getBackpacks().values()) {

            try {
                PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("INSERT INTO backpacks (uuid, name, size, items) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE uuid =?, name = ?, size = ?, items = ?");
                preparedStatement.setString(1, backpack.getUuid());
                preparedStatement.setString(2, backpack.getOwner());
                preparedStatement.setInt(3, backpack.getSize());
                preparedStatement.setString(4, getBackpackHandler().itemStackArrayToBase64(backpack.getItems()));

                preparedStatement.setString(5, backpack.getUuid());
                preparedStatement.setString(6, backpack.getOwner());
                preparedStatement.setInt(7, backpack.getSize());
                preparedStatement.setString(8, Factions.getInstance().getBackpackHandler().itemStackArrayToBase64(backpack.getItems()));

                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public void savePlayers() {
        for (BasePlayer p : basePlayerHandler.getBasePlayers().values()) {
            try {
                PreparedStatement preparedStatement = getMySQL().getConn().prepareStatement("INSERT INTO baseplayers(uuid,playername,money,health,armor,expmultiplier,playerlevel,exp,skillpoints,attackdamage,attackspeed,movementspeed) VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE uuid = ?, playername = ?, money = ?, health = ?, armor = ?,expmultiplier = ?, playerlevel = ?,exp = ?,skillpoints = ?, attackdamage = ?,attackspeed = ?,movementspeed = ?");
                preparedStatement.setString(1, p.getUuid());
                preparedStatement.setString(2, p.getName());
                preparedStatement.setInt(3, p.getMoney());
                preparedStatement.setInt(4, p.getHealth());
                preparedStatement.setInt(5, p.getArmor());
                preparedStatement.setInt(6, p.getExpMultiplier());
                preparedStatement.setInt(7, p.getLevel());
                preparedStatement.setInt(8, p.getExp());
                preparedStatement.setInt(9, p.getSkillpoints());
                preparedStatement.setInt(10, p.getAttackDamage());
                preparedStatement.setInt(11, p.getAttackSpeed());
                preparedStatement.setInt(12, p.getMovementSpeed());

                preparedStatement.setString(13, p.getUuid());
                preparedStatement.setString(14, p.getName());
                preparedStatement.setInt(15, p.getMoney());
                preparedStatement.setInt(16, p.getHealth());
                preparedStatement.setInt(17, p.getArmor());
                preparedStatement.setInt(18, p.getExpMultiplier());
                preparedStatement.setInt(19, p.getLevel());
                preparedStatement.setInt(20, p.getExp());
                preparedStatement.setInt(21, p.getSkillpoints());
                preparedStatement.setInt(22, p.getAttackDamage());
                preparedStatement.setInt(23, p.getAttackSpeed());
                preparedStatement.setInt(24, p.getMovementSpeed());

                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void loadSchmatic(Location location, String fileName) {
        try {
            EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(location.getWorld()), -1);
            Clipboard clipboard;
            ClipboardFormat format = ClipboardFormats.findByFile(new File("plugins/WorldEdit/schematics/" + fileName));
            clipboard = format.getReader(new FileInputStream("plugins/WorldEdit/schematics/" + fileName)).read();

            Operation operation = new ClipboardHolder(clipboard).createPaste(session)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
            session.flushSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addXP(Player player, int xp) {
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 0.1F, 1);

        BasePlayer basePlayer = Factions.getInstance().getBasePlayerHandler().getBasePlayers().get(player.getName());
        basePlayer.setExp(basePlayer.getExp() + xp);

        for (int i = 0; i < 10; i++) {
            if (basePlayer.getExp() > Factions.getInstance().getBasePlayerHandler().getLevels().get(basePlayer.getLevel())) {
                basePlayer.setExp(basePlayer.getExp() - Factions.getInstance().getBasePlayerHandler().getLevels().get(basePlayer.getLevel()));
                basePlayer.setLevel(basePlayer.getLevel() + 1);
                basePlayer.setSkillpoints(basePlayer.getSkillpoints() + 2);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.1F, 1);
            }
        }

        player.setLevel(basePlayer.getLevel());
        player.setExp((float) basePlayer.getExp() / Factions.getInstance().getBasePlayerHandler().getLevels().get(basePlayer.getLevel()));
        player.sendMessage("§6§l+" + xp + " XP");
    }

    private int booleanToInt(Boolean b) {
        return b ? 1 : 0;
    }
}
