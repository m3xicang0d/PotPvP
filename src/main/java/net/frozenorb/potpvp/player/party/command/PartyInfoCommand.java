package net.frozenorb.potpvp.player.party.command;

import com.google.common.base.Joiner;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import net.frozenorb.potpvp.util.potpvp.PotPvPLang;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public final class PartyInfoCommand {

//    CODE CHANGED BY: JESUSMX

    @Command(names = {"party info", "p info", "t info", "team info", "f info", "p i", "t i", "f i", "party i", "team i"}, permission = "")
    public static void partyInfo(Player sender, @Param(name = "player",defaultValue = "self") Player target) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(target);
        if (party == null) {
            if (sender == target) {
                sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-IN-PARTY")));
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " isn't in a party.");
            }

        } else {
            String leaderName = PotPvPSI.getInstance().getUuidCache().name(party.getLeader());
            int memberCount = party.getMembers().size();
            String members = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(party.getMembers()));
            sender.sendMessage(ChatColor.GRAY + PotPvPLang.LONG_LINE);
            sender.sendMessage(ChatColor.YELLOW + "Leader: " + ChatColor.GOLD + leaderName);
            sender.sendMessage(ChatColor.YELLOW + "Members " + ChatColor.GOLD + "(" + memberCount + ")" + ChatColor.YELLOW + ": " + ChatColor.GRAY + members);
            switch (party.getAccessRestriction()) {
                case PUBLIC:
                    sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.GREEN + "Open");
                    break;
                case INVITE_ONLY:
                    sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.GOLD + "Invite-Only");
                    break;
                case PASSWORD:
                    if (party.isLeader(sender.getUniqueId())) {
                        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;
                        BaseComponent[] passwordComponent = new BaseComponent[]{new TextComponent(party.getPassword())};
                        ComponentBuilder builder = (new ComponentBuilder("Privacy: ")).color(ChatColor.YELLOW);
                        builder.append("Password Protected ").color(ChatColor.RED);
                        builder.append("[Hover for password]").color(ChatColor.GRAY);
                        builder.event(new HoverEvent(showText, passwordComponent));
                        sender.spigot().sendMessage(builder.create());
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.RED + "Password Protected");
                    }
            }

            sender.sendMessage(ChatColor.GRAY + PotPvPLang.LONG_LINE);
        }
    }
}


//    @Command(names = {"party info", "p info", "t info", "team info", "f info", "p i", "t i", "f i", "party i", "team i"}, permission = "")
//    public static void partyInfo(Player sender, @Param(name = "player",defaultValue = "self") Player target) {
//        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(target);
//        if (party == null) {
//            if (sender == target) {
//                sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-IN-PARTY")));
//            } else {
//                sender.sendMessage(ChatColor.RED + target.getName() + " isn't in a party.");
//            }
//
//        } else {
//            String leaderName = PotPvPSI.getInstance().getUuidCache().name(party.getLeader());
//            int memberCount = party.getMembers().size();
//            String members = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(party.getMembers()));
//            sender.sendMessage(ChatColor.GRAY + PotPvPLang.LONG_LINE);
//            sender.sendMessage(ChatColor.YELLOW + "Leader: " + ChatColor.GOLD + leaderName);
//            sender.sendMessage(ChatColor.YELLOW + "Members " + ChatColor.GOLD + "(" + memberCount + ")" + ChatColor.YELLOW + ": " + ChatColor.GRAY + members);
//
//        //sender.sendMessage(ChatColor.GRAY + PotPvPLang.LONG_LINE);
//        String top = "&7&m--------&7[&c " + leaderName + "&e's Party &7]&m--------";
//        sender.sendMessage(CC.translate(top));
//        sender.sendMessage(CC.translate("&cMembers: &7(&e" + memberCount + "&7)"));
//        sender.sendMessage(CC.translate("&7" + StringEscapeUtils.unescapeJava(" » ") + "&a" + members));
//
//        switch (party.getAccessRestriction()) {
//            case PUBLIC:
//                sender.sendMessage(CC.translate("&cStatus: &7(&aOpen&7)"));
//                break;
//            case INVITE_ONLY:
//                sender.sendMessage(CC.translate("&cStatus: &7(&eInvite-Only&7)"));
//                break;
//            case PASSWORD:
//                // leader can see password by hovering
//                if (party.isLeader(sender.getUniqueId())) {
//                    HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;
//                    BaseComponent[] passwordComponent = {new TextComponent(party.getPassword())};
//
//                    // Privacy: Password Protected [Hover for password]
//                    ComponentBuilder builder = new ComponentBuilder("Privacy: ").color(ChatColor.YELLOW);
//                    builder.append("Password Protected ").color(ChatColor.RED);
//                    builder.append("[Hover for password]").color(ChatColor.GRAY);
//                    builder.event(new HoverEvent(showText, passwordComponent));
//
//                    sender.spigot().sendMessage(builder.create());
//                } else {
//                    sender.sendMessage(CC.translate("&cStatus: &7(&ePassword-Protected&7)"));
//                }
//
//                break;
//            default:
//                break;
//        }
//
//        int length = top.length() - 10;
//        String down = "&7&m" + StringUtils.repeat("-", length);
//        if (down.length() > length) {
//            down = down.substring(0, length);
//        }
//        sender.sendMessage(CC.translate(down));
//
//        //sender.sendMessage(ChatColor.GRAY + PotPvPLang.LONG_LINE);
//    }
//}