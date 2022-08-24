package net.frozenorb.potpvp.player.duel.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.kittype.menu.select.SelectKitTypeMenu;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.player.duel.DuelHandler;
import net.frozenorb.potpvp.player.duel.DuelInvite;
import net.frozenorb.potpvp.player.duel.PartyDuelInvite;
import net.frozenorb.potpvp.player.duel.PlayerDuelInvite;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.ItemBuilder;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DuelCommand {


    @Command(names={"duel", "1v1"})
    public static void duel(Player sender, @Param(name="player") Player target) {

        ConfigFile messages = PotPvPSI.getInstance().getMessagesConfig();

        if (sender == target) {
            sender.sendMessage(CC.translate(messages.getString("DUEL.YOURSELF")));
            return;
        }
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();
        Party senderParty = partyHandler.getParty(sender);
        Party targetParty = partyHandler.getParty(target);
        if (senderParty != null && targetParty != null) {
            if (!PotPvPValidation.canSendDuel(senderParty, targetParty, sender)) {
                return;
            }
            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();
                Party newSenderParty = partyHandler.getParty(sender);
                Party newTargetParty = partyHandler.getParty(target);
                if (newSenderParty != null && newTargetParty != null) {
                    if (newSenderParty.isLeader(sender.getUniqueId())) {
                        DuelCommand.duel(sender, newSenderParty, newTargetParty, kitType);
                    } else {
                        sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-LEADER-PARTY")));
                    }
                }
            }, "Select a kit type...").openMenu(sender);
        } else if (senderParty == null && targetParty == null) {
            if (!PotPvPValidation.canSendDuel(sender, target)) {
                return;
            }
            if (target.hasPermission("potpvp.famous") && System.currentTimeMillis() - lobbyHandler.getLastLobbyTime(target) < 3000L) {
                sender.sendMessage(ChatColor.RED + target.getName() + " just returned to the lobby, please wait a moment.");
                return;
            }
            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();
                DuelCommand.duel(sender, target, kitType);
            }, "Select a kit type...").openMenu(sender);
        }
        else if (senderParty == null) {
            sender.sendMessage(CC.translate(messages.getString("DUEL.NEED-PARTY").replaceAll("%player%", target.getName())));
        }
        else {
            sender.sendMessage(CC.translate(messages.getString("DUEL.NEED-LEAVE-PARTY").replaceAll("%player%", target.getName())));
        }
    }

    public static void duel(Player sender, Player target, KitType kitType) {

        ConfigFile messages = PotPvPSI.getInstance().getMessagesConfig();

        if (!PotPvPValidation.canSendDuel(sender, target)) {
            return;
        }
        Menu menu = new Menu() {

            @Override
            public String getTitle(Player player) {
                return CC.translate("&7Choose a map...");
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                FileConfiguration config = PotPvPSI.getInstance().getConfig();
                Map<Integer, Button> buttons = new HashMap<>();

                int i = 0;
                for ( ArenaSchematic schematic : PotPvPSI.getInstance().getArenaHandler().getSchematics()) {
                    if (!schematic.isEnabled())
                        continue;
                    if(!MatchHandler.canUseSchematic(kitType, schematic)) continue;
                    buttons.put(i, new Button() {

                        @Override
                        public ItemStack getButtonItem(Player player) {
                            return new ItemBuilder(Material.valueOf(config.getString("MAP-SELECTOR.ITEM")))
                                    .name(config.getString("MAP-SELECTOR.NAME").replace("%schematic-name%", schematic.getName()))
                                    .lore(config.getStringList("MAP-SELECTOR.LORE"))
                                    .data(config.getInt("MAP-SELECTOR.DATA"))
                                    .build();
                        }

                        @Override
                        public String getName(Player var1) {
                            return null;
                        }

                        @Override
                        public List<String> getDescription(Player var1) {
                            return null;
                        }

                        @Override
                        public Material getMaterial(Player var1) {
                            return null;
                        }

                        @Override
                        public void clicked(Player player, int slot, ClickType clickType) {
                            DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
                            DuelInvite autoAcceptInvite = duelHandler.findInvite(target, sender);

                            // if two players duel each other for the same thing automatically
                            // accept it to make their life a bit easier.
                            if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
                                AcceptCommand.accept(sender, target);
                                return;
                            }

                            DuelInvite alreadySentInvite = duelHandler.findInvite(sender, target);

                            if (alreadySentInvite != null) {
                                if (alreadySentInvite.getKitType() == kitType) {
                                    sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.BLUE + target.getName() + ChatColor.YELLOW + " to a " + kitType.getColoredDisplayName() + ChatColor.WHITE + " duel.");
                                    return;
                                } else {
                                    // if an invite was already sent (with a different kit type)
                                    // just delete it (so /accept will accept the 'latest' invite)
                                    duelHandler.removeInvite(alreadySentInvite);
                                }
                            }

                            target.sendMessage(CC.translate(messages.getString("DUEL.SEND-DUEL-PLAYER").replaceAll("%player%", sender.getName())).replaceAll("%kit-display-name%", kitType.getColoredDisplayName()));
                            target.spigot().sendMessage((createInviteNotification(sender.getName())));
                            sender.sendMessage(CC.translate(messages.getString("DUEL.SEND-DUEL-SUCCESSFULLY").replaceAll("%kit-display-name%", kitType.getColoredDisplayName())).replaceAll("%player%", target.getName()));
                            duelHandler.insertInvite(new PlayerDuelInvite(sender, target, kitType, schematic));
                            player.closeInventory();
                        }
                    });
                    ++i;
                }

                return buttons;
            }
        };
        sender.closeInventory();
        menu.openMenu(sender);
    }

    public static void duel(Player sender, Party senderParty, Party targetParty, KitType kitType) {

        FileConfiguration config = PotPvPSI.getInstance().getConfig();

        if (!PotPvPValidation.canSendDuel(senderParty, targetParty, sender)) {
            return;
        }

        Menu menu = new Menu() {

            @Override
            public String getTitle(Player player) {
                return config.getString("TITLES.MAP-SELECTOR");
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                Map<Integer, Button> buttons = new HashMap<>();

                int i = 0;
                for ( ArenaSchematic schematic : PotPvPSI.getInstance().getArenaHandler().getSchematics()) {
                    if (!schematic.isEnabled())
                        continue;
                    if(!MatchHandler.canUseSchematic(kitType, schematic)) continue;
                    buttons.put(i, new Button() {

                        @Override
                        public ItemStack getButtonItem(Player player) {
                            return new ItemBuilder(Material.valueOf(config.getString("MAP-SELECTOR.ITEM")))
                                    .name(config.getString("MAP-SELECTOR.NAME").replace("%schematic-name%", schematic.getName()))
                                    .lore(config.getStringList("MAP-SELECTOR.LORE"))
                                    .data(config.getInt("MAP-SELECTOR.DATA"))
                                    .build();
                        }

                        @Override
                        public String getName(Player var1) {
                            return null;
                        }

                        @Override
                        public List<String> getDescription(Player var1) {
                            return null;
                        }

                        @Override
                        public Material getMaterial(Player var1) {
                            return null;
                        }

                        @Override
                        public void clicked(Player player, int slot, ClickType clickType) {
                            DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
                            PartyDuelInvite autoAcceptInvite=duelHandler.findInvite(targetParty, senderParty);
                            String targetPartyLeader=PotPvPSI.getInstance().getUuidCache().name(targetParty.getLeader());

                            // if two players duel each other for the same thing automatically
                            // accept it to make their life a bit easier.
                            if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
                                AcceptCommand.accept(sender, Bukkit.getPlayer(targetParty.getLeader()));
                                return;
                            }

                            PartyDuelInvite alreadySentInvite=duelHandler.findInvite(senderParty, targetParty);

                            if (alreadySentInvite != null) {
                                if (alreadySentInvite.getKitType() == kitType) {
                                    sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.AQUA + targetPartyLeader + "'s party" + ChatColor.YELLOW + " to a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
                                    return;
                                }
                                duelHandler.removeInvite(alreadySentInvite);
                            }

                            targetParty.message(ChatColor.AQUA + sender.getName() + "'s Party (" + senderParty.getMembers().size() + ")" + ChatColor.YELLOW + " has sent you a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel on the " +ChatColor.AQUA + schematic.getName()+ChatColor.YELLOW + " map.");
                            Bukkit.getPlayer(targetParty.getLeader()).spigot().sendMessage(DuelCommand.createInviteNotification(sender.getName()));

                            sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel invite to " + ChatColor.AQUA + targetPartyLeader + "'s party" + ChatColor.YELLOW + ".");
                            duelHandler.insertInvite(new PartyDuelInvite(senderParty, targetParty, kitType, schematic));
                            player.closeInventory();
                        }
                    });
                    ++i;
                }

                return buttons;
            }
        };
        sender.closeInventory();
        menu.openMenu(sender);
    }

    private static TextComponent[] createInviteNotification(String sender) {
        TextComponent firstPart=new TextComponent("(Accept)");
        firstPart.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        ClickEvent.Action runCommand=ClickEvent.Action.RUN_COMMAND;
        HoverEvent.Action showText=HoverEvent.Action.SHOW_TEXT;
        firstPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        firstPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[]{new TextComponent(ChatColor.GREEN + "Accept the duel")}));
        return new TextComponent[]{firstPart};
    }
}

