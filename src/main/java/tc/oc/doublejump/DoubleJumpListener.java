package tc.oc.doublejump;

import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.google.common.collect.Maps;

public class DoubleJumpListener implements Listener {
    public DoubleJumpListener(final @Nonnull DoubleJumpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void onGroundStateChanged(final PlayerOnGroundEvent event) {
        if(!event.getPlayer().hasPermission("doublejump.use")) return;
        if(event.getOnGround()) {
            this.refreshJump(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleFlight(final PlayerToggleFlightEvent event) {
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

            BukkitTask task = Bukkit.getScheduler().runTaskLater(this.plugin, new RefreshJump(this, player), 10);
            this.refreshTasks.put(player, task);
        }
    }

    public void refreshJump(@Nonnull Player player) {
        player.setAllowFlight(true);
        player.setExp(1.0f);
        BukkitTask task = this.refreshTasks.remove(player);
        if(task != null) task.cancel();
    }

    private final @Nonnull DoubleJumpPlugin plugin;
    private final @Nonnull Map<Player, BukkitTask> refreshTasks = Maps.newHashMap();

    public static class RefreshJump implements Runnable {
        public RefreshJump(@Nonnull DoubleJumpListener parent, @Nonnull Player player) {
            this.parent = parent;
            this.player = player;
        }

        public void run() {
            if(!this.player.getAllowFlight()) {
                this.parent.refreshJump(this.player);
            }
        }

        private final @Nonnull DoubleJumpListener parent;
        private final @Nonnull Player player;
    }
}
