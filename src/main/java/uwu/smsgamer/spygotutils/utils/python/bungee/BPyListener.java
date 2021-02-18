package uwu.smsgamer.spygotutils.utils.python.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.event.*;
import org.python.core.PyFunction;
import uwu.smsgamer.spygotutils.BungeeLoader;

import java.lang.reflect.*;
import java.util.*;

public class BPyListener implements Listener {
    private static BPyListener instance;
    private static EventBus eventBus;
    Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority;
    Map<Class<?>, EventHandlerMethod[]> byEventBaked;
    Method bakeHandlersMethod;
    Method eventMethod;

    public static BPyListener getInstance() {
        if (instance == null) instance = new BPyListener();
        return instance;
    }

    byte priority = EventPriority.NORMAL;

    @SuppressWarnings("unchecked")
    public BPyListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeLoader.getInstance(), this);
        EventBus bus = null;
        try {
            Field busField = ProxyServer.getInstance().getPluginManager().getClass().getDeclaredField("eventBus");
            busField.setAccessible(true);
            bus = (net.md_5.bungee.event.EventBus) busField.get(ProxyServer.getInstance().getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        eventBus = bus;
        try {
            eventMethod = getClass().getMethod("onEvent", Event.class);
            bakeHandlersMethod = eventBus.getClass().getDeclaredMethod("bakeHandlers", Class.class);
            bakeHandlersMethod.setAccessible(true);

            Field byListenerAndPriorityField = eventBus.getClass().getDeclaredField("byListenerAndPriority");
            byListenerAndPriorityField.setAccessible(true);
            byListenerAndPriority = (Map<Class<?>, Map<Byte, Map<Object, Method[]>>>) byListenerAndPriorityField.get(eventBus);
            Field byEventBakedField = eventBus.getClass().getDeclaredField("byEventBaked");
            byEventBakedField.setAccessible(true);
            byEventBaked = (Map<Class<?>, EventHandlerMethod[]>) byEventBakedField.get(eventBus);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        registerListener(ClientConnectEvent.class, null);
    }

    public Map<Class<? extends Event>, Set<PyFunction>> funcs = new HashMap<>();

    @EventHandler
    public void onEvent(Event event) {
        System.out.println(event);
    }

    public void registerListener(Class<? extends Event> event, PyFunction fun) {
        Set<PyFunction> set = funcs.computeIfAbsent(event, k -> new HashSet<>());
        set.add(fun);

        Map<Byte, Map<Object, Method[]>> map = byListenerAndPriority.computeIfAbsent(event, k -> new HashMap<>());
        Map<Object, Method[]> map1 = map.computeIfAbsent(priority, k -> new HashMap<>());
        map1.computeIfAbsent(this, k -> new Method[]{eventMethod});
        bakeMethods(event);
    }

    private void bakeMethods(Class<? extends Event> event) {
        try {
            bakeHandlersMethod.invoke(eventBus, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
