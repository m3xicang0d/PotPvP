package net.frozenorb.potpvp.player.rematch.listener;

import net.frozenorb.potpvp.player.duel.command.AcceptCommand;
import net.frozenorb.potpvp.player.duel.command.DuelCommand;
import net.frozenorb.potpvp.player.rematch.RematchData;
import net.frozenorb.potpvp.player.rematch.RematchHandler;
import net.frozenorb.potpvp.player.rematch.RematchItems;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.bukkit.ItemListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class RematchItemListener
        extends ItemListener {
    public RematchItemListener(RematchHandler rematchHandler) {
        this.addHandler(RematchItems.REQUEST_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData((Player)player);
            if (rematchData != null) {
                Player target = Bukkit.getPlayer((UUID)rematchData.getTarget());
                DuelCommand.duel(player, target, rematchData.getKitType());
                InventoryUtils.resetInventoryDelayed(player);
                InventoryUtils.resetInventoryDelayed(target);
            }
        });
        this.addHandler(RematchItems.SENT_REMATCH_ITEM, p -> p.sendMessage((Object)ChatColor.RED + "You have already sent a rematch request."));
        this.addHandler(RematchItems.ACCEPT_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData((Player)player);
            if (rematchData != null) {
                Player target = Bukkit.getPlayer((UUID)rematchData.getTarget());
                AcceptCommand.accept(player, target);
            }
        });
    }
}