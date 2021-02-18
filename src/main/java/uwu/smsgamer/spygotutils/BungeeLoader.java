package uwu.smsgamer.spygotutils;

import me.godead.lilliputian.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.event.EventBus;
import uwu.smsgamer.spygotutils.commands.CommandManager;
import uwu.smsgamer.spygotutils.config.ConfigManager;
import uwu.smsgamer.spygotutils.config.bungee.BConfigManager;
import uwu.smsgamer.spygotutils.utils.BChatUtils;
import uwu.smsgamer.spygotutils.utils.python.bungee.BPyListener;

import java.lang.reflect.Field;

public class BungeeLoader extends Plugin implements Loader {
    private static BungeeLoader instance;
    public static BungeeLoader getInstance() {
      return instance;
    }

    public BungeeLoader() {
        instance = this;
    }

    public BungeeLoader(ProxyServer proxy, PluginDescription description) {
        super(proxy, description);
        instance = this;
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
        SPYgotUtils ins = new SPYgotUtils(false);

        ins.firstLoad = !getDataFolder().exists();

        ConfigManager.setInstance(new BConfigManager());

        ConfigManager.getInstance().setup("messages", "chat-filter", "py-settings");

        ins.onLoad();

        BChatUtils.init();

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
        CommandManager.bungeeCommands();
    }

    @Override
    public void onDisable() {
        SPYgotUtils.getInstance().onDisable();
    }
}
