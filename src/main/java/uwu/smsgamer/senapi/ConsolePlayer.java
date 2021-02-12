package uwu.smsgamer.senapi;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * An {@link OfflinePlayer} implementation that represents the console.
 */
public class ConsolePlayer implements OfflinePlayer {
    private static final String NAME = "CONSOLE";
    private static final UUID UUID = new UUID(0L, 0L);

    private static ConsolePlayer INSTANCE;

    public static ConsolePlayer getInstance() {
        if (INSTANCE == null) INSTANCE = new ConsolePlayer();
        return INSTANCE;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public UUID getUniqueId() {
        return UUID;
    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public boolean isWhitelisted() {
        return true;
    }

    @Override
    public void setWhitelisted(boolean value) {
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public long getFirstPlayed() {
        return 0;
    }

    @Override
    public long getLastPlayed() {
        return 0;
    }

    @Override
    public boolean hasPlayedBefore() {
        return true;
    }

    @Override
    public Location getBedSpawnLocation() {
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("UUID", getUniqueId().toString());
        return result;
    }

    public static OfflinePlayer deserialize(Map<String, Object> args) {
        return getInstance();
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
    }
}
