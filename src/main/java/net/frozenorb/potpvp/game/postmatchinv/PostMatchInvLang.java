package net.frozenorb.potpvp.game.postmatchinv;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public final class PostMatchInvLang {

    static final String LINE;
    static final String INVENTORY_HEADER;
    public static final String WINNER;
    public static final String LOSER;
    public static final String PARTICIPANTS;
    public static final TextComponent COMMA_COMPONENT;

    static {
        LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------";
        INVENTORY_HEADER = ChatColor.GOLD + "Post-Match Inventories " + ChatColor.GRAY + "(click name to view)";
        WINNER = ChatColor.GREEN + "Winner:" + ChatColor.GRAY;
        LOSER = ChatColor.RED + "Loser:" + ChatColor.GRAY;
        PARTICIPANTS = ChatColor.GREEN + "Participants:";
        COMMA_COMPONENT = new TextComponent(", ");
        COMMA_COMPONENT.setColor(ChatColor.YELLOW);
    }

    static Object[] gen1v1PlayerInvs(UUID winner, UUID loser) {
        return new Object[]{new TextComponent[]{new TextComponent(ChatColor.GREEN + "Winner: "), clickToViewLine(winner), new TextComponent(ChatColor.GRAY + " - " + ChatColor.RED + "Loser: "), clickToViewLine(loser)}};
    }

    static Object[] genSpectatorInvs(MatchTeam winner, MatchTeam loser) {
        return new Object[]{WINNER, clickToViewLine(winner.getAllMembers()), LOSER, clickToViewLine(loser.getAllMembers())};
    }

    static Object[] genTeamInvs(MatchTeam viewer, MatchTeam winner, MatchTeam loser) {
        return new Object[]{WINNER + (viewer == winner ? " (Your team)" : " (Enemy team)"), clickToViewLine(winner.getAllMembers()), LOSER + (viewer == loser ? " (Your team)" : " (Enemy team)"), clickToViewLine(loser.getAllMembers())};
    }

    static Object[] genGenericInvs(Collection<MatchTeam> teams) {
        Set<UUID> members = (Set)teams.stream().flatMap((t) -> {
            return t.getAllMembers().stream();
        }).collect(Collectors.toSet());
        return new Object[]{PARTICIPANTS, clickToViewLine(members)};
    }

    public static TextComponent clickToViewLine(UUID member) {
        String memberName = PotPvPSI.getInstance().getUuidCache().name(member);
        TextComponent component = new TextComponent();
        component.setText(memberName);
        component.setColor(ChatColor.YELLOW);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.GREEN + "Click to view inventory of " + ChatColor.GOLD + memberName)).create()));
        component.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/_ " + memberName));
        return component;
    }

    public static TextComponent[] clickToViewLine(Set<UUID> members) {
        List<TextComponent> components = new ArrayList();
        Iterator var2 = members.iterator();

        while(var2.hasNext()) {
            UUID member = (UUID)var2.next();
            components.add(clickToViewLine(member));
            components.add(COMMA_COMPONENT);
        }

        components.remove(components.size() - 1);
        return (TextComponent[])components.toArray(new TextComponent[components.size()]);
    }


}