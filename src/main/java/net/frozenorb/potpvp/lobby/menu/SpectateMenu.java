package net.frozenorb.potpvp.lobby.menu;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchState;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.player.setting.Setting;
import net.frozenorb.potpvp.player.setting.SettingHandler;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpectateMenu extends PaginatedMenu {

    private final FileConfiguration config = PotPvPSI.getInstance().getConfig();

    public SpectateMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.translate(config.getString("TITLES.SPECTATE-MENU"));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;

        for (Match match : PotPvPSI.getInstance().getMatchHandler().getHostedMatches()) {
            // players can view this menu while spectating
            if (match.isSpectator(player.getUniqueId())) {
                continue;
            }

            if (match.getState() == MatchState.ENDING) {
                continue;
            }

            if (!PotPvPSI.getInstance().getTournamentHandler().isInTournament(match)) {
                int numTotalPlayers = 0;
                int numSpecDisabled = 0;

                for (MatchTeam team : match.getTeams()) {
                    for (UUID member : team.getAliveMembers()) {
                        numTotalPlayers++;

                        if (!settingHandler.getSetting(Bukkit.getPlayer(member), Setting.ALLOW_SPECTATORS)) {
                            numSpecDisabled++;
                        }
                    }
                }

                // if >= 50% of participants have spectators disabled
                // we won't render this match in the menu
                if ((float) numSpecDisabled / (float) numTotalPlayers >= 0.5) {
                    continue;
                }
            }

            buttons.put(i++, new SpectateButton(match));
        }

        return buttons;
    }

    // we lock the size of this inventory at full, otherwise we'll have
    // issues if it 'grows' into the next line while it's open (say we open
    // the menu with 8 entries, then it grows to 11 [and onto the second row]
    // - this breaks things)


    @Override
    public int size(Player player) {
        return 9 * 6;
    }
}