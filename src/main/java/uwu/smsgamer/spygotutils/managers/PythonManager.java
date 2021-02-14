package uwu.smsgamer.spygotutils.managers;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import uwu.smsgamer.spygotutils.SPYgotUtils;
import uwu.smsgamer.spygotutils.config.ConfigManager;
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
          "    PyListener.registerEvent(event_type, priority, function)\n" +
          "def Command(name, description=\"\", usage_msg=None, aliases=None):\n" +
          "    if usage_msg is None:\n" +
          "        usage_msg = \"/\" + name\n" +
          "    if aliases is None:\n" +
          "        aliases = []\n" +
          "    from uwu.smsgamer.spygotutils.utils.python import PyCommand\n" +
          "    return PyCommand(name, description, usage_msg, aliases)\n");
        interpreter.exec("from sys import path\n" +
          "path.append(\"" + SPYgotUtils.getInstance().plugin.getDataFolder() + File.separator + "scripts\")");

        defaultFuns = new PyFunction[]{(PyFunction) interpreter.get("register_event"),
          (PyFunction) interpreter.get("Command")};

        packetListener = Py.java2py(PycketListener.getInstance());
    }

    public static void loadScripts() {
        File dir = new File(SPYgotUtils.getInstance().plugin.getDataFolder(), "scripts");
        if (!dir.exists()) return;
        List<File> exclude = new ArrayList<>(files == null ? Collections.emptyList() : Arrays.asList(files));
        for (String fileName : getLoadScripts()) {
            File file = getFile(fileName);
            if (!file.exists() || exclude.contains(file)) continue;
            newScript(file);
            exclude.add(file);
        }
        files = exclude.toArray(new File[0]);
    }

    public static void newScript(File file) {
        if (!file.exists()) return;
        PyScript script = new PyScript(file).setFuns(defaultFuns).set("packet_listener", packetListener);
        try {
            script.execFile();
            scripts.add(script);
            script.getGoodFuns();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getLoadScripts() {
        return ConfigManager.getConfig("py-settings").getStringList("load-scripts");
    }

    public static File getFile(String scriptName) {
        return new File(SPYgotUtils.getInstance().plugin.getDataFolder(), "scripts" + File.separator + scriptName);
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
