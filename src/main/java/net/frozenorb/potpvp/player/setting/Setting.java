package net.frozenorb.potpvp.player.setting;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;
import java.util.List;

public enum Setting {

    SHOW_SCOREBOARD(ChatColor.GOLD+ "Match Scoreboard", ImmutableList.of(ChatColor.GRAY + "Toggles side scoreboard in-match"), Material.ITEM_FRAME, ChatColor.GREEN + "Show match scoreboard", ChatColor.GOLD + "Hide match scoreboard", true, null),
    SHOW_SPECTATOR_JOIN_MESSAGES(ChatColor.GOLD+ "Spectator Join Messages", ImmutableList.of(ChatColor.GRAY + "Enable this to display messages as spectators join."), Material.BONE, ChatColor.GREEN + "Show spectator join messages", ChatColor.GOLD + "Hide spectator join messages", true, null),
    VIEW_OTHER_SPECTATORS(ChatColor.GOLD+ "Other Spectators", ImmutableList.of(ChatColor.GRAY + "If enabled, you can see spectators", ChatColor.GRAY + "in the same match as you.", "", ChatColor.GRAY + "Disable to only see alive players in match."), Material.GLASS_BOTTLE, ChatColor.GREEN + "Show other spectators", ChatColor.GOLD + "Hide other spectators", true, null),
    ALLOW_SPECTATORS(ChatColor.GOLD+ "Allow Spectators", ImmutableList.of(ChatColor.GRAY + "If enabled, players can spectate your", ChatColor.GRAY + "matches with /spectate.", "", ChatColor.GRAY + "Disable to disallow match spectators."), Material.REDSTONE_TORCH_ON, ChatColor.GREEN + "Let players spectate your matches", ChatColor.GOLD + "Don't let players spectate your matches", true, null),
    RECEIVE_DUELS(ChatColor.GOLD+ "Duel Invites", ImmutableList.of(ChatColor.GRAY + "If enabled, you will be able to receive", ChatColor.GRAY + "duels from other players or parties.", "", ChatColor.GRAY + "Disable to not receive, but still send duels."), Material.FIRE, ChatColor.GREEN + "Allow duel invites", ChatColor.GOLD + "Disallow duel invites", true, "potpvp.donator"),
    VIEW_OTHERS_LIGHTNING(ChatColor.GOLD+ "Death Lightning", ImmutableList.of(ChatColor.GRAY + "If enabled, lightning will be visible", ChatColor.GRAY + "when other players die.", "", ChatColor.GRAY + "Disable to hide others lightning."), Material.TORCH, ChatColor.GREEN + "Show other lightning", ChatColor.GOLD + "Hide other lightning", true, "potpvp.donator"),
    NIGHT_MODE(ChatColor.GOLD+ "Night Mode", ImmutableList.of(ChatColor.GRAY + "If enabled, your player time will be", ChatColor.GRAY + "changed to night time.", "", ChatColor.GRAY + "Disable to play in day time."), Material.GLOWSTONE, ChatColor.GOLD + "Time is set to night", ChatColor.GOLD + "Time is set to day", false, null),
    ENABLE_GLOBAL_CHAT(ChatColor.GOLD+ "Global Chat", ImmutableList.of(ChatColor.GRAY + "If enabled, you will see messages", ChatColor.GRAY + "sent in the global chat channel.", "", ChatColor.GRAY + "Disable to only see OP messages."), Material.BOOK_AND_QUILL, ChatColor.GREEN + "Global chat is shown", ChatColor.GOLD + "Global chat is hidden", true, null),
    SEE_TOURNAMENT_JOIN_MESSAGE(ChatColor.GOLD+ "Tournament Join Messages", ImmutableList.of(ChatColor.GRAY + "If enabled, you will see messages", ChatColor.GRAY + "when people join the tournament", "", ChatColor.GRAY + "Disable to only see your own party join messages."), Material.IRON_DOOR, ChatColor.GREEN + "Tournament join messages are shown", ChatColor.GOLD + "Tournament join messages are hidden", true, null),
    SEE_TOURNAMENT_ELIMINATION_MESSAGES(ChatColor.GOLD+ "Tournament Elimination Messages", ImmutableList.of(ChatColor.GRAY + "If enabled, you will see messages when", ChatColor.GRAY + "people are eliminated the tournament", "", ChatColor.GRAY + "Disable to only see your own party elimination messages."), Material.SKULL_ITEM, ChatColor.GREEN + "Tournament elimination messages are shown", ChatColor.GOLD + "Tournament elimination messages are hidden", true, null);

    public String name;
    public List<String> description;
    public Material icon;
    public String enabledText;
    public String disabledText;
    public boolean defaultValue;
    public String permission;

    public boolean getDefaultValue() {
        return this.defaultValue;
    }

    public boolean canUpdate(Player player) {
        return this.permission == null || player.hasPermission(this.permission);
    }

    @ConstructorProperties({ "name", "description", "icon", "enabledText", "disabledText", "defaultValue", "permission" })
    Setting(String name, List<String> description, Material icon, String enabledText, String disabledText, boolean defaultValue, String permission) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.enabledText = enabledText;
        this.disabledText = disabledText;
        this.defaultValue = defaultValue;
        this.permission = permission;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public Material getIcon() {
        return this.icon;
    }

    public String getEnabledText() {
        return this.enabledText;
    }

    public String getDisabledText() {
        return this.disabledText;
    }
}
