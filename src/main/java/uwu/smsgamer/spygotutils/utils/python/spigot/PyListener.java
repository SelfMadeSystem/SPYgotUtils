package uwu.smsgamer.spygotutils.utils.python.spigot;

import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.python.core.*;
import uwu.smsgamer.spygotutils.SPYgotUtils;

import java.lang.reflect.Method;
import java.util.*;

public class PyListener extends RegisteredListener {
    public static PyListener lowestListener;
    public static PyListener lowListener;
    public static PyListener listener;
    public static PyListener highListener;
    public static PyListener highestListener;
    public static PyListener monitorListener;

    public static void init() {
        lowestListener = new PyListener(EventPriority.LOWEST, SPYgotUtils.getInstance().spigotPlugin);
        lowListener = new PyListener(EventPriority.LOW, SPYgotUtils.getInstance().spigotPlugin);
        listener = new PyListener(EventPriority.NORMAL, SPYgotUtils.getInstance().spigotPlugin);
        highListener = new PyListener(EventPriority.HIGH, SPYgotUtils.getInstance().spigotPlugin);
        highestListener = new PyListener(EventPriority.HIGHEST, SPYgotUtils.getInstance().spigotPlugin);
        monitorListener = new PyListener(EventPriority.MONITOR, SPYgotUtils.getInstance().spigotPlugin);
    }

    @SuppressWarnings("unused")
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
