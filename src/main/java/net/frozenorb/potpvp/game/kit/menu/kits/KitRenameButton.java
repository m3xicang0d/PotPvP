package net.frozenorb.potpvp.game.kit.menu.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kit.Kit;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

import java.util.List;

class KitRenameButton
extends Button {
    public Kit kit;

    KitRenameButton(Kit kit) {
        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.AQUA.toString() + ChatColor.BOLD + "Rename";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of("", ChatColor.AQUA + "Click to rename this kit.");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SIGN;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        ConversationFactory factory = new ConversationFactory((Plugin)PotPvPSI.getInstance()).withFirstPrompt((Prompt)new StringPrompt(){

            public String getPromptText(ConversationContext context) {
                return ChatColor.YELLOW + "Renaming " + ChatColor.BOLD + KitRenameButton.this.kit.getName() + ChatColor.YELLOW + "... " + ChatColor.GREEN + "Enter the new name now.";
            }

            public Prompt acceptInput(ConversationContext ctx, String s) {
                if (s.length() > 20) {
                    ctx.getForWhom().sendRawMessage(ChatColor.RED + "Kit names can't have more than 20 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }
                KitRenameButton.this.kit.setName(s);
                PotPvPSI.getInstance().getKitHandler().saveKitsAsync(player);
                ctx.getForWhom().sendRawMessage(ChatColor.YELLOW + "Kit renamed.");
                if (!PotPvPSI.getInstance().getMatchHandler().isPlayingMatch(player)) {
                    new KitsMenu(KitRenameButton.this.kit.getType()).openMenu(player);
                }
                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false);
        player.closeInventory();
        player.beginConversation(factory.buildConversation((Conversable)player));
    }
}

