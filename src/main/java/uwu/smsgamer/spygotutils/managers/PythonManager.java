package uwu.smsgamer.spygotutils.managers;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import uwu.smsgamer.senapi.utils.Pair;
import uwu.smsgamer.spygotutils.SPYgotUtils;
import uwu.smsgamer.senapi.config.ConfVal;
import uwu.smsgamer.spygotutils.utils.FileUtils;
import uwu.smsgamer.spygotutils.utils.python.PyScript;

import java.io.File;
import java.util.*;

public class PythonManager {
    public static PyFunction[] defaultFuns;
    public static Pair<String, PyObject>[] defaultVars;
    private static File[] files;
    private static final List<PyScript> scripts = new ArrayList<>();

    public static ConfVal<List<String>> loadScripts = new ConfVal<>("load-scripts", "py-settings", Collections.emptyList());
    public static ConfVal<String> pythonInit = new ConfVal<>("init", "py-settings", "");
    public static ConfVal<List<String>> startDefs = new ConfVal<>("start-defs", "py-settings", Collections.emptyList());
    public static ConfVal<List<String>> startVars = new ConfVal<>("start-vars", "py-settings", Collections.emptyList());

    public static void init() {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec(pythonInit.getValue());
        List<String> defs = startDefs.getValue();
        defaultFuns = new PyFunction[defs.size()];
        for (int i = 0; i < defs.size(); i++) {
            String def = defs.get(i);
            if (def.startsWith("spigot:")) {
                if (SPYgotUtils.getInstance().onSpigot) {
                    PyObject pyObject = interpreter.get(def.substring(7));
                    if (pyObject == null) {
                        SPYgotUtils.getLoader().getLogger().warning("Def name: " + def + " is null.");
                        continue;
                    }
                    defaultFuns[i] = (PyFunction) pyObject;
                }
            } else if (def.startsWith("bungee:")) {
                if (!SPYgotUtils.getInstance().onSpigot) {
                    PyObject pyObject = interpreter.get(def.substring(7));
                    if (pyObject == null) {
                        SPYgotUtils.getLoader().getLogger().warning("Def name: " + def + " is null.");
                        continue;
                    }
                    defaultFuns[i] = (PyFunction) pyObject;
                }
            } else {
                PyObject pyObject = interpreter.get(def);
                if (pyObject == null) {
                    SPYgotUtils.getLoader().getLogger().warning("Def name: " + def + " is null.");
                    continue;
                }
                defaultFuns[i] = (PyFunction) pyObject;
            }
        }
        List<String> vars = startVars.getValue();
        defaultVars = new Pair[vars.size()];
        for (int i = 0; i < vars.size(); i++) {
            String def = vars.get(i);
            if (def.startsWith("spigot:")) {
                if (SPYgotUtils.getInstance().onSpigot) {
                    PyObject pyObject = interpreter.get(def.substring(7));
                    if (pyObject == null) {
                        SPYgotUtils.getLoader().getLogger().warning("Obj name: " + def + " is null.");
                        continue;
                    }
                    defaultVars[i] = new Pair<>(def, pyObject);
                }
            } else if (def.startsWith("bungee:")) {
                if (!SPYgotUtils.getInstance().onSpigot) {
                    PyObject pyObject = interpreter.get(def.substring(7));
                    if (pyObject == null) {
                        SPYgotUtils.getLoader().getLogger().warning("Obj name: " + def + " is null.");
                        continue;
                    }
                    defaultVars[i] = new Pair<>(def, pyObject);
                }
            } else {
                PyObject pyObject = interpreter.get(def);
                if (pyObject == null) {
                    SPYgotUtils.getLoader().getLogger().warning("Obj name: " + def + " is null.");
                    continue;
                }
                defaultVars[i] = new Pair<>(def, pyObject);
            }
        }
        interpreter.exec("from sys import path\n" +
          "path.append('" + SPYgotUtils.getLoader().getDataFolder() + File.separator + "scripts')");
    }

    public static void execute(PythonInterpreter interpreter, File file, String fileName) {
        execute(interpreter, FileUtils.readLineByLine(file), fileName);
    }

    public static void execute(PythonInterpreter interpreter, String str, String fileName) {
        if (defaultFuns != null) for (PyFunction obj : defaultFuns) if (obj != null) interpreter.set(obj.__name__, obj);
        if (defaultVars != null) for (Pair<String, PyObject> pair : defaultVars) if (pair != null) interpreter.set(pair.a, pair.b);
        interpreter.exec(interpreter.compile(str, fileName));
    }

    public static void loadScripts(boolean reload) {
        File dir = new File(SPYgotUtils.getLoader().getDataFolder(), "scripts");
        if (!dir.exists()) return;
        List<File> exclude = new ArrayList<>(files == null ? Collections.emptyList() : Arrays.asList(files));
        for (String fileName : loadScripts.getValue()) {
            File file = getFile(fileName);
            if (!file.exists() || exclude.contains(file)) continue;
            newScript(file, reload);
            exclude.add(file);
        }
        files = exclude.toArray(new File[0]);
    }

    public static void newScript(File file, boolean reload) {
        if (!file.exists()) return;
        PyScript script = new PyScript(file).setFuns(defaultFuns).setVars(defaultVars);
        try {
            script.execFile();
            scripts.add(script);
            script.getGoodFuns();
            if (reload) script.execAll(script.enableFuns);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getFile(String scriptName) {
        return new File(SPYgotUtils.getLoader().getDataFolder(), "scripts" + File.separator + scriptName);
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
