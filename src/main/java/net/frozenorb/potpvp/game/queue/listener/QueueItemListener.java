package net.frozenorb.potpvp.game.queue.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.kittype.menu.select.CustomSelectKitTypeMenu;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.queue.QueueHandler;
import net.frozenorb.potpvp.game.queue.QueueItems;
import net.frozenorb.potpvp.listener.RankedMatchQualificationListener;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.ItemListener;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

// This class followes a different organizational style from other item listeners
// because we need seperate listeners for ranked/unranked, we have methods which
// we call which generate a Consumer<Player> designed for either ranked/unranked,
// based on the argument passed. Returning Consumers makes this code slightly
// harder to follow, but saves us from a lot of duplication
public final class QueueItemListener extends ItemListener {

    public final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionRanked = selectionMenuAddition(true);
    public final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionUnranked = selectionMenuAddition(false);
    public final QueueHandler queueHandler;

    public QueueItemListener(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;

        addHandler(QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM, joinSoloConsumer(false));
        addHandler(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM, joinSoloConsumer(true));

        addHandler(QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));
        addHandler(QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));

        addHandler(QueueItems.JOIN_SOLO_PREMIUM_QUEUE_ITEM, joinSoloConsumer(false));

        Consumer<Player> leaveQueuePartyConsumer = player -> {
            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            // don't message, players who aren't leader shouldn't even get this item
            if (party != null && party.isLeader(player.getUniqueId())) {
                queueHandler.leaveQueue(party, false);
            }
        };
    }

    public Consumer<Player> joinSoloConsumer(boolean ranked) {
        return player -> {
            if (ranked) {
                if (!player.hasPermission("kore.vip")) {
                    if (!RankedMatchQualificationListener.isQualified(player.getUniqueId())) {
                        int needed = RankedMatchQualificationListener.getWinsNeededToQualify(player.getUniqueId());
                        player.sendMessage(ChatColor.RED + "You must have " + RankedMatchQualificationListener.MIN_MATCH_WINS + " unranked 1v1 wins to queue up for ranked matches. You need " + needed + " more!");
                        return;
                    }
                }
            }

            if (PotPvPValidation.canJoinQueue(player)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(player, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, ChatColor.BLUE + ChatColor.BOLD.toString() + "Join " + (ranked ? "Ranked" : "Unranked") + " Queue...", ranked).openMenu(player);
            }
        };
    }

    public Consumer<Player> joinPartyConsumer(boolean ranked) {
        return player -> {
            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            // just fail silently, players who aren't a leader
            // of a party shouldn't even have this item
            if (party == null || !party.isLeader(player.getUniqueId())) {
                return;
            }

            if (ranked) {
                for (UUID member : party.getMembers()) {
                    if (!RankedMatchQualificationListener.isQualified(member)) {
                        int needed = RankedMatchQualificationListener.getWinsNeededToQualify(member);
                        player.sendMessage(ChatColor.RED + "Your party can't join ranked queues because " + PotPvPSI.getInstance().getUuidCache().name(member) + " has less than " + RankedMatchQualificationListener.MIN_MATCH_WINS + " unranked 1v1 wins. They need " + needed + " more wins!");
                        return;
                    }
                }
            }

            // try to check validation issues in advance
            // (will be called again in QueueHandler#joinQueue)
            if (PotPvPValidation.canJoinQueue(party)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(party, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, "Play " + (ranked ? "Ranked" : "Unranked"), ranked).openMenu(player);
            }
        };
    }

    public Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionMenuAddition(boolean ranked) {
        return kitType -> {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

            int inFightsRanked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && m.isRanked());
            int inQueueRanked = queueHandler.countPlayersQueued(kitType, true);

            int inFightsUnranked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && !m.isRanked());
            int inQueueUnranked = queueHandler.countPlayersQueued(kitType, false);

            int inQueueDisplay = ranked ? inQueueRanked : inQueueUnranked;
            int inFightsDisplay = ranked ? inFightsRanked : inFightsUnranked;

            return new CustomSelectKitTypeMenu.CustomKitTypeMeta(
                    // clamp value to >= 1 && <= 64
                    Math.max(1, Math.min(64, ranked ? inQueueRanked + inFightsRanked : inQueueUnranked + inFightsUnranked)),
                    CC.translate(PotPvPSI.getInstance().getConfig().getStringList("SETTINGS.LORE-KIT-MENU")).stream()
                            .map(s -> s.replace("%unranked-fights%", String.valueOf(inFightsDisplay)))
                            .map(s -> s.replace("%unranked-queues%", String.valueOf(inQueueDisplay)))
                            .map(s -> s.replace("%ranked-fights%", String.valueOf(inFightsRanked)))
                            .map(s -> s.replace("%ranked-queues%", String.valueOf(inQueueRanked)))
                            .collect(Collectors.toList())
                    /*ImmutableList.of(
                            CC.translate("&7&m---------------"),
                            CC.translate("&6Unranked:"),
                            CC.translate("&7► &fFighting: &6" + inFightsDisplay),
                            CC.translate("&7► &fQueueing: &6" + inQueueDisplay),
                            CC.translate(""),
                            CC.translate("&6Ranked:"),
                            CC.translate("&7► &fFighting: &6" + inFightsRanked),
                            CC.translate("&7► &fQueueing: &6" + inQueueRanked),
                            CC.translate("&7&m---------------")
                    )*/
            );
        };
    }
}