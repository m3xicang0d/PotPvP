package net.frozenorb.potpvp.integration.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import net.frozenorb.potpvp.util.tablist.headutil.StringUtils;
import net.frozenorb.potpvp.util.tablist.shared.entry.TabElement;
import net.frozenorb.potpvp.util.tablist.shared.skin.SkinType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;

final class MatchParticipantLayoutProvider implements BiConsumer<Player, TabElement> {

    private final ConfigFile matchConfig = PotPvPSI.instance.getTablistmatchConfig();
    private final ConfigFile matchPartyConfig = PotPvPSI.instance.getTablistmatchPartyConfig();

    @Override
    public void accept(Player player, TabElement element) {
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player);
        List<MatchTeam> teams = match.getTeams();

        List<String> list = Arrays.asList("LEFT", "MIDDLE", "RIGHT", "FAR-RIGHT");


        // if it's one team versus another
        if (teams.size() == 2) {
            // this method won't be called if the player isn't a participant
            MatchTeam ourTeam = match.getTeam(player.getUniqueId());
            MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);

            boolean duel = ourTeam.getAllMembers().size() == 1 && otherTeam.getAllMembers().size() == 1;

            int teamStart = 4;
            int teamStop = 20;
            int otherTeamStart = 4;
            int otherTeamStop = 20;
            if (!duel) {
                for (int i = 0; i < 4; ++i) {
                    String s = list.get(i);
                    for (int l = 0; l < 20; ++l) {
                        String str = matchPartyConfig.getString("TABLIST." + s + "." + (l + 1))
                                .replace("%player%", player.getDisplayName())
                                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                                .replace("%ping%", String.valueOf(PlayerUtils.getPing(player)))
                                .replace("%in-fights%", String.valueOf(PotPvPSI.getInstance().matchHandler.countPlayersPlayingInProgressMatches()));


                        //Heads start
                        SkinType skinType = SkinType.DARK_GRAY;
                        if(str.toLowerCase(Locale.ROOT).contains("<skin=")) {
                            String skin = StringUtils.after(StringUtils.before(str, ">"), "<skin=");
                            String input = "<skin=" + skin + ">";
                            if(skin.equalsIgnoreCase("$self")) {
                                skinType = SkinType.fromUsername(player.getName());
                            } else {
                                skinType = SkinType.fromUsername(skin);
                            }
                            str = str.replace(input, "");
                        }
                        //Heads end

                        //Team start
                        str = str.replace("%team-size%", String.valueOf(ourTeam.getAliveMembers().size()))
                                .replace("%opponent-size%", String.valueOf(otherTeam.getAliveMembers().size()));

                        if(str.contains("%team-start-players%")) {
                            teamStart = l;
                            continue;
                        }
                        if(str.contains("%team-stop-players%")) {
                            teamStop = l;
                            continue;
                        }
                        if(str.contains("opponent-start-players%")) {
                            otherTeamStart = l;
                            continue;
                        }
                        if(str.contains("%opponent-stop-players%")) {
                            otherTeamStop = l;
                            continue;
                        }

                        element.add(i, l, str, 0, skinType.getSkinData());
                    }
                }
//                tabLayout.add(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "Team " + ChatColor.GREEN + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
//                tabLayout.add(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Enemies " + ChatColor.RED + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");

            } else {
                for (int i = 0; i < 4; ++i) {
                    String s = list.get(i);
                    for (int l = 0; l < 20; ++l) {
                        String str = matchConfig.getString("TABLIST." + s + "." + (l + 1))
                                .replace("%player%", player.getDisplayName())
                                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                                .replace("%ping%", String.valueOf(PlayerUtils.getPing(player)))
                                .replace("%in-fights%", String.valueOf(PotPvPSI.getInstance().matchHandler.countPlayersPlayingInProgressMatches()));



                        //Heads start
                        SkinType skinType = SkinType.DARK_GRAY;
                        if (str.toLowerCase(Locale.ROOT).contains("<skin=")) {
                            String skin = StringUtils.after(StringUtils.before(str, ">"), "<skin=");
                            String input = "<skin=" + skin + ">";
                            if (skin.equalsIgnoreCase("$self")) {
                                skinType = SkinType.fromUsername(player.getName());
                            } else {
                                skinType = SkinType.fromUsername(skin);
                            }
                            str = str.replace(input, "");
                        }
                        if(str.contains("%you%")) {
                            teamStart = l;
                            continue;
                        }
                        if(str.contains("%opponent%")) {
                            otherTeamStart = l;
                            continue;
                        }
                        element.add(i, l, str, 0, skinType.getSkinData());
                    }
                }
//                element.add(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "You");
//                element.add(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Opponent");
            }
            renderTeamMemberOverviewEntries(element, ourTeam, 0, teamStart, teamStop, ChatColor.GREEN);
            renderTeamMemberOverviewEntries(element, otherTeam, 2, otherTeamStart, otherTeamStop, ChatColor.RED);
        } else { // it's an FFA or something else like that
            element.add(1, 3, ChatColor.BLUE + ChatColor.BOLD.toString() + "Party FFA");

            int x = 0;
            int y = 4;

            Map<String, Integer> entries = new LinkedHashMap<>();

            MatchTeam ourTeam = match.getTeam(player.getUniqueId());

            {
                // this is where we'll be adding our team members

                Map<String, Integer> aliveLines = new LinkedHashMap<>();
                Map<String, Integer> deadLines = new LinkedHashMap<>();

                // separate lists to sort alive players before dead
                // + color differently
                for (UUID teamMember : ourTeam.getAllMembers()) {
                    if (ourTeam.isAlive(teamMember)) {
                        aliveLines.put(ChatColor.GREEN + PotPvPSI.getInstance().getUuidCache().name(teamMember),  PotPvPLayoutProvider.getPingOrDefault(teamMember));
                    } else {
                        deadLines.put("&7&m" + PotPvPSI.getInstance().getUuidCache().name(teamMember), PotPvPLayoutProvider.getPingOrDefault(teamMember));
                    }
                }

                entries.putAll(aliveLines);
                entries.putAll(deadLines);
            }

            {
                // this is where we'll be adding everyone else
                Map<String, Integer> deadLines = new LinkedHashMap<>();

                for (MatchTeam otherTeam : match.getTeams()) {
                    if (otherTeam == ourTeam) {
                        continue;
                    }

                    // separate lists to sort alive players before dead
                    // + color differently
                    for (UUID enemy : otherTeam.getAllMembers()) {
                        if (otherTeam.isAlive(enemy)) {
                            entries.put(ChatColor.RED + PotPvPSI.getInstance().getUuidCache().name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                        } else {
                            deadLines.put("&7&m" + PotPvPSI.getInstance().getUuidCache().name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                        }
                    }
                }

                entries.putAll(deadLines);
            }

            List<Map.Entry<String, Integer>> result = new ArrayList<>(entries.entrySet());

            // actually display our entries
            for (int index = 0; index < result.size(); index++) {
                Map.Entry<String, Integer> entry = result.get(index);

                element.add(x++, y, entry.getKey(), entry.getValue());

                if (x == 3 && y == PotPvPLayoutProvider.MAX_TAB_Y) {
                    // if we're at the last slot, we want to see if we still have alive players to show
                    int aliveLeft = 0;

                    for (int i = index; i < result.size(); i++) {
                        String currentEntry = result.get(i).getKey();
                        boolean dead = ChatColor.getLastColors(currentEntry).equals(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString());

                        if (!dead) {
                            aliveLeft++;
                        }
                    }

                    if (aliveLeft != 0 && aliveLeft != 1) {
                        // if there are players we weren't able to show and if it's more than one
                        // (if it's only one they'll be shown as the last entry [see 17 lines above]), display the number
                        // of alive players we weren't able to show instead.
                        element.add(x, y, ChatColor.GREEN + "+" + aliveLeft);
                    }

                    break;
                }

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
        }
    }

    public void renderTeamMemberOverviewEntries(TabElement layout, MatchTeam team, int column, int start, int end, ChatColor color) {
        List<Map.Entry<String, Integer>> result = new ArrayList<>(renderTeamMemberOverviewLines(team, color).entrySet());

        // how many spots we have left
        int spotsLeft = end - start;

        // we could've used the 'start' variable, but we create a new one for readability.
        int y = start;

        for (int index = 0; index < result.size(); index++) {
            Map.Entry<String, Integer> entry = result.get(index);

            // we check if we only have 1 more spot to show
            if (spotsLeft == 1) {
                // if so, count how many alive players we have left to show
                int aliveLeft = 0;

                for (int i = index; i < result.size(); i++) {
                    String currentEntry = result.get(i).getKey();
                    boolean dead = !ChatColor.getLastColors(currentEntry).equals(color.toString());

                    if (!dead) {
                        aliveLeft++;
                    }
                }

                // if we have any
                if (aliveLeft != 0) {
                    if (aliveLeft == 1) {
                        // if it's only one, we display them as the last entry
                        layout.add(column, y, entry.getKey(), entry.getValue());
                    } else {
                        // if it's more than one, display a number of how many we couldn't display.
                        layout.add(column, y, color + "+" + aliveLeft);
                    }
                }

                break;
            }

            // if not, just display the entry.
            layout.add(column, y, entry.getKey(), entry.getValue());
            y++;
            spotsLeft--;
        }
    }

    public Map<String, Integer> renderTeamMemberOverviewLines(MatchTeam team, ChatColor aliveColor) {
        Map<String, Integer> aliveLines = new LinkedHashMap<>();
        Map<String, Integer> deadLines = new LinkedHashMap<>();

        for (UUID member : team.getAllMembers()) {
            int ping = PotPvPLayoutProvider.getPingOrDefault(member);

            if (team.isAlive(member)) {
                aliveLines.put(aliveColor + PotPvPSI.getInstance().getUuidCache().name(member), ping);
            } else {
                deadLines.put(CC.translate("&7&m" + PotPvPSI.getInstance().getUuidCache().name(member)), ping);
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();

        result.putAll(aliveLines);
        result.putAll(deadLines);

        return result;
    }

}