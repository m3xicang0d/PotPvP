package net.frozenorb.potpvp;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.jesusmx.practice.practice.ability.AbilitySystem;
import me.jesusmx.practice.practice.ability.provider.PandaAbility;
import me.jesusmx.practice.practice.ability.provider.SladeAbility;
import me.jesusmx.practice.practice.integration.PotPvPScoreboard;
import me.jesusmx.practice.practice.integration.scoreboard.provider.game.GameScoreGetter;
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.LobbyScoreboard;
import me.jesusmx.practice.practice.integration.scoreboard.provider.match.MatchScoreboard;
import me.jesusmx.practice.practice.integration.scoreboard.provider.party.PartyScoreboard;
import net.frozenorb.potpvp.game.arena.ArenaHandler;
import net.frozenorb.potpvp.game.follow.FollowHandler;
import net.frozenorb.potpvp.game.kit.KitHandler;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.kittype.KitTypeJsonAdapter;
import net.frozenorb.potpvp.game.kittype.KitTypeParameterType;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.spectator.SpectatorHandler;
import net.frozenorb.potpvp.game.morpheus.EventListeners;
import net.frozenorb.potpvp.game.nametag.PotPvPNametagProvider;
import net.frozenorb.potpvp.game.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.game.pvpclasses.PvPClassHandler;
import net.frozenorb.potpvp.game.queue.QueueHandler;
import net.frozenorb.potpvp.game.tournament.TournamentHandler;
import net.frozenorb.potpvp.integration.holograms.HologramsHandler;
import net.frozenorb.potpvp.integration.spigot.chunk.ChunkSnap;
import net.frozenorb.potpvp.integration.tab.PotPvPLayoutProvider;
import net.frozenorb.potpvp.kt.command.CommandHandler;
import net.frozenorb.potpvp.kt.morpheus.Morpheus;
import net.frozenorb.potpvp.kt.morpheus.game.GameListeners;
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEvent;
import net.frozenorb.potpvp.kt.nametag.NametagEngine;
import net.frozenorb.potpvp.kt.potion.cache.PotionCache;
import net.frozenorb.potpvp.kt.potion.task.PotionTask;
import net.frozenorb.potpvp.kt.protocol.PingAdapter;
import net.frozenorb.potpvp.kt.redis.Redis;
import net.frozenorb.potpvp.kt.redis.RedisCredentials;
import net.frozenorb.potpvp.kt.util.serialization.*;
import net.frozenorb.potpvp.kt.visibility.VisibilityEngine;
import net.frozenorb.potpvp.listener.*;
import net.frozenorb.potpvp.listener.fixes.DurabilityFixListener;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.player.duel.DuelHandler;
import net.frozenorb.potpvp.player.elo.EloHandler;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.player.rematch.RematchHandler;
import net.frozenorb.potpvp.player.setting.SettingHandler;
import net.frozenorb.potpvp.player.statistics.StatisticsHandler;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import net.frozenorb.potpvp.util.menu.ButtonListener;
import net.frozenorb.potpvp.util.potpvp.PotPvPCache;
import net.frozenorb.potpvp.util.scoreboard.Assemble;
import net.frozenorb.potpvp.util.scoreboard.AssembleStyle;
import net.frozenorb.potpvp.util.tablist.shared.TabHandler;
import net.frozenorb.potpvp.util.tablist.versions.v1_8_R3.v1_8_R3TabAdapter;
import net.frozenorb.potpvp.util.uuid.IUUIDCache;
import net.frozenorb.potpvp.util.uuid.UUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.IOException;

@Getter
public final class PotPvPSI extends JavaPlugin {

    @Getter public static Gson gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
        .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
        .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
        .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
        .registerTypeHierarchyAdapter(KitType.class, new KitTypeJsonAdapter()) // custom KitType serializer
        .registerTypeAdapter(ChunkSnap.class, new ChunkSnapshotAdapter())
        .serializeNulls()
        .create();

    public static Gson plainGson = new GsonBuilder()
        .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
        .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
        .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
        .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
        .serializeNulls()
        .create();

    @Getter public static PotPvPSI instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    public ArenaHandler arenaHandler;
    public SettingHandler settingHandler;
    public DuelHandler duelHandler;
    public KitHandler kitHandler;
    public MatchHandler matchHandler;
    public PartyHandler partyHandler;
    public QueueHandler queueHandler;
    public RematchHandler rematchHandler;
    public PostMatchInvHandler postMatchInvHandler;
    public FollowHandler followHandler;
    public EloHandler eloHandler;
    public PvPClassHandler pvpClassHandler;
    public TournamentHandler tournamentHandler;
    public ChatColor dominantColor = ChatColor.RED;
    public PotPvPCache cache = new PotPvPCache();
    public LobbyHandler lobbyHandler;
    public VisibilityEngine visibilityEngine;
    public CommandHandler commandHandler;
    public IUUIDCache uuidCache;
    public NametagEngine nametagEngine;
    public Redis redis;

    //JESUSMX START
    public ConfigFile scoreboardConfig;
    public ConfigFile messagesConfig;
    public ConfigFile hotbarConfig;
    public ConfigFile hologramsConfig;
    public ConfigFile databaseConfig;
    public ConfigFile nametagsConfig;
    public ConfigFile tablistlobbyConfig;
    public ConfigFile tablistmatchConfig;
    public ConfigFile tablistmatchPartyConfig;
    public ConfigFile tablistHeadsConfig;
    public ConfigFile tablistspectatorParticipantConfig;
    public ConfigFile tablistspectatorNoParticipantConfig;
    public ConfigFile tablistspectatorPartyConfig;
    public ConfigFile tablistspectatorWithoutPartyConfig;
    public SpectatorHandler spectatorHandler;
    public AbilitySystem abilitySystem;
    public HologramsHandler hologramsHandler;
    public Assemble assemble;

    @Override
    public void onEnable() {
        PotPvPSI.instance = this;
        saveDefaultConfig();

        this.scoreboardConfig = new ConfigFile(this, "scoreboard");
        this.messagesConfig = new ConfigFile(this, "messages");
        this.hotbarConfig = new ConfigFile(this, "hotbar");
        this.databaseConfig = new ConfigFile(this, "database");
        this.nametagsConfig = new ConfigFile(this, "nametags");

        this.tablistHeadsConfig = new ConfigFile(this, "tablist/heads");
        this.tablistlobbyConfig = new ConfigFile(this, "tablist/in-lobby");

        this.tablistmatchConfig = new ConfigFile(this, "tablist/match/in-match");
        this.tablistmatchPartyConfig = new ConfigFile(this, "tablist/match/in-match-party");

/*        this.tablistspectatorParticipantConfig = new ConfigFile(this, "tablist/spectators");
        this.tablistspectatorNoParticipantConfig = new ConfigFile(this, "tablist/spectators");
        this.tablistspectatorPartyConfig = new ConfigFile(this, "tablist/spectators");
        this.tablistspectatorWithoutPartyConfig = new ConfigFile(this, "tablist/spectators");*/

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setWeatherDuration(0);
            world.setTime(6_000L);
        }

        this.setupMongo();
        this.setupRedis();

        commandHandler = new CommandHandler();
        commandHandler.load();
        commandHandler.registerParameterType(KitType.class, new KitTypeParameterType());
        commandHandler.registerAll(this);

        this.registerAbility();
        this.uuidCache = new UUIDCache();

        PotionCache.init();
        Bukkit.getServer().getScheduler().runTaskTimer(this, new PotionTask(), 10L, 10L);

        if (getConfig().getBoolean("SETTINGS.SCOREBOARD")) {
            assemble = new Assemble(this, new PotPvPScoreboard(new MatchScoreboard(), new LobbyScoreboard(), new GameScoreGetter(), new PartyScoreboard()));
            assemble.setTicks(2);
            assemble.setAssembleStyle(AssembleStyle.CUSTOM.descending(true).startNumber(8));
        }

        nametagEngine = new NametagEngine();
        nametagEngine.load();
        nametagEngine.registerProvider(new PotPvPNametagProvider());

        if (getConfig().getBoolean("SETTINGS.TABLIST")) {
            if (Bukkit.getVersion().contains("1.8")) {
                new TabHandler(new v1_8_R3TabAdapter(), new PotPvPLayoutProvider(), this, 20L);
            }
        }

        if (this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            this.hologramsConfig = new ConfigFile(this, "holograms");
            Bukkit.getServer().getScheduler().runTaskLater(this, () -> (hologramsHandler = new HologramsHandler()).load(), 20L);
        }

