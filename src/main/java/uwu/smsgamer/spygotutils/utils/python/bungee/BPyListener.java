package uwu.smsgamer.spygotutils.utils.python.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.event.*;
import org.python.core.PyFunction;
import uwu.smsgamer.spygotutils.BungeeLoader;

import java.lang.reflect.Field;
import java.util.*;

public class BPyListener implements Listener {
    private static BPyListener instance;
    private static EventBus eventBus;
    public static BPyListener getInstance() {
      if (instance == null) instance = new BPyListener();
      return instance;
    }

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
        assert bus != null;
        eventBus = bus;
    }

    public Map<Class<? extends  Event>, Set<PyFunction>> funcs = new HashMap<>();

    @EventHandler
    public void onEvent(Event event) {
    }

    public void registerListener(Class<? extends Event> event, PyFunction fun) {
        Set<PyFunction> set = funcs.computeIfAbsent(event, k -> new HashSet<>());
        set.add(fun);

      //  eventBus.
    }
}
