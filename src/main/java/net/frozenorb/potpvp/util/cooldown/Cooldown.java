package net.frozenorb.potpvp.util.cooldown;

import lombok.Data;
import net.frozenorb.potpvp.util.cooldown.form.DurationFormatter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Cooldown {

    public Map<UUID, Long> cooldowns = new HashMap<>();

    public void applyCooldown(Player player, int seconds) {
        if (player.hasMetadata("abilitymaster")) {
            cooldowns.put(player.getUniqueId(), (System.currentTimeMillis() + seconds * 1000L) % 2);
        } else {
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + seconds * 1000L);
        }
    }
    public boolean onCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId()) && (cooldowns.get(player.getUniqueId()) >= System.currentTimeMillis());
    }
    public void removeCooldown(Player player) {

        cooldowns.remove(player.getUniqueId());
    }
    public String getRemaining(Player player) {
        if(!this.cooldowns.containsKey(player.getUniqueId())) return null;
        long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return DurationFormatter.getRemaining(l, true);
    }

}
