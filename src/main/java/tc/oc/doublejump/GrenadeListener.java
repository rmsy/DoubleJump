package tc.oc.doublejump;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class GrenadeListener implements Listener {
    public GrenadeListener(@Nonnull DoubleJumpPlugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin");

        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskTimer(plugin, new GrenadeDestructionRunner(this.grenades), 1, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack held = event.getItem();
            if(held != null && held.getType() == Material.SLIME_BALL) {
                Vector normal = VectorUtil.calculateLookVector(event.getPlayer().getLocation());
                normal.multiply(3);

                Location spawnLocation = event.getPlayer().getLocation().clone();
                spawnLocation.setY(spawnLocation.getY() + event.getPlayer().getEyeHeight());
                spawnLocation.add(normal);

                ItemStack itemStack = new ItemStack(Material.SLIME_BALL, 1);
                Item item = event.getPlayer().getWorld().dropItem(spawnLocation, itemStack);
                item.setVelocity(normal);

                int newAmount = held.getAmount() - 1;
                if(newAmount > 0) {
                    held.setAmount(newAmount);
                } else {
                    event.getPlayer().setItemInHand(null);
                }

                this.grenades.add(item);
            }
        }
    }

    private final @Nonnull DoubleJumpPlugin plugin;
    private final @Nonnull Set<Item> grenades = Sets.newHashSet();

    public static class GrenadeDestructionRunner implements Runnable {
        public GrenadeDestructionRunner(@Nonnull Set<Item> grenades) {
            this.grenades = grenades;
        }

        public void run() {
            for(Iterator<Item> it = this.grenades.iterator(); it.hasNext(); ) {
                Item item = it.next();

                Location location = item.getLocation();

                Block block = location.getBlock();
                Location min = block.getLocation();
                Location max = block.getLocation().clone().add(new Vector(1, 1, 1));

                for(BlockFace face : BlockFace.values()) {
                    if(block.getRelative(face).getType() == Material.AIR) continue;
                    Vector rel = new Vector(face.getModX(), face.getModY(), face.getModZ()).multiply(0.8);
                    if(location.toVector().isInAABB(min.clone().add(rel).toVector(), max.clone().add(rel).toVector())) {
                        createExplosive(location);
                        it.remove();
                        item.remove();
                        break;
                    }
                }

                if(location.getY() < 0 || item.getVelocity().lengthSquared() < 1) {
                    it.remove();
                    item.remove();
                    break;
                }
            }
        }

        public static void createExplosive(@Nonnull Location location) {
            location.getWorld().createExplosion(location, 3);
        }

        private final @Nonnull Set<Item> grenades;
    }
}
