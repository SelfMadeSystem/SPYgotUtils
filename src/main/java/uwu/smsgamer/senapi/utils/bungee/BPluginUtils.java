package uwu.smsgamer.senapi.utils.bungee;

import net.md_5.bungee.api.ProxyServer;

/**
 * Utils for plugins.
 */
public class BPluginUtils {
    /**
     * Gets if a plugin is enabled.
     * @param name Plugin name.
     * @return If the plugin is enabled.
     */
    public static boolean isPluginEnabled(String name) {
        return ProxyServer.getInstance().getPluginManager().getPlugin(name) != null;
    }
}
