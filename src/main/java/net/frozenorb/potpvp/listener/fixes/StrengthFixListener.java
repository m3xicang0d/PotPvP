package net.frozenorb.potpvp.listener.fixes;

/*import org.bukkit.entity.Player;
import org.bukkit.entity.potion.EffectAddReason;
import org.bukkit.entity.potion.PotionEffectAddEvent;
import org.bukkit.entity.potion.PotionEffectExtendEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class StrengthFixListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                PotionEffect strength = null;

                for (PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        strength = effect;
                    }
                }

                if (strength == null) return;

                if (strength.getAmplifier() == 0) {
                    if (event.getDamage() > 30D) {
                        event.setDamage(17D);
                        return;
                    }

                    if (event.getDamage() > 20D) {
                        event.setDamage(13D);
                    }
                }

                if (strength.getAmplifier() == 1) {
                    if (event.getDamage() > 40) {
                        event.setDamage(21D);
                        return;
                    }

                    if (event.getDamage() > 30) {
                        event.setDamage(19D);
                    }
                }
            }
        }
    }

    @EventHandler
    public void on(PotionEffectAddEvent event) {
        if (event.getReason() == EffectAddReason.BEACON && event.getEffect().getType() == PotionEffectType.INCREASE_DAMAGE && event.getEffect().getAmplifier() >= 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PotionEffectExtendEvent event) {
        if (event.getReason() == EffectAddReason.BEACON && event.getEffect().getType() == PotionEffectType.INCREASE_DAMAGE && event.getEffect().getAmplifier() >= 1) {
            event.setCancelled(true);
        }
    }
}*/
