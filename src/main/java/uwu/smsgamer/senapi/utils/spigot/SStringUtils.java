package uwu.smsgamer.senapi.utils.spigot;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;

/**
 * Spigot string utils. Utilities for strings that require the Spigot API.
 */
public class SStringUtils {
    /**
     * If PlaceholderAPI is enabled, it uses PlaceholderAPI for placeholder replacement.
     * If not, it simply replaces {@code %player_name%} with the player's name.
     *
     * @param player The player for replacements.
     * @param string The string with the placeholders.
     * @return A string with placeholders replaced.
     */
    public static String replacePlaceholders(final OfflinePlayer player, final String string) {
        if (SPluginUtils.isPlaceholderAPIEnabled()) {
            if (player instanceof SConsolePlayer)
                Bukkit.getLogger().warning(
                  "If an error occurs, please do not report Sms_Gamer or a placeholder's author. " +
                    "The player parameter was a ConsolePlayer.");
            return PlaceholderAPI.setPlaceholders(player, string);
        } else return string.replace("%player_name%", player.getName());
    }
}
