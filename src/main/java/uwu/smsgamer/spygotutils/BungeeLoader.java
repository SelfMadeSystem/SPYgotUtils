package uwu.smsgamer.spygotutils;

import me.godead.lilliputian.*;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventBus;
import uwu.smsgamer.spygotutils.utils.python.bungee.BPyListener;

import java.lang.reflect.Field;

public class BungeeLoader extends Plugin implements Loader {
    private static BungeeLoader instance;
    public static BungeeLoader getInstance() {
      if (instance == null) instance = new BungeeLoader();
      return instance;
    }

    @Override
    public void onLoad() {
        final Lilliputian lilliputian = new Lilliputian(this);
        lilliputian.getDependencyBuilder()
          .addDependency(new Dependency(Repository.JITPACK,
            "com.github.retrooper", "packetevents", "v1.8-pre-4"))
          .addDependency(new Dependency(Repository.MAVENCENTRAL,
            "org.python", "jython-standalone", "2.7.2"))
          .addDependency(new Dependency(Repository.MAVENCENTRAL,
            "org.xerial", "sqlite-jdbc", "3.8.11.2"))
          .loadDependencies();

        new SPYgotUtils(null, this, false).onLoad();

        EventBus bus = null;
        try {
            Field busField = getProxy().getPluginManager().getClass().getDeclaredField("eventBus");
            busField.setAccessible(true);
            bus = (EventBus) busField.get(getProxy().getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assert bus != null;
        bus.register(BPyListener.getInstance());
    }

    @Override
    public void onEnable() {
        SPYgotUtils.getInstance().onEnable();
    }

    @Override
    public void onDisable() {
        SPYgotUtils.getInstance().onDisable();
    }
}
