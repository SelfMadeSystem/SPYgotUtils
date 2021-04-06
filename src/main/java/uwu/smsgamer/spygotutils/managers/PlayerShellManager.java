package uwu.smsgamer.spygotutils.managers;

import org.python.core.PyObject;
import uwu.smsgamer.spygotutils.utils.python.PyShell;

import java.util.*;

public class PlayerShellManager {
    public static final Map<UUID, PyShell> interpreters = new HashMap<>();

    public static void newInterpreter(UUID uuid) {
        interpreters.put(uuid, new PyShell());
    }

    public static void removeInterpreter(UUID uuid) {
        interpreters.remove(uuid);
    }

    public static PyObject eval(UUID uuid, String command) {
        return interpreters.get(uuid).eval(command);
    }
}
