package tc.oc.doublejump;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class VectorUtil {
    public static @Nonnull Vector calculateLookVector(@Nonnull Location location) {
        double pitch = Math.toRadians(location.getPitch());
        double yaw = Math.toRadians(location.getYaw());

        Vector normal = new Vector(
                -(Math.cos(pitch) * Math.sin(yaw)),
                -Math.sin(pitch),
                Math.cos(pitch) * Math.cos(yaw)
                );

        return normal;
    }
}
