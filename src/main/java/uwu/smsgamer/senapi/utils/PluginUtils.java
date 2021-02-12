package uwu.smsgamer.senapi.utils;

import org.bukkit.Bukkit;

/**
 * Utils for plugins.
 */
public class PluginUtils {
    /**
     * Gets if a plugin is enabled.
     * @param name Plugin name.
     * @return If the plugin is enabled.
     */
    public static boolean isPluginEnabled(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    /**
     * Returns if PlaceholderAPI is enabled.
     * @return If PlaceholderAPI is enabled.
     */
    public static boolean isPlaceholderAPIEnabled() {
        return isPluginEnabled("PlaceholderAPI");
    }
}
