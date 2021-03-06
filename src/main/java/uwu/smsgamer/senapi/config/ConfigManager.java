package uwu.smsgamer.senapi.config;

import de.leonhard.storage.*;
import de.leonhard.storage.internal.settings.ReloadSettings;
import uwu.smsgamer.spygotutils.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Abstract class to store configuration files.
 *
 * @author Sms_Gamer_3808 (Shoghi Simon)
 */
public class ConfigManager {
    public Map<String, Config> configs = new HashMap<>();
    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

    public static void setInstance(ConfigManager instance) {
        ConfigManager.instance = instance;
    }

    protected static Loader pl = SPYgotUtils.getLoader();

    public void setup(String... configs) {
        if (!pl.getDataFolder().exists()) {
            pl.getDataFolder().mkdir();
        }
        for (String config : configs) {
            pl.getLogger().info("Loading config: " + config);
            try {
                loadConfig(config);
                pl.getLogger().info("Loaded config: " + config);
            } catch (Exception e) {
                e.printStackTrace();
                pl.getLogger().severe("Error while loading config: " + config);
            }
        }
    }

    public File configFile(String name) {
        return new File(pl.getDataFolder(), name + ".yml");
    }

    public void reloadConfig(String name) {
        getConfig(name).forceReload();
    }

    private void loadConfig(String name) {
        if (new Throwable().getStackTrace()[0].getClassName().startsWith("org.python"))
            throw new IllegalStateException("Don't u dare execute this from Python!");

        File configFile = configFile(name);
        if (!configFile.exists())
            saveResource(name + ".yml", pl.getDataFolder());
        loadConfig(name, configFile);
    }

    public void loadConfig(String name, File file) {
        if (!pl.getDataFolder().exists()) {
            pl.getDataFolder().mkdir();
        }
        Config config = LightningBuilder.fromFile(file).createConfig();
        config.setReloadSettings(ReloadSettings.MANUALLY);
        configs.remove(config.getName());
        configs.put(name, config);
    }

    public void saveConfig(String name) {
        Config config = getConfig(name);
        config.write();
    }

    public Config getConfig(String name) {
        return configs.get(name);
    }

    public Set<Map.Entry<String, Config>> getConfigs() {
        return configs.entrySet();
    }

    public Set<ConfVal<?>> vals = new HashSet<>();

    public <T> void reloadConfVal(ConfVal<T> val) {
        setConfVal(val, val.dVal);
    }

    @SuppressWarnings("unchecked")
    public <T> void setConfVal(ConfVal<T> val, T dVal) {
        if (dVal instanceof Map) {
            vals.add(val);
            Config config = configs.get(val.config);
            if (!config.contains(val.name)) {
                config.set(val.name, dVal);
                val.value = dVal;
            } else {
                HashMap<String, Object> map = new HashMap<>();
                for (String key : config.getSection(val.name).singleLayerKeySet())
                    map.put(key, config.get(val.name + "." + key));
                val.value = (T) map;
            }
        } else {
            vals.add(val);
            Config config = configs.get(val.config);
            val.value = config.get(val.name, dVal);
            if (!config.contains(val.name)) config.set(val.name, dVal);
        }
    }

    private static void saveResource(String resourcePath, File dataFolder) {
        if (new Throwable().getStackTrace()[0].getClassName().startsWith("org.python"))
            throw new IllegalStateException("Don't u dare execute this from Python!");
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null)
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + pl.getFile());

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) outDir.mkdirs();

        try {
            if (!outFile.exists()) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                pl.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            pl.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public static InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = pl.
              getClass().
              getClassLoader().
              getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
