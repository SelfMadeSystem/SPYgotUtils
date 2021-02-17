package uwu.smsgamer.spygotutils;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.Bukkit;
import uwu.smsgamer.spygotutils.commands.CommandManager;
import uwu.smsgamer.spygotutils.config.*;
import uwu.smsgamer.spygotutils.config.bungee.BConfigManager;
import uwu.smsgamer.spygotutils.config.spigot.SConfigManager;
import uwu.smsgamer.spygotutils.listener.*;
import uwu.smsgamer.spygotutils.managers.*;
import uwu.smsgamer.spygotutils.utils.*;
import uwu.smsgamer.spygotutils.utils.python.spigot.*;

import java.io.File;

public final class SPYgotUtils {
    public static SPYgotUtils INSTANCE;

    public static SPYgotUtils getInstance() {
        return INSTANCE;
    }

    public static Loader loader;
    public boolean onSpigot;
    public boolean firstLoad;

    public SPYgotUtils(boolean onSpigot) {
        INSTANCE = this;
        this.onSpigot = onSpigot;
    }

    public static Loader getLoader() {
        return loader;
    }

    public void onLoad() {
        firstLoad = !getDataFolder().exists();

        EvalUtils.init();
        PythonManager.init();

        if (firstLoad) sScriptFiles();

        PythonManager.loadScripts();
    }

    public void onEnable() {
        PythonManager.onEnable();
    }

    public ConfVal<Boolean> removePyClasses = new ConfVal<>("py-settings", "remove-classes-on-disable", true);

    public void onDisable() {
        PythonManager.onDisable();

        if (removePyClasses.getValue()) {
            for (File file : new File(SPYgotUtils.getLoader().getDataFolder(), "scripts")
              .listFiles(pathname -> pathname.getName().endsWith("$py.class")))
                file.delete();
        }
    }

    private void sScriptFiles() {
        // Shitty ik but I'm lazy.
        FileUtils.saveResource(getLoader(), "spigot/event.py", new File(getLoader().getDataFolder(), "scripts/event.py"), false);
        FileUtils.saveResource(getLoader(), "spigot/command.py", new File(getLoader().getDataFolder(), "scripts/command.py"), false);
        FileUtils.saveResource(getLoader(), "spigot/packet.py", new File(getLoader().getDataFolder(), "scripts/packet.py"), false);
        FileUtils.saveResource(getLoader(), "spigot/test.py", new File(getLoader().getDataFolder(), "scripts/test.py"), false);
        FileUtils.saveResource(getLoader(), "spigot/itest.py", new File(getLoader().getDataFolder(), "scripts/itest.py"), false);
    }

    void configFiles() {
        ConfigManager.getInstance().saveConfig("messages");
        ConfigManager.getInstance().saveConfig("py-settings");
    }

    public File getDataFolder() {
        return getLoader().getDataFolder();
    }
}
