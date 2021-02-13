package uwu.smsgamer.spygotutils.utils.python;

import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.python.core.*;

import java.lang.reflect.Method;
import java.util.*;

public class PyListener extends RegisteredListener {
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
