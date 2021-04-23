package uwu.smsgamer.spygotutils;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.godead.lilliputian.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.smsgamer.spygotutils.commands.CommandManager;
import uwu.smsgamer.senapi.config.ConfigManager;
import uwu.smsgamer.spygotutils.listener.spigot.*;
import uwu.smsgamer.spygotutils.managers.ChatFilterManager;
import uwu.smsgamer.spygotutils.managers.chatfilter.*;
import uwu.smsgamer.spygotutils.utils.ChatUtils;
import uwu.smsgamer.spygotutils.utils.python.spigot.*;

import java.io.File;

public class SpigotLoader extends JavaPlugin implements Loader {
    private static SpigotLoader instance;

    public static SpigotLoader getInstance() {
        if (instance == null) instance = new SpigotLoader();
        return instance;
    }

    public SpigotLoader() {
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

        /*
         * PacketEvents likes to maintain compatibility with other plugins shading the same API.
         * If some other API loaded before you, the create method will return it's PacketEvents instance
         * to maintain compatibility, otherwise you will create the instance.
         * Set the settings.
         */
        PacketEvents.create(this).getSettings().checkForUpdates(true); //We won't check for updates
        /*
         * Access the stored instance of the PacketEvents class and load PacketEvents.
         * A few settings need to be specified before loading PacketEvents
         * as PacketEvents already uses a few in the load method.
         * To be safe, just set them all before loading.
         */
        PacketEvents.get().loadAsyncNewThread();

        SPYgotUtils ins = new SPYgotUtils(true);

        ins.firstLoad = !getDataFolder().exists();

        ConfigManager.getInstance().setup("messages", "chat-filter", "py-settings");

        new ChatFilterManager(new SChatFilter());

        ChatUtils.init();
        PyListener.init();
        ins.onLoad();
    }

    @Override
    public void onEnable() {

        //Register our listener (class extending PacketListenerDynamic)
        //By default it is configured to listen to all packets with no filter at all.
        SPacketProcessor packetProcessor = new SPacketProcessor();
        //We will FILTER all packets. We don't want to listen to anything.
        packetProcessor.filterAll();
        //Now since the filter has been applied, we are no longer listening to all packets. We can now "whitelist" our wanted packets.
        //Using this feature can improve performance.

        packetProcessor.addServerSidedPlayFilter(PacketType.Play.Server.CHAT);

        PacketEvents.get().registerListener(packetProcessor);

        PycketListener pycketListener = PycketListener.getInstance();
        PacketEvents.get().registerListener(pycketListener);

        SPYgotUtils.getInstance().onEnable();
        Bukkit.getPluginManager().registerEvents(BukkitListener.getInstance(), this);

        CommandManager.spigotCommands();
        if (SPYgotUtils.getInstance().firstLoad) SPYgotUtils.getInstance().configFiles();
    }

    @Override
    public void onDisable() {
        //Terminate PacketEvents
        PacketEvents.get().terminate();

        SPYgotUtils.getInstance().onDisable();
    }

    @Override
    public File getFile() {
        return super.getFile();
    }
}
