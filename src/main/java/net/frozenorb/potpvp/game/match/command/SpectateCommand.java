package net.frozenorb.potpvp.game.match.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.player.setting.Setting;
import net.frozenorb.potpvp.player.setting.SettingHandler;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import net.frozenorb.potpvp.util.cooldown.Cooldown;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SpectateCommand {

    public static final int SPECTATE_COOLDOWN_SECONDS = 2;
    private static final Cooldown cooldown = new Cooldown();


    @Command(names = {"spectate", "spec"}, permission = "")
    public static void spectate(Player sender, @Param(name = "target") Player target) {
        if (PotPvPValidation.isInGame(sender)) return;
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot spectate yourself.");
            return;
        }
        if (cooldown.onCooldown(sender)) {
            sender.sendMessage(ChatColor.RED + "Please wait before using this command again.");
            return;
        }

        cooldown.applyCooldown(sender, SPECTATE_COOLDOWN_SECONDS);

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        ConfigFile config = PotPvPSI.getInstance().getMessagesConfig();
        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a match.");
            return;
        }

        //boolean bypassesSpectating = PotPvPSI.getInstance().getTournamentHandler().isInTournament(targetMatch);
        //Staff bypass
        if(!settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS)) {
            /*if(!staffMode) {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");
            } else {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            }
*/
            sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");

        } else {
            target.sendMessage(CC.translate(config.getString("SPECTATOR.PLAYER-MESSAGE"))
                    .replace("%player%", sender.getDisplayName()));

        }
        if ((!sender.isOp() && !sender.hasPermission("basic.staff")) && targetMatch.getTeams().size() == 2) {
            MatchTeam teamA = targetMatch.getTeams().get(0);
            MatchTeam teamB = targetMatch.getTeams().get(1);

            if (teamA.getAllMembers().size() == 1 && teamB.getAllMembers().size() == 1) {
                UUID teamAPlayer = teamA.getFirstMember();
                UUID teamBPlayer = teamB.getFirstMember();

                if (
                    !settingHandler.getSetting(Bukkit.getPlayer(teamAPlayer), Setting.ALLOW_SPECTATORS) ||
                        !settingHandler.getSetting(Bukkit.getPlayer(teamBPlayer), Setting.ALLOW_SPECTATORS)
                ) {
                    sender.sendMessage(ChatColor.RED + "Not all players in that 1v1 have spectators enabled.");
                    return;
                }
            }
        }

        Player teleportTo = null;

        // /spectate looks up matches being played OR watched by the target,
        // so we can only target them if they're not spectating
        if (!targetMatch.isSpectator(target.getUniqueId())) {
            teleportTo = target;
        }

        if (PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(sender)) {
            Match currentlySpectating = matchHandler.getMatchSpectating(sender);

            if (currentlySpectating != null) {
                if (currentlySpectating.equals(targetMatch)) {
                    sender.sendMessage(ChatColor.RED + "You're already spectating this match.");
                    return;
                }

                currentlySpectating.removeSpectator(sender);
            }

            targetMatch.addSpectator(sender, teleportTo);
        }
    }

}