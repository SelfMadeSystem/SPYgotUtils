package uwu.smsgamer.spygotutils.managers;

import org.bukkit.event.*;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import uwu.smsgamer.spygotutils.SPYgotUtils;
import uwu.smsgamer.spygotutils.utils.python.*;

import java.io.File;
import java.util.*;

public class PythonManager {
    public static PyListener lowestListener;
    public static PyListener lowListener;
    public static PyListener listener;
    public static PyListener highListener;
    public static PyListener highestListener;
    public static PyListener monitorListener;
    public static PyFunction[] defaultFuns;
    public static PyObject packetListener;
    private static File[] files;

    public static void init() {
        lowestListener = new PyListener(EventPriority.LOWEST, SPYgotUtils.getInstance().plugin);
        lowListener = new PyListener(EventPriority.LOW, SPYgotUtils.getInstance().plugin);
        listener = new PyListener(EventPriority.NORMAL, SPYgotUtils.getInstance().plugin);
        highListener = new PyListener(EventPriority.HIGH, SPYgotUtils.getInstance().plugin);
        highestListener = new PyListener(EventPriority.HIGHEST, SPYgotUtils.getInstance().plugin);
        monitorListener = new PyListener(EventPriority.MONITOR, SPYgotUtils.getInstance().plugin);

        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("def register_event(event_type, priority, function):\n" +
          "    from uwu.smsgamer.spygotutils.managers import PythonManager\n" +
          "    PythonManager.registerEvent(event_type, priority, function)" +
          "\n" +
          "\n" +
          "def Command(name, description=\"\", usage_msg=None, aliases=None):\n" +
          "    if usage_msg is None:\n" +
          "        usage_msg = \"/\" + name\n" +
          "    if aliases is None:\n" +
          "        aliases = []\n" +
          "    from uwu.smsgamer.spygotutils.utils.python import PyCommand\n" +
          "    return PyCommand(name, description, usage_msg, aliases)\n");

        defaultFuns = new PyFunction[]{(PyFunction) interpreter.get("register_event"),
          (PyFunction) interpreter.get("Command")};

        packetListener = Py.java2py(PycketListener.getInstance());
    }

    public static void loadScripts() {
        File dir = new File(SPYgotUtils.getInstance().plugin.getDataFolder(), "scripts");
        if (dir.exists()) {
            File[] arr = dir.listFiles();
            if (files == null) {
                for (File file : arr) newScript(file);
                files = arr;
            } else {
                // Who cares about efficiency. It's during load or reload.
                // *Don't* want to reload a script; only load new ones.
                List<File> exclude = Arrays.asList(files);
                for (File file : arr) if (!exclude.contains(file)) newScript(file);
                HashSet<File> set = new HashSet<>(Arrays.asList(arr));
                set.addAll(Arrays.asList(files));
                files = set.toArray(new File[0]);
            }
        }
    }

    public static void newScript(File file) {
        PyScript script = new PyScript(file).setFuns(defaultFuns).set("packet_listener", packetListener);
        try {
            script.execFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerEvent(Class<? extends Event> type, EventPriority priority, PyFunction function) {
        switch (priority) {
            case LOWEST:
                lowestListener.registerFunction(type, function);
                break;
            case LOW:
                lowListener.registerFunction(type, function);
                break;
            case NORMAL:
                listener.registerFunction(type, function);
                break;
            case HIGH:
                highListener.registerFunction(type, function);
                break;
            case HIGHEST:
                highestListener.registerFunction(type, function);
                break;
            case MONITOR:
                monitorListener.registerFunction(type, function);
                break;
        }
    }
}
