package uwu.smsgamer.spygotutils;

import uwu.smsgamer.spygotutils.config.*;
import uwu.smsgamer.spygotutils.managers.PythonManager;
import uwu.smsgamer.spygotutils.utils.*;

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
        EvalUtils.init();
        PythonManager.init();

        if (firstLoad) {
            if (onSpigot) sScriptFiles();
            else bScriptFiles();
        }

        PythonManager.loadScripts();
    }

    public void onEnable() {
        PythonManager.onEnable();
    }

    public void onDisable() {
        PythonManager.onDisable();

        if (new ConfVal<>("remove-classes-on-disable", "py-settings", true).getValue()) {
            File[] listFiles = new File(getDataFolder(), "scripts")
              .listFiles(pathname -> pathname.getName().endsWith("$py.class"));
            if (listFiles != null)
                for (File file : listFiles) file.delete();
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

    private void bScriptFiles() {
        // Shitty ik but I'm lazy.
        FileUtils.saveResource(getLoader(), "bungee/test.py", new File(getLoader().getDataFolder(), "scripts/test.py"), false);
        FileUtils.saveResource(getLoader(), "bungee/itest.py", new File(getLoader().getDataFolder(), "scripts/itest.py"), false);
        FileUtils.saveResource(getLoader(), "bungee/event.py", new File(getLoader().getDataFolder(), "scripts/event.py"), false);
    }

    void configFiles() {
        ConfigManager.getInstance().saveConfig("messages");
        ConfigManager.getInstance().saveConfig("py-settings");
    }

    public File getDataFolder() {
        return getLoader().getDataFolder();
    }
}
