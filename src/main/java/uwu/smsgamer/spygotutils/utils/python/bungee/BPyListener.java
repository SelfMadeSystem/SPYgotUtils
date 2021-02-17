package uwu.smsgamer.spygotutils.utils.python.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.event.EventHandler;
import org.python.core.PyFunction;

public class BPyListener implements Listener {
    private static BPyListener instance;
    public static BPyListener getInstance() {
      if (instance == null) instance = new BPyListener();
      return instance;
    }

    @EventHandler
    public void onEvent(Event event) {
    }

    public void registerListener(Class<? extends Event> event, PyFunction fun) {

//        ProxyServer.getInstance().getPluginManager().registerListener();
    }
}
