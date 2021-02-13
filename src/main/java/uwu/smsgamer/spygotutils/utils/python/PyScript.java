package uwu.smsgamer.spygotutils.utils.python;

import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import uwu.smsgamer.spygotutils.SPYgotUtils;
import uwu.smsgamer.spygotutils.utils.FileUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class PyScript {
    public static PyListener lowestListener;
    public static PyListener lowListener;
    public static PyListener listener;
    public static PyListener highListener;
    public static PyListener highestListener;
    public static PyListener monitorListener;
    public static PyFunction registerEventFun;
    private static File[] files;

    public File scriptFile;
    public PythonInterpreter interpreter;

    public static void init() {
        lowestListener = new PyListener(EventPriority.LOWEST, SPYgotUtils.getInstance().plugin);
        lowListener = new PyListener(EventPriority.LOW, SPYgotUtils.getInstance().plugin);
        listener = new PyListener(EventPriority.NORMAL, SPYgotUtils.getInstance().plugin);
        highListener = new PyListener(EventPriority.HIGH, SPYgotUtils.getInstance().plugin);
        highestListener = new PyListener(EventPriority.HIGHEST, SPYgotUtils.getInstance().plugin);
        monitorListener = new PyListener(EventPriority.MONITOR, SPYgotUtils.getInstance().plugin);

        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("def register_event(event_type, priority, function):\n" +
          "    from uwu.smsgamer.spygotutils.utils.python import PyScript\n" +
          "    PyScript.registerEvent(event_type, priority, function)\n");

        registerEventFun = (PyFunction) interpreter.get("register_event");
    }

    public static void loadScripts() {
        File dir = new File(SPYgotUtils.getInstance().plugin.getDataFolder(), "scripts");
        if (dir.exists()) {
            File[] arr = dir.listFiles();
            if (files == null){
                for (File file : arr) new PyScript(file);
                files = arr;
            } else {
                // Who cares about efficiency. It's during load or reload.
                // *Don't* want to reload a script; only load new ones.
                for (File file : arr) if (!Arrays.asList(files).contains(file)) new PyScript(file);
                HashSet<File> set = new HashSet<>(Arrays.asList(arr));
                set.addAll(Arrays.asList(files));
                files = set.toArray(new File[0]);
            }
        }
    }

    public PyScript(File scriptFile) {
        this.scriptFile = scriptFile;
        this.interpreter = new PythonInterpreter();
        this.interpreter.set("register_event", registerEventFun);
        this.interpreter.exec(FileUtils.readLineByLine(scriptFile));
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

    private static HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }

    private static Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
              && !clazz.getSuperclass().equals(Event.class)
              && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
            }
        }
    }

    private static class PyListener extends RegisteredListener {
        private final HashMap<Class<? extends Event>, Set<PyFunction>> functions = new HashMap<>();

        public PyListener(EventPriority priority, Plugin plugin) {
            super(null, null, priority, plugin, false);
        }

        public void registerFunction(Class<? extends Event> event, PyFunction fun) {
            functions.computeIfAbsent(event, k -> {
                getEventListeners(event).register(this);
                return new HashSet<>();
            }).add(fun);
        }

        @Override
        public Listener getListener() {
            return new Listener(){
                @Override
                public boolean equals(Object o) {
                    return false;
                }
            };
        }

        @Override
        public void callEvent(Event event) {
            Set<PyFunction> funs = functions.get(event.getClass());
            for (PyFunction fun : funs) {
                fun.__call__(Py.java2py(event));
            }
        }
    }
}
