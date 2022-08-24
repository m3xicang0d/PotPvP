package net.frozenorb.potpvp.player.party;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public final class PartyLang {

    public static final TextComponent INVITE_PREFIX = new TextComponent("Invite > ");

    public static final TextComponent INVITED_YOU_TO_JOIN = new TextComponent(" has invited you to a party. ");

    public static final TextComponent ACCEPT_BUTTON = new TextComponent("[Accept]");
    public static final TextComponent INFO_BUTTON = new TextComponent("[Info]");

    static {
        INVITED_YOU_TO_JOIN.setColor(ChatColor.YELLOW);

        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT; // readability
        BaseComponent[] acceptTooltip = new ComponentBuilder("Click to join party").color(ChatColor.GREEN).create();

        ACCEPT_BUTTON.setColor(ChatColor.GREEN);
        ACCEPT_BUTTON.setHoverEvent(new HoverEvent(showText, acceptTooltip));

        INFO_BUTTON.setColor(ChatColor.AQUA);
    }

    public static TextComponent inviteAcceptPrompt(Party party) {
        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        String partyLeader = PotPvPSI.getInstance().getUuidCache().name(party.getLeader());

        // create copies via constructor (we're going to update their click event)
        TextComponent acceptButton = new TextComponent(ACCEPT_BUTTON);
        TextComponent infoButton = new TextComponent(INFO_BUTTON);

        acceptButton.setClickEvent(new ClickEvent(runCommand, "/p join " + partyLeader));

        infoButton.setHoverEvent(hoverablePreviewTooltip(party));
        infoButton.setClickEvent(new ClickEvent(runCommand, "/p info " + partyLeader));

        TextComponent builder = new TextComponent("");

        builder.addExtra(hoverablePartyName(party));
        builder.addExtra(INVITED_YOU_TO_JOIN);
        builder.addExtra(acceptButton);
        builder.addExtra(new TextComponent(" "));
        builder.addExtra(infoButton);

        return builder;
    }

    public static TextComponent hoverablePartyName(Party party) {
        TextComponent previewComponent = new TextComponent();
        String leaderName = PotPvPSI.getInstance().getUuidCache().name(party.getLeader());

        // only show an actual tooltip for parties with >= 2 members,
        // parties that (to the user) don't exist yet just show up as a name
        if (party.getMembers().size() > 1) {
            HoverEvent hoverEvent = hoverablePreviewTooltip(party);

            previewComponent.setText("[" + leaderName + "'s Party]");
            previewComponent.setHoverEvent(hoverEvent);
        } else {
            previewComponent.setText(leaderName);
        }

        previewComponent.setColor(ChatColor.BLUE);
        return previewComponent;
    }

    public static HoverEvent hoverablePreviewTooltip(Party party) {
        ComponentBuilder builder = new ComponentBuilder("Members (").color(ChatColor.BLUE);
        String size = "" + party.getMembers().size();

        builder.append(size).color(ChatColor.GOLD);
        builder.append("):").color(ChatColor.BLUE);

        for (String member : getMemberPreviewNames(party)) {
            builder.append("\n");
            builder.append(member);
        }

        HoverEvent.Action action = HoverEvent.Action.SHOW_TEXT;
        return new HoverEvent(action, builder.create());
    }

    // this method is probably named badly;
    // it puts min(partySize, 6) member display names into a set,
    // with a String indicating how many more members are present (if there are any)
    public static List<String> getMemberPreviewNames(Party party) {
        List<UUID> members = new ArrayList<>(party.getMembers());
        int partySize = members.size();
        List<String> displayNames = new ArrayList<>();

        for (int i = 0; i < Math.min(partySize, 6); i++) {
            UUID member = members.remove(0);
            String suffix = party.isLeader(member) ? "*" : "";

            displayNames.add(ChatColor.YELLOW + PotPvPSI.getInstance().getUuidCache().name(member) + suffix);
        }

        if (!members.isEmpty()) {
            displayNames.add(ChatColor.GRAY + "+ " + members.size() + " more");
        }

        return displayNames;
    }

}