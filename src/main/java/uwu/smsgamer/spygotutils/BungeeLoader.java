package uwu.smsgamer.spygotutils;

import me.godead.lilliputian.*;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventBus;
import uwu.smsgamer.spygotutils.config.ConfigManager;
import uwu.smsgamer.spygotutils.config.bungee.BConfigManager;
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
        SPYgotUtils.loader = this;

        final Lilliputian lilliputian = new Lilliputian(this);
        lilliputian.getDependencyBuilder()
          .addDependency(new Dependency(Repository.MAVENCENTRAL,
            "org.python", "jython-standalone", "2.7.2"))
          .addDependency(new Dependency(Repository.MAVENCENTRAL,
            "org.xerial", "sqlite-jdbc", "3.8.11.2"))
          .loadDependencies();

        ConfigManager.setInstance(new BConfigManager());

        new SPYgotUtils(false).onLoad();

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
        System.out.println("Enabling");
        SPYgotUtils.getInstance().onEnable();
    }

    @Override
    public void onDisable() {
        System.out.println("Disabling");
        SPYgotUtils.getInstance().onDisable();
    }
}
