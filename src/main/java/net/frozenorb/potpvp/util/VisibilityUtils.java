package net.frozenorb.potpvp.util;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.follow.FollowHandler;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.player.setting.Setting;
import net.frozenorb.potpvp.player.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public final class VisibilityUtils {

    public static void updateVisibilityFlicker(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            target.hidePlayer(otherPlayer);
            otherPlayer.hidePlayer(target);
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> updateVisibility(target), 10L);
    }

    public static void updateVisibility(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (shouldSeePlayer(otherPlayer, target)) {
                otherPlayer.showPlayer(target);
            } else {
                otherPlayer.hidePlayer(target);
            }
            // target.showPlayer(otherPlayer);
        }
    }


    public static boolean shouldSeePlayer(Player viewer, Player target) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch == null) {
            Party targetParty = partyHandler.getParty(target);
            Optional<UUID> following = followHandler.getFollowing(viewer);

            boolean viewerPlayingMatch = matchHandler.isPlayingOrSpectatingMatch(viewer);
            boolean viewerSameParty = targetParty != null && targetParty.isMember(viewer.getUniqueId());
            boolean viewerFollowingTarget = following.isPresent() && following.get().equals(target.getUniqueId());

            Game game = GameQueue.INSTANCE.getCurrentGame(target);
            if(game != null){
                if(game.getPlayers().contains(target)) return true;
            }

            if(viewer.hasPermission("tfs.practice.view") && target.hasPermission("tfs.practice.view"))
                return true;

            return viewerPlayingMatch || viewerSameParty || viewerFollowingTarget;
        }
        boolean targetIsSpectator = targetMatch.isSpectator(target.getUniqueId());
        boolean viewerSpecSetting = settingHandler.getSetting(viewer, Setting.VIEW_OTHER_SPECTATORS);
        boolean viewerIsSpectator = matchHandler.isSpectatingMatch(viewer);
        return !targetIsSpectator || (viewerSpecSetting && viewerIsSpectator && !target.hasMetadata("ModMode"));
    }
}