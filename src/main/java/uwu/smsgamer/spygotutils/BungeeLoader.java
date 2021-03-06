package uwu.smsgamer.spygotutils;

import me.godead.lilliputian.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.*;
import uwu.smsgamer.senapi.utils.bungee.BPluginUtils;
import uwu.smsgamer.spygotutils.commands.CommandManager;
import uwu.smsgamer.senapi.config.ConfigManager;
import uwu.smsgamer.spygotutils.listener.bungee.*;
import uwu.smsgamer.spygotutils.managers.ChatFilterManager;
import uwu.smsgamer.spygotutils.managers.chatfilter.BChatFilter;
import uwu.smsgamer.spygotutils.utils.BChatUtils;
import uwu.smsgamer.spygotutils.utils.python.bungee.BPyListener;

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
          .addDependency(new Dependency(Repository.JITPACK,
            "com.github.simplix-softworks", "SimplixStorage", "3.2.2"))
          .loadDependencies();
        SPYgotUtils ins = new SPYgotUtils(false);

        ins.firstLoad = !getDataFolder().exists();

        ConfigManager.getInstance().setup("messages", "chat-filter", "py-settings");

        new ChatFilterManager(new BChatFilter());

        ins.onLoad();

        BChatUtils.init();

        BPyListener.getInstance();
    }

    @Override
    public void onEnable() {

        SPYgotUtils.getInstance().onEnable();
        CommandManager.bungeeCommands();

        ProxyServer.getInstance().getPluginManager().registerListener(this, BungeeListener.getInstance());

        if (SPYgotUtils.getInstance().firstLoad) SPYgotUtils.getInstance().configFiles();

        if (BPluginUtils.isPluginEnabled("protocolize-plugin")) {
            new BPacketProcessor(true);
            new BPacketProcessor(false);
        }
    }

    @Override
    public void onDisable() {
        SPYgotUtils.getInstance().onDisable();
    }
}
