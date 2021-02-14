package uwu.smsgamer.spygotutils.managers;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import uwu.smsgamer.spygotutils.SPYgotUtils;
import uwu.smsgamer.spygotutils.utils.python.*;

import java.io.File;
import java.util.*;

public class PythonManager {
    public static PyFunction[] defaultFuns;
    public static PyObject packetListener;
    private static File[] files;
    private static List<PyScript> scripts = new ArrayList<>();

    public static void init() {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("def register_event(event_type, priority, function):\n" +
          "    from uwu.smsgamer.spygotutils.utils.python import PyListener\n" +
          "    PyListener.registerEvent(event_type, priority, function)" +
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
        if (!file.getName().endsWith(".py")) return; // not python script; don't do shit
        PyScript script = new PyScript(file).setFuns(defaultFuns).set("packet_listener", packetListener);
        try {
            script.execFile();
            scripts.add(script);
            script.getGoodFuns();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onEnable() {
        for (PyScript script : scripts) script.execAll(script.enableFuns);
    }

    public static void onReload() {
        for (PyScript script : scripts) script.execAll(script.reloadFuns);
    }

    public static void onDisable() {
        for (PyScript script : scripts) script.execAll(script.disableFuns);
    }
}
