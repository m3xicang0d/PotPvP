package net.frozenorb.potpvp.game.nametag;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.follow.FollowHandler;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.game.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import net.frozenorb.potpvp.kt.nametag.NametagInfo;
import net.frozenorb.potpvp.kt.nametag.NametagProvider;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


public final class PotPvPNametagProvider extends NametagProvider {
    public PotPvPNametagProvider() {
        super("PotPvP Provider", 5);
    }


    public static String getNameColor(Player toRefresh, Player refreshFor) {
        MatchHandler handler = PotPvPSI.getInstance().getMatchHandler();

        if (PotPvPValidation.isInGame(toRefresh) && PotPvPValidation.isInGame(refreshFor)) {
            Game game = GameQueue.INSTANCE.getCurrentGame(toRefresh);
            return game.getEvent().getNameTag(game, toRefresh, refreshFor);
        }

        if (!handler.inEqualMatches(toRefresh, refreshFor)
                || (handler.isSpectatingMatch(toRefresh) && handler.isPlayingMatch(refreshFor))
        ) return getNameColorLobby(toRefresh, refreshFor);
        else return getNameColorMatch(toRefresh, refreshFor);
    }


    private static String getNameColorMatch(Player toRefresh, Player refreshFor) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        Match toRefreshMatch = matchHandler.getMatchPlayingOrSpectating(toRefresh);
        MatchTeam toRefreshTeam = toRefreshMatch.getTeam(toRefresh.getUniqueId());

        // they're a spectator, so we see them as gray
        if (toRefreshTeam == null) {
            return ChatColor.GRAY.toString();
        }

        MatchTeam refreshForTeam = toRefreshMatch.getTeam(refreshFor.getUniqueId());

        // if we can't find a current team, check if they have any
        // previously teams we can use for this
        if (refreshForTeam == null) {
            refreshForTeam = toRefreshMatch.getPreviousTeam(refreshFor.getUniqueId());
        }

        // if we were/are both on teams display a friendly/enemy color
        if (refreshForTeam != null) {
            if (toRefreshTeam == refreshForTeam) {
                return ChatColor.GREEN.toString();
            } else {
                if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && System.currentTimeMillis() < ArcherClass.getMarkedPlayers().get(toRefresh.getName())) {
                    return ChatColor.YELLOW.toString();
                }
                return ChatColor.RED.toString();
            }
        }

        // if we're a spectator just display standard colors
        List<MatchTeam> teams = toRefreshMatch.getTeams();

        // we have predefined colors for 'normal' matches
        if (teams.size() == 2) {
            // team 1 = LIGHT_PURPLE, team 2 = AQUA
            if (toRefreshTeam == teams.get(0)) {
                return ChatColor.LIGHT_PURPLE.toString();
            } else {
                return ChatColor.AQUA.toString();
            }
        } else {
            // we don't have colors defined for larger matches
            // everyone is just red for spectators
            return ChatColor.RED.toString();
        }
    }

    private static String getNameColorLobby(Player toRefresh, Player refreshFor) {
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();

        Optional<UUID> following = followHandler.getFollowing(refreshFor);
        boolean refreshForFollowingTarget = following.isPresent() && following.get().equals(toRefresh.getUniqueId());

        if (refreshForFollowingTarget) return ChatColor.AQUA.toString();
        else {
            return getNameColorRank(toRefresh);
        }
    }

    public static String getNameColorRank(Player player) {
        ConfigFile config = PotPvPSI.getInstance().getNametagsConfig();
        AtomicReference<String> nametagMatch = new AtomicReference<>();

        config.getConfiguration().getConfigurationSection("NAMETAGS").getKeys(false).forEach(rank -> {
            String nametag = config.getString("NAMETAGS." + rank + ".TAG");
            String node = config.getString("NAMETAGS." + rank + ".PERM");

            if (player.hasPermission(node) && !player.isOp()) nametagMatch.set(nametag);
        });

        if (nametagMatch.get() != null) {
            return ChatColor.translateAlternateColorCodes('&', nametagMatch.get());
        }

        return ChatColor.GREEN.toString(); //fallback
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        String prefix = getNameColor(toRefresh, refreshFor);
        return createNametag(prefix, "");
    }

}