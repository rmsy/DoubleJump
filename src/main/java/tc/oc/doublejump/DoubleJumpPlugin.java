package tc.oc.doublejump;

import org.bukkit.plugin.java.JavaPlugin;

public final class DoubleJumpPlugin extends JavaPlugin {
    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new DoubleJumpListener(this), this);
    }
}
