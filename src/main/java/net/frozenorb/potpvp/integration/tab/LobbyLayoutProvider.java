package net.frozenorb.potpvp.integration.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.player.elo.EloHandler;
import net.frozenorb.potpvp.player.party.Party;
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

final class LobbyLayoutProvider implements BiConsumer<Player, TabElement> {

    @Override
    public void accept(Player player, TabElement element) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);
        EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();
        ConfigFile config = PotPvPSI.getInstance().getTablistlobbyConfig();
        List<String> list = Arrays.asList("LEFT", "MIDDLE", "RIGHT", "FAR-RIGHT");

        for (int i = 0; i < 4; ++i) {
            String s = list.get(i);
            for (int l = 0; l < 20; ++l) {
                String str = config.getString("TABLIST." + s + "." + (l + 1))
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

                //Kits start
                String kit = null;
                if(str.contains("<elo-")) {
                    kit = StringUtils.after(StringUtils.before(str, ">"), "<elo-");
                }
                if(kit != null) {
                    KitType kitType = KitType.byId(kit);
                    if(kitType == null) {
                        Bukkit.getConsoleSender().sendMessage(CC.translate("&7[&6Practice&7] &cThe kit" + kit + "&cnot exist"));
                        Bukkit.getConsoleSender().sendMessage(CC.translate("&7[&6Practice&7] &cUsage: /kittype create [kit]"));
                        continue;
                    }
                    String input = "<elo-" + kit + ">";
                    str = str.replace(input, String.valueOf(eloHandler.getElo(player, kitType)));
                }
                //Kits end

                //Party start
                if (str.contains("<party-player-")) {
                    String number = StringUtils.after(StringUtils.before(str, ">"), "<party-player-");
                    String input = "<party-player-" + number + ">";
                    if(party != null) {
                        List<UUID> members = getOrderedMembers(player, party);
                        if (Integer.parseInt(number) > members.size()) {
                            str = str.replace(input, "");
                        } else {
                            UUID member = members.get(Integer.parseInt(number) - 1);
                            String suffix = member == party.getLeader() ? ChatColor.GRAY + "*" : "";
                            String displayName = ChatColor.BLUE + PotPvPSI.getInstance().getUuidCache().name(member) + suffix;
                            str = str.replace(input, displayName);
                        }
                    } else {
                        str = str.replace(input, "");
                    }
                }
                //Party end

                element.add(i, l, str, 0, skinType.getSkinData());
            }
        }
    }

    // player first, leader next, then all other members
    private List<UUID> getOrderedMembers(Player viewer, Party party) {
        List<UUID> orderedMembers = new ArrayList<>();
        UUID leader = party.getLeader();

        orderedMembers.add(viewer.getUniqueId());

        // if they're the leader we don't display them twice
        if (viewer.getUniqueId() != leader) {
            orderedMembers.add(leader);
        }

        for (UUID member : party.getMembers()) {
            // don't display the leader or the viewer again
            if (member == leader || member == viewer.getUniqueId()) {
                continue;
            }

            orderedMembers.add(member);
        }

        return orderedMembers;
    }

}