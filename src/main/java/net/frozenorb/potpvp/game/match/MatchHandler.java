package net.frozenorb.potpvp.game.match;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.jesusmx.practice.practice.game.match.MatchFreezeListener;
import me.jesusmx.practice.practice.game.modes.BaseRaidingMode;
import me.jesusmx.practice.practice.game.modes.BoxingMode;
import me.jesusmx.practice.practice.game.modes.SumoMode;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.arena.Arena;
import net.frozenorb.potpvp.game.arena.ArenaHandler;
import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.match.listener.*;
import net.frozenorb.potpvp.game.match.modes.ComboMode;
import net.frozenorb.potpvp.game.match.modes.SoupMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class MatchHandler {

    public static final String MONGO_COLLECTION_NAME = "matches";

    public final Set<Match> hostedMatches = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Getter @Setter public boolean rankedMatchesDisabled;
    @Getter @Setter public boolean unrankedMatchesDisabled;

    // these two maps are one of my least favorite bits of code in the match module, but
    // they let us run O(1) lookups of player matches, so we absolutely should keep them.
    // we do, however, have to be very careful to keep them updated with the "actuaL" data.
    // if we ever have issues with these, we have /pstatus to compare the "actual" data (O(n) scan)
    // with the O(1) data here
    @Getter(AccessLevel.PACKAGE) public final Map<UUID, Match> playingMatchCache = new ConcurrentHashMap<>();
    @Getter(AccessLevel.PACKAGE) public final Map<UUID, Match> spectatingMatchCache = new ConcurrentHashMap<>();

    public MatchHandler() {
        //Register PvPModes
        new BoxingMode().register();
        new BaseRaidingMode().register();
        new SumoMode().register();
        Bukkit.getPluginManager().registerEvents(new HorseListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new GoldenHeadListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitSelectionListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchBlockPickupListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchBuildListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new ComboMode(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchCountdownListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDeathMessageListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDurationLimitListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchEnderPearlDamageListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchFreezeListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchGeneralListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchHardcoreHealingListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchHealthDisplayListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchPartySpectateListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchRodListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new SoupMode(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchStatsListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchWizardListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new SpectatorItemListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new SpectatorPreventionListener(), PotPvPSI.getInstance());
    }

    public Match startMatch(List<MatchTeam> teams, KitType kitType, boolean ranked, boolean allowRematches) {
        boolean anyOps = false;

        for (MatchTeam team : teams) {
            for (UUID member : team.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(member);

                if (!anyOps && memberPlayer.isOp()) {
                    anyOps = true;
                }

                if (isPlayingOrSpectatingMatch(memberPlayer)) {
                    throw new IllegalArgumentException(PotPvPSI.getInstance().getUuidCache().name(member) + " is already in a match!");
                }
            }
        }

        if (!anyOps) {
            if (ranked && rankedMatchesDisabled) {
                throw new IllegalArgumentException("Ranked match creation is disabled!");
            } else if (unrankedMatchesDisabled) {
                throw new IllegalArgumentException("Unranked match creation is disabled!");
            }
        }

        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        long matchSize = teams.stream()
                .mapToInt(t -> t.getAllMembers().size())
                .sum();

        // the archer only logic here was often a source of confusion while
        // this code was being written. below is a table of the desired
        // results / if a match can run in a given arena
        //
        //              Arena is archer only    Arena is not archer only
        //  Is Archer           Yes                         Yes
        // Not Archer           No                          Yes
        //
        // the left side of the or statement covers the top row, and the
        // right side covers the right side
        Optional<Arena> openArenaOpt = arenaHandler.allocateUnusedArena(schematic ->
                schematic.isEnabled() &&
                        !schematic.isGameMapOnly() &&
                        !schematic.isTeamFightsOnly() &&
                        canUseSchematic(kitType, schematic) &&
                        matchSize <= schematic.getMaxPlayerCount() &&
                        matchSize >= schematic.getMinPlayerCount() &&
                        (!ranked || schematic.isSupportsRanked()) &&
                        (kitType.getId().equals("ARCHER") || !schematic.isArcherOnly())
        );

        if (kitType.equals(KitType.teamFight)) {
            openArenaOpt = arenaHandler.allocateUnusedArena(schematic ->
                    schematic.isEnabled() &&
                            canUseSchematic(kitType, schematic) &&
                            matchSize <= schematic.getMaxPlayerCount() &&
                            matchSize >= schematic.getMinPlayerCount() &&
                            !schematic.isGameMapOnly() &&
                            schematic.isTeamFightsOnly()
            );
        }

        if (!openArenaOpt.isPresent()) {
            PotPvPSI.getInstance().getLogger().warning("Failed to start match: No open arenas found");
            return null;
        }

        Match match = new Match(kitType, openArenaOpt.get(), teams, ranked, allowRematches);

        hostedMatches.add(match);
        match.startCountdown();

        return match;
    }

    public Match startMatch(List<MatchTeam> teams, KitType kitType, boolean ranked, boolean allowRematches, ArenaSchematic arenaSchematic) {
        boolean anyOps = false;

        for (MatchTeam team : teams) {
            for (UUID member : team.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(member);

                if (!anyOps && memberPlayer.isOp()) {
                    anyOps = true;
                }

                if (isPlayingOrSpectatingMatch(memberPlayer)) {
                    throw new IllegalArgumentException(PotPvPSI.getInstance().getUuidCache().name(member) + " is already fighting other player!");
                }
            }
        }

        if (!anyOps) {
            if (ranked && rankedMatchesDisabled) {
                throw new IllegalArgumentException("Ranked match creation is disabled!");
            } else if (unrankedMatchesDisabled) {
                throw new IllegalArgumentException("Unranked match creation is disabled!");
            }
        }

        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        Optional<Arena> openArenaOpt = arenaHandler.allocateUnusedArena(schematic ->
                schematic.isEnabled() &&
                        !schematic.isGameMapOnly() &&
                        schematic == arenaSchematic
        );

        if (!openArenaOpt.isPresent()) {
            PotPvPSI.getInstance().getLogger().warning("Failed to start match: No open arenas found");
            return null;
        }

        Match match = new Match(kitType, openArenaOpt.get(), teams, ranked, allowRematches);

        hostedMatches.add(match);
        match.startCountdown();

        return match;
    }


    public static boolean canUseSchematic(KitType kitType, ArenaSchematic schematic) {
        String kitId = kitType.getId();

        if (kitId.equalsIgnoreCase("baseraiding")) return schematic.isRaidingOnly();
        if (kitId.equalsIgnoreCase("skywars")) return schematic.isSkywarsOnly();

        if (kitId.equalsIgnoreCase("ARCHER")) return schematic.isArcherOnly();
        if (kitId.equalsIgnoreCase("BUILDUHC")) return schematic.isBuildUHCOnly();
        if (kitId.equalsIgnoreCase("SPLEEF")) return schematic.isSpleefOnly();
        if (kitId.equalsIgnoreCase("SUMO")) return schematic.isSumoOnly();
        if (kitId.equalsIgnoreCase("HCF")) return schematic.isHCFOnly();
        if (kitType.equals(KitType.teamFight)) return schematic.isTeamFightsOnly();


        if (schematic.isRaidingOnly()) return kitId.equalsIgnoreCase("baseraiding");
        if (schematic.isSkywarsOnly()) return kitId.equalsIgnoreCase("skywars");
        if (schematic.isArcherOnly()) return kitId.equalsIgnoreCase("ARCHER");
        if (schematic.isBuildUHCOnly()) return kitId.equalsIgnoreCase("BUILDUHC");
        if (schematic.isSpleefOnly()) return kitId.equalsIgnoreCase("SPLEEF");
        if (schematic.isSumoOnly()) return kitId.equalsIgnoreCase("SUMO");
        if (schematic.isHCFOnly()) return kitId.equalsIgnoreCase("HCF");

        return true;
    }

    void removeMatch(Match match) {
        hostedMatches.remove(match);
    }

    /**
     * Gets a read-only set containing all matches currently being hosted.
     * This includes matches that are pre-start and ending.
     *
     * @return a read-only representation of all hosted matches
     */
    public Set<Match> getHostedMatches() {
        return ImmutableSet.copyOf(hostedMatches);
    }

    /**
     * Returns a sum of all players who are playing in an IN_PROGRESS match
     *
     * @return number of players playing in IN_PROGRESS matches
     */
    public int countPlayersPlayingInProgressMatches() {
        return countPlayersPlayingMatches(m -> m.getState() == MatchState.COUNTDOWN || m.getState() == MatchState.IN_PROGRESS);
    }

    /**
     * Returns a sum of all players who are playing in a {@link Match}
     * that passes the {@link Predicate} provided.
     *
     * @return number of players playing in matches that
     * pass the {@link Predicate} provided
     */
    public int countPlayersPlayingMatches(Predicate<Match> inclusionPredicate) {
        int result = 0;

        for (Match match : hostedMatches) {
            if (inclusionPredicate.test(match)) {
                for (MatchTeam team : match.getTeams()) {
                    result += team.getAliveMembers().size();
                }
            }
        }

        return result;
    }

    /**
     * Gets the match currently being played by the player provided.
     * In this context, played means a player who alive and fighting on a team
     *
     * @param player player to look up match for
     * @return the match being played by the provided player
     */
    public Match getMatchPlaying(Player player) {
        return playingMatchCache.get(player.getUniqueId());
    }

    /**
     * Gets the match currently being spectated by the player provided.
     * In this context, spectated includes both players who died while
     * fighting who have not left and players who joined via /spectate.
     *
     * @param player player to look up match for
     * @return the match being spectated by the provided player
     */
    public Match getMatchSpectating(Player player) {
        return spectatingMatchCache.get(player.getUniqueId());
    }

    /**
     * Gets the match currently being spectated or played by the player provided.
     * This method acts as a combination of {@link MatchHandler#getMatchPlaying(Player)}
     * and {@link MatchHandler#getMatchSpectating(Player)}.
     *
     * @param player player to look up match for
     * @return the match being played or spectated by the provided player
     */
    public Match getMatchPlayingOrSpectating(Player player) {
        Match playing = playingMatchCache.get(player.getUniqueId());

        if (playing != null) {
            return playing;
        } else {
            return spectatingMatchCache.get(player.getUniqueId());
        }
    }

    /**
     * Checks if the player specified is playing a match.
     * See {@link MatchHandler#getMatchPlaying(Player)} for a definition
     * of the term playing.
     *
     * @param player player to look up match for
     * @return if a match is being played by the provided player
     */
    public boolean isPlayingMatch(Player player) {
        return playingMatchCache.containsKey(player.getUniqueId());
    }

    /**
     * Checks if the player specified is spectating a match.
     * See {@link MatchHandler#getMatchSpectating(Player)} (UUID)} for a
     * definition of the term spectating.
     *
     * @param player player to look up match for
     * @return if a match is being spectated by the provided player
     */
    public boolean isSpectatingMatch(Player player) {
        return spectatingMatchCache.containsKey(player.getUniqueId());
    }

    /**
     * Checks if the player specified is playing or spectating a match.
     * See {@link MatchHandler#getMatchPlayingOrSpectating(Player)} for a definition
     * of the term playing or spectating.
     *
     * @param player player to look up match for
     * @return if a match is being played or spectated by the provided player
     */
    public boolean isPlayingOrSpectatingMatch(Player player) {
        if (player == null) return false;
        return playingMatchCache.containsKey(player.getUniqueId()) || spectatingMatchCache.containsKey(player.getUniqueId());
    }

    /**
     * Checks if two specified players share the same match, whether they are
     * spectating or playing it.
     *
     * @param player  Player you want to compare to other
     * @param player2 The player you will compare the first one to
     * @return if both players share the same match
     */
    public boolean inEqualMatches(Player player, Player player2) {
        if (player == null || player2 == null) return false;
        if (getMatchPlayingOrSpectating(player) == null || getMatchPlayingOrSpectating(player2) == null) return false;
        return getMatchPlayingOrSpectating(player) == getMatchPlayingOrSpectating(player2);
    }
}