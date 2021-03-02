package uwu.smsgamer.senapi.utils.bungee;

import net.md_5.bungee.api.CommandSender;
import uwu.smsgamer.senapi.Constants;

/**
 * Utilities regarding players.
 */
public class BPlayerUtils {
    /**
     * Returns an array of characters representing every colour the player has access to.
     *
     * @param permissionBase The permission base to use when checking their permission.
     * @param permissible The {@link CommandSender} to check the permission of.
     * @return an array of characters representing every colour the player has access to.
     */
    public static char[] getAllowedColors(String permissionBase, CommandSender permissible) {
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
}
