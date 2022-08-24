package net.frozenorb.potpvp.game.match.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.nametag.PotPvPNametagProvider;
import net.frozenorb.potpvp.player.setting.Setting;
import net.frozenorb.potpvp.player.setting.SettingHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class MatchDeathMessageListener implements Listener {

    public static final String NO_KILLER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "%s&e died.");
    public static final String KILLED_BY_OTHER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "%s &7was killed by %s&7.");

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match == null) {
            return;
        }

        Player killed = event.getEntity();
        Player killer = killed.getKiller();
        PacketContainer lightningPacket = createLightningPacket(killed.getLocation());

        float thunderSoundPitch = 0.8F + ThreadLocalRandom.current().nextFloat() * 0.2F;
        float explodeSoundPitch = 0.5F + ThreadLocalRandom.current().nextFloat() * 0.2F;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            UUID onlinePlayerUuid = onlinePlayer.getUniqueId();

            // if this player has no relation to the match skip
            if (match.getTeam(onlinePlayerUuid) == null && !match.isSpectator(onlinePlayerUuid)) {
                continue;
            }

            String killedNameColor = PotPvPNametagProvider.getNameColor(killed, onlinePlayer);
            String killedFormattedName = killedNameColor + killed.getName();

            // if the killer died before the player did we just pretend they weren't
            // involved (their name would show up as a spectator, which would be confusing
            // for players)
            if (killer == null || match.isSpectator(killer.getUniqueId())) {
                onlinePlayer.sendMessage(String.format(NO_KILLER_MESSAGE, killedFormattedName));
            } else {
                String killerNameColor = PotPvPNametagProvider.getNameColor(killer, onlinePlayer);
                String killerFormattedName = killerNameColor + killer.getName();
                onlinePlayer.sendMessage(String.format(KILLED_BY_OTHER_MESSAGE, killedFormattedName, killerFormattedName));
/*                for (int i = 0; i < 6; ++i) {
                    PotPvPSI.getInstance().getKillEffectHandler().playEffect(killer, killed);
                }*/
            }
            if (settingHandler.getSetting(onlinePlayer, Setting.VIEW_OTHERS_LIGHTNING)) {
                onlinePlayer.playSound(killed.getLocation(), Sound.AMBIENCE_THUNDER, 10000F, thunderSoundPitch);
                onlinePlayer.playSound(killed.getLocation(), Sound.EXPLODE, 2.0F, explodeSoundPitch);
                sendLightningPacket(onlinePlayer, lightningPacket);
            }
        }
    }

    public PacketContainer createLightningPacket(Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128); // entity id of 128
        lightningPacket.getIntegers().write(4, 1); // type of lightning (1)
        lightningPacket.getIntegers().write(1, (int) (location.getX() * 32.0D)); // x
        lightningPacket.getIntegers().write(2, (int) (location.getY() * 32.0D)); // y
        lightningPacket.getIntegers().write(3, (int) (location.getZ() * 32.0D)); // z

        return lightningPacket;
    }

    public void sendLightningPacket(Player target, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
        } catch (InvocationTargetException ignored) {
            // will never happen, ProtocolWrapper (the lib this code was from)
            // ignores this exception as well
        }
    }

}