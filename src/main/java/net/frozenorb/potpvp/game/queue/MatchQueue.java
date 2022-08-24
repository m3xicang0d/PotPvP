package net.frozenorb.potpvp.game.queue;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.player.elo.EloHandler;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MatchQueue {

    @Getter
    public final KitType kitType;
    @Getter
    public final boolean ranked;
    public final List<MatchQueueEntry> entries = new CopyOnWriteArrayList<>();

    MatchQueue(KitType kitType, boolean ranked) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.ranked = ranked;
    }

    void tick() {
        // we clone so we can remove entries from our working set
        // (sometimes matches fail to create [ex no maps open] and
        // we should retry)
        List<MatchQueueEntry> entriesCopy = new ArrayList<>(entries);
        EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();

        // ranked match algorithm requires entries are in
        // order by elo. There's no reason we only do this for ranked
        // matches aside from performance
        if (ranked) {
            entriesCopy.sort(Comparator.comparing(e -> eloHandler.getElo(e.getMembers(), kitType)));
        }

        while (entriesCopy.size() >= 2) {
            // remove from 0 both times because index shifts down
            MatchQueueEntry a = entriesCopy.remove(0);
            MatchQueueEntry b = entriesCopy.remove(0);

            // the algorithm for ranked and unranked queues is actually very similar,
            // except for the fact ranked matches can't be made if the elo window for
            // both players don't overlap
            if (ranked) {
                int aElo = eloHandler.getElo(a.getMembers(), kitType);
                int bElo = eloHandler.getElo(b.getMembers(), kitType);

                int aEloWindow = a.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;
                int bEloWindow = b.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;

                if (Math.abs(aElo - bElo) > Math.max(aEloWindow, bEloWindow)) {
                    continue;
                }
            }

            createMatchAndRemoveEntries(a, b);
        }
    }

    public int countPlayersQueued() {
        int count = 0;

        for (MatchQueueEntry entry : entries) {
            count += entry.getMembers().size();
        }

        return count;
    }

    void addToQueue(MatchQueueEntry entry) {
        entries.add(entry);
    }

    void removeFromQueue(MatchQueueEntry entry) {
        entries.remove(entry);
    }

    public void createMatchAndRemoveEntries(MatchQueueEntry entryA, MatchQueueEntry entryB) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();

        MatchTeam teamA = new MatchTeam(entryA.getMembers());
        MatchTeam teamB = new MatchTeam(entryB.getMembers());

        Match match = matchHandler.startMatch(
            ImmutableList.of(teamA, teamB),
            kitType,
            ranked,
            !ranked // allowRematches is the inverse of ranked
        );

        // only remove entries if match creation was successfull
        if (match != null) {
            queueHandler.removeFromQueueCache(entryA);
            queueHandler.removeFromQueueCache(entryB);

            String teamAElo = "";
            String teamBElo = "";

            if (ranked) {
                EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();

                teamAElo = ChatColor.GRAY.toString() + " (" + ChatColor.GREEN.toString() + eloHandler.getElo(teamA.getAliveMembers(), kitType) + " Elo" + ChatColor.GRAY.toString() + ")";
                teamBElo = ChatColor.GRAY.toString() + " (" + ChatColor.GREEN.toString() + eloHandler.getElo(teamB.getAliveMembers(), kitType) + " Elo" + ChatColor.GRAY.toString() + ")";
            }

            String foundStart = ChatColor.YELLOW.toString() + "Match found against: " + ChatColor.RED;

            teamA.messageAlive(foundStart + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamB.getAllMembers())) + teamBElo);
            teamB.messageAlive(foundStart + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamA.getAllMembers())) + teamAElo);

            entries.remove(entryA);
            entries.remove(entryB);
        }
    }
}