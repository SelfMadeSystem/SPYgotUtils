package uwu.smsgamer.spygotutils.managers;

import org.bukkit.event.*;
import org.python.core.PyFunction;
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
    public static PyFunction registerEventFun;
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
          "    PythonManager.registerEvent(event_type, priority, function)\n" +
          "\n" +
          "\n" +
          "def register_command_executor(command, function):\n" +
          "    pass\n" +
          "\n" +
          "\n" +
          "def register_tab_completer(command, function):\n" +
          "    pass\n" +
          "\n" +
          "\n" +
          "def Command(name, ):\n" +
          "    pass\n");

        registerEventFun = (PyFunction) interpreter.get("register_event");
    }

    public static void loadScripts() {
        File dir = new File(SPYgotUtils.getInstance().plugin.getDataFolder(), "scripts");
        if (dir.exists()) {
            File[] arr = dir.listFiles();
            if (files == null) {
                for (File file : arr) {
                    PyScript script = new PyScript(file).set("register_event", registerEventFun);
                    try {
                        script.execFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                files = arr;
            } else {
                // Who cares about efficiency. It's during load or reload.
                // *Don't* want to reload a script; only load new ones.
                for (File file : arr)
                    if (!Arrays.asList(files).contains(file)) {
                        PyScript script = new PyScript(file).set("register_event", registerEventFun);
                        try {
                            script.execFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                HashSet<File> set = new HashSet<>(Arrays.asList(arr));
                set.addAll(Arrays.asList(files));
                files = set.toArray(new File[0]);
            }
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
