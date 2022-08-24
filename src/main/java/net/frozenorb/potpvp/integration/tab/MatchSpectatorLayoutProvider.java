package net.frozenorb.potpvp.integration.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.tablist.shared.entry.TabElement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;

final class MatchSpectatorLayoutProvider implements BiConsumer<Player, TabElement> {

    @Override
    public void accept(Player player, TabElement tabLayout) {
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchSpectating(player);
        MatchTeam oldTeam = match.getTeam(player.getUniqueId());
        List<MatchTeam> teams = match.getTeams();

        List<String> list = Arrays.asList("LEFT", "MIDDLE", "RIGHT", "FAR-RIGHT");

        // if it's one team versus another
        if (teams.size() == 2) {
            MatchTeam teamOne = teams.get(0);
            MatchTeam teamTwo = teams.get(1);

            boolean duel = teamOne.getAllMembers().size() == 1 && teamTwo.getAllMembers().size() == 1;

            int teamStart = 4;
            int teamStop = 20;
            int otherTeamStart = 4;
            int otherTeamStop = 20;

            // first, we want to check if they were a part of the match and died, and if so, render the tab differently.
            if (oldTeam != null) {
                // if they were, it means it couldn't have been a duel, so we don't check for that below.
                MatchTeam ourTeam = teamOne == oldTeam ? teamOne : teamTwo;
                MatchTeam otherTeam = teamOne == ourTeam ? teamTwo : teamOne;
                if (!duel) {
                    tabLayout.add(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "Team " + ChatColor.GREEN + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
                    tabLayout.add(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Enemies " + ChatColor.RED + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
                } else {
                    tabLayout.add(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "You");
                    tabLayout.add(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Opponent");
                }
                renderTeamMemberOverviewEntries(tabLayout, ourTeam, 0, teamStart, teamStop, ChatColor.GREEN);
                renderTeamMemberOverviewEntries(tabLayout, otherTeam, 2, otherTeamStart, otherTeamStop, ChatColor.RED);
            } else {
                if (!duel) {
                    tabLayout.add(0, 3, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Team One (" + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size() + ")");
                    tabLayout.add(2, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Team Two (" + teamTwo.getAliveMembers().size() + "/" + teamTwo.getAllMembers().size() + ")");
                } else {
                    tabLayout.add(0, 3, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Player One");
                    tabLayout.add(2, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Player Two");
                }
                renderTeamMemberOverviewEntries(tabLayout, teamOne, 0, teamStart, teamStop, ChatColor.LIGHT_PURPLE);
                renderTeamMemberOverviewEntries(tabLayout, teamTwo, 2, teamStart, teamStop, ChatColor.AQUA);
            }
        } else { // it's an FFA or something else like that
            tabLayout.add(1, 3, ChatColor.BLUE + ChatColor.BOLD.toString() + "Party FFA");

            int x = 0;
            int y = 4;

            Map<String, Integer> entries = new LinkedHashMap<>();

            if (oldTeam != null) {
                // if they were a part of this match, we want to render it like we would for an alive player, showing their team-mates first and in green.
                entries = renderTeamMemberOverviewLines(oldTeam, ChatColor.GREEN);

                {
                    // this is where we'll be adding everyone else
                    Map<String, Integer> deadLines = new LinkedHashMap<>();

                    for (MatchTeam otherTeam : match.getTeams()) {
                        if (otherTeam == oldTeam) {
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
            } else {
                // if they're just a random spectator, we'll pick different colors for each team.
                Map<String, Integer> deadLines = new LinkedHashMap<>();

                for (MatchTeam team : match.getTeams()) {
                    for (UUID enemy : team.getAllMembers()) {
                        if (team.isAlive(enemy)) {
                            entries.put("&c" + PotPvPSI.getInstance().getUuidCache().name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
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

                tabLayout.add(x++, y, entry.getKey(), entry.getValue());

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
                        tabLayout.add(x, y, ChatColor.GREEN + "+" + aliveLeft);
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

    public void renderTeamMemberOverviewEntries(TabElement layout, MatchTeam team, int column, int start, int stop, ChatColor color) {
        List<Map.Entry<String, Integer>> result = new ArrayList<>(renderTeamMemberOverviewLines(team, color).entrySet());

        // how many spots we have left
        int spotsLeft = stop - start;

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