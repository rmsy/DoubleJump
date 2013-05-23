package tc.oc.doublejump;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class DoubleJumpListener implements Listener {
    private final DoubleJumpPlugin plugin;

    public DoubleJumpListener(DoubleJumpPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable() {
            public void run() {
                for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if(!player.hasPermission("doublejump.use")) continue;
                    if(player.hasPermission("doublejump.using")) continue;

                    if(player.getExp() < 1.0f) {
                        player.setExp(player.getExp() + 0.2f);
                    } else if(player.getExp() > 1.0f) {
                        player.setExp(1.0f);
                    }
                    DoubleJumpListener.this.refreshJump(player);
                }
            }
        }, 0, 10);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void noFallDamage(final EntityDamageEvent event) {
        if(event.getCause() != DamageCause.FALL) return;
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(!player.hasPermission("doublejump.nofalldamage")) return;

        event.setCancelled(true);
        player.addAttachment(this.plugin, "doublejump.nofalldamage", false);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void onGroundStateChanged(final PlayerOnGroundEvent event) {
        if(!event.getPlayer().hasPermission("doublejump.use")) return;

        if(event.getOnGround()) {
            if(event.getPlayer().hasPermission("doublejump.using") && event.getPlayer().getExp() == 0.0f) {
                event.getPlayer().addAttachment(this.plugin, "doublejump.nofalldamage", true);
                event.getPlayer().addAttachment(this.plugin, "doublejump.using", false);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleFlight(final PlayerToggleFlightEvent event) {
        if(!event.getPlayer().hasPermission("doublejump.use")) return;
        Player player = event.getPlayer();
        if(event.isFlying()) {
            event.getPlayer().addAttachment(this.plugin, "doublejump.using", true);
            event.getPlayer().setAllowFlight(false);
            player.setExp(0.0f);
            event.setCancelled(true);

            // calculate jump
            Vector normal = VectorUtil.calculateLookVector(player.getLocation());

            normal.setY(0.75 + Math.abs(normal.getY()) * 0.5);
            event.getPlayer().setVelocity(normal);

            player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_INFECT, 0.5f, 1.8f);
        }
    }

    public void refreshJump(@Nonnull Player player) {
        if(player.getExp() >= 1.0f) {
            player.setAllowFlight(true);
        }
    }
}
