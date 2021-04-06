package uwu.smsgamer.spygotutils.managers;

import org.python.core.PyObject;
import uwu.smsgamer.spygotutils.utils.python.PyInterpreter;

import java.util.*;

public class PlayerShellManager {
    public static final Map<UUID, PyInterpreter> interpreters = new HashMap<>();

    public static void newInterpreter(UUID uuid) {
        interpreters.put(uuid, (PyInterpreter) new PyInterpreter().setFuns(PythonManager.defaultFuns).setVars(PythonManager.defaultVars));
    }

    public static void removeInterpreter(UUID uuid) {
        interpreters.remove(uuid);
    }

    public static PyObject eval(UUID uuid, String command) {
        return getInterpreter(uuid).eval(command);
    }

    public static void save(UUID uuid, String fileName) {
    }

    public static String getLines(UUID uuid) {
        return getInterpreter(uuid).getLines();
    }

    public static PyInterpreter getInterpreter(UUID uuid) {
        return interpreters.get(uuid);
    }
}
