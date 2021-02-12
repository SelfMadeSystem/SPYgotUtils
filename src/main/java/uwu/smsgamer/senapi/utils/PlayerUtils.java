package uwu.smsgamer.senapi.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import uwu.smsgamer.senapi.*;

/**
 * Utilities regarding players.
 */
public class PlayerUtils {
    /**
     * Returns an array of characters representing every colour the player has access to.
     *
     * @param permissionBase The permission base to use when checking their permission.
     * @param permissible The {@link Permissible} to check the permission of.
     * @return an array of characters representing every colour the player has access to.
     */
    public static char[] getAllowedColors(String permissionBase, Permissible permissible) {
        char[] allowedColors = new char[0];
        for (char c : Constants.getCharColors())
            if (permissible.hasPermission(permissionBase + c)) {
                char[] chars = new char[allowedColors.length + 1];
                System.arraycopy(allowedColors, 0, chars, 0, allowedColors.length);
                chars[chars.length - 1] = c;
                allowedColors = chars;
            }
        return allowedColors;
    }

    /**
     * Returns the sender cast to an offline player if it's an offline players.
     * If it isn't, then it returns a {@link ConsolePlayer} instance.
     *
     * @param sender The sender to check for.
     * @return the sender cast to an offline player if it's an offline players.
     * If it isn't, then it returns a {@link ConsolePlayer} instance.
     */
    public static OfflinePlayer getPlayer(CommandSender sender) {
        if (sender instanceof OfflinePlayer) {
            return (OfflinePlayer) sender;
        } else return ConsolePlayer.getInstance();
    }
}
