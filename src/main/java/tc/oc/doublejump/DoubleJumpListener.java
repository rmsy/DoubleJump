package tc.oc.doublejump;

import javax.annotation.Nonnull;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class DoubleJumpListener implements Listener {
    public DoubleJumpListener() {
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void onGroundStateChanged(final PlayerOnGroundEvent event) {
        if(!event.getPlayer().hasPermission("doublejump.use")) return;
        this.refreshJump(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleFlight(final PlayerToggleFlightEvent event) {
        if(!event.getPlayer().hasPermission("doublejump.use")) return;
        Player player = event.getPlayer();
        if(event.isFlying()) {
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
        player.setAllowFlight(true);
        player.setExp(1.0f);
    }
}