        visibilityEngine = new VisibilityEngine();
        visibilityEngine.load();

        ProtocolLibrary.getProtocolManager().addPacketListener(new PingAdapter());
        this.getServer().getPluginManager().registerEvents(new PingAdapter(), this);

        settingHandler = new SettingHandler();
        duelHandler = new DuelHandler();
        kitHandler = new KitHandler();
        lobbyHandler = new LobbyHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        partyHandler = new PartyHandler();
        queueHandler = new QueueHandler();
        rematchHandler = new RematchHandler();
        postMatchInvHandler = new PostMatchInvHandler();
        followHandler = new FollowHandler();
        eloHandler = new EloHandler();
        pvpClassHandler = new PvPClassHandler();
        tournamentHandler = new TournamentHandler();
        (spectatorHandler = new SpectatorHandler()).init();

        this.getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        this.getServer().getPluginManager().registerEvents(new BowHealthListener(), this);
        this.getServer().getPluginManager().registerEvents(new PearlCooldownListener(), this);
        this.getServer().getPluginManager().registerEvents(new RankedMatchQualificationListener(), this);
        this.getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
        this.getServer().getPluginManager().registerEvents(new StatisticsHandler(), this);
        this.getServer().getPluginManager().registerEvents(new EventListeners(), this);
        this.getServer().getPluginManager().registerEvents(new ProtectionListener(), this);
        this.getServer().getPluginManager().registerEvents(new JoinMessageListener(), this);
        this.getServer().getPluginManager().registerEvents(new ButtonListener(), this);
        this.getServer().getPluginManager().registerEvents(new ElevatorListener(), this);
        this.getServer().getPluginManager().registerEvents(new MatchSumoListener(), this);
        this.getServer().getPluginManager().registerEvents(new DurabilityFixListener(), this);

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, cache, 20L, 20L);

        new Morpheus(this);
        this.getServer().getPluginManager().registerEvents(new GameListeners(), this);
        GameEvent.getEvents().forEach(game -> { game.getListeners().forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this)); });
    }


    @Override
    public void onDisable() {
        for (Match match : this.matchHandler.getHostedMatches()) {
            if (match.getKitType().isBuildingAllowed()) match.getArena().restore();
        }
        try {
            arenaHandler.saveSchematics();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(this.getServer().getPlayerExact(playerName));
        }
        if(hologramsHandler != null) {
            hologramsHandler.save();
        }
    }

    private void setupRedis() {
        this.redis = new Redis();
        RedisCredentials local = new RedisCredentials(databaseConfig.getString("LOCALREDIS.HOST"), databaseConfig.getInt("LOCALREDIS.PORT"), "", 0);
        RedisCredentials back = new RedisCredentials(databaseConfig.getString("BACKBONEREDIS.HOST"), databaseConfig.getInt("BACKBONEREDIS.PORT"), "", 0);
        this.redis.load(back, local);
        this.getServer().getConsoleSender().sendMessage(CC.translate("&7[&6Practice&7] &aRedis started successfully"));
    }

    private void setupMongo() {
        mongoClient = new MongoClient(new ServerAddress(databaseConfig.getString("MONGO.HOST"), databaseConfig.getInt("MONGO.PORT")));
        String databaseId = databaseConfig.getString("MONGO.DATABASE");
        mongoDatabase = mongoClient.getDatabase(databaseId);
        this.getServer().getConsoleSender().sendMessage(CC.translate("&7[&6Practice&7] &aMongoDB started successfully"));
    }

    private void registerAbility() {
        String ability = getConfig().getString("SETTINGS.ABILITY");
        switch (ability) {
            case "SladeAbility":
                abilitySystem = new SladeAbility();
                return;
            case "PandaAbility":
                abilitySystem = new PandaAbility();
                return;
        }
        this.getServer().getConsoleSender().sendMessage(CC.translate("&7[&6Practice&7] " + ability + " &ahas hooked on the practice"));
    }

    public static class ChunkSnapshotAdapter extends TypeAdapter<ChunkSnap> {
        @Override
        public ChunkSnap read(JsonReader arg0) throws IOException {
            return null;
        }
        @Override
        public void write(JsonWriter arg0, ChunkSnap arg1) throws IOException {
        }
    }
}