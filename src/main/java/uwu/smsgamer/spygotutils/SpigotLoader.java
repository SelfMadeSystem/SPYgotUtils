package uwu.smsgamer.spygotutils;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.godead.lilliputian.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.smsgamer.spygotutils.commands.CommandManager;
import uwu.smsgamer.spygotutils.config.ConfigManager;
import uwu.smsgamer.spygotutils.config.spigot.SConfigManager;
import uwu.smsgamer.spygotutils.listener.*;
import uwu.smsgamer.spygotutils.managers.ChatFilterManager;
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
          .addDependency(new Dependency(Repository.JITPACK,
            "com.github.retrooper", "packetevents", "v1.8-pre-4"))
          .addDependency(new Dependency(Repository.MAVENCENTRAL,
            "org.python", "jython-standalone", "2.7.2"))
          .addDependency(new Dependency(Repository.MAVENCENTRAL,
            "org.xerial", "sqlite-jdbc", "3.8.11.2"))
          .loadDependencies();

        /*
         * PacketEvents likes to maintain compatibility with other plugins shading the same API.
         * If some other API loaded before you, the create method will return it's PacketEvents instance
         * to maintain compatibility, otherwise you will create the instance.
         * Set the settings.
         */
        //When a player injection fails, what should happen?
        PacketEvents.create(this).getSettings().checkForUpdates(true) //We won't check for updates
          .injectionFailureReaction(player -> {
              /*
               * NOTE: We are OFF the main thread. Kicking is not thread safe. We will switch over to the main thread to kick.
               * By default if you don't specify a reaction, PacketEvents will kick them! If you need to modify the kick message or the whole action,
               * use this feature!
               * You NOT kicking the player will result in them staying on the server, and PacketEvents won't notice if they are sending any packets.
               * We also won't notice if the server is sending any packets to that player.
               */
              Bukkit.getScheduler().runTask(this, () -> player.kickPlayer("Failed to inject. Please rejoin!"));
          });
        /*
         * Access the stored instance of the PacketEvents class and load PacketEvents.
         * A few settings need to be specified before loading PacketEvents
         * as PacketEvents already uses a few in the load method.
         * To be safe, just set them all before loading.
         */
        PacketEvents.get().load();
        ConfigManager.setInstance(new SConfigManager());

        SPYgotUtils ins = new SPYgotUtils(true);

        ins.firstLoad = !getDataFolder().exists();

        ConfigManager.getInstance().setup("messages", "chat-filter", "py-settings");

        ChatFilterManager.getInstance();

        ChatUtils.init();
        PyListener.init();
        ins.onLoad();
    }

    @Override
    public void onEnable() {
        //Initiate PacketEvents
        PacketEvents.get().init(this);

        //Register our listener (class extending PacketListenerDynamic)
        //By default it is configured to listen to all packets with no filter at all.
        PacketProcessor packetProcessor = new PacketProcessor();
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
