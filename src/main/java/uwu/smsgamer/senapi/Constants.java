package uwu.smsgamer.senapi;

import java.util.Arrays;

public class Constants {
    private static final char[] COLORS_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};
    private static final String COLORS_STRING = "1234567890abcdefklmnor";

    /**
     * Returns an array of characters representing every possible color format for minecraft.
     *
     * @return an array of characters representing every possible color format for minecraft.
     */
    public static char[] getCharColors() {
        return Arrays.copyOf(COLORS_CHARS, COLORS_CHARS.length);
    }


    /**
     * Returns a string characters representing every possible color format for minecraft.
     *
     * @return a string characters representing every possible color format for minecraft.
     */
    public static String getStringColors() {
        return COLORS_STRING;
    }
}
