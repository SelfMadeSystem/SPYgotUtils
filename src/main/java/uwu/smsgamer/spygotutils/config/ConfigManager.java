package uwu.smsgamer.spygotutils.config;

import org.bukkit.configuration.file.YamlConfiguration;
import uwu.smsgamer.spygotutils.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Static class to store configuration files.
 *
 * @author Sms_Gamer_3808 (Shoghi Simon)
 */
public class ConfigManager {
    public static boolean needToSave = false;

    public static HashMap<String, YamlConfiguration> configs = new HashMap<>();
    private static Loader pl;

    public static void setup(String... configs) {
        pl = SPYgotUtils.getLoader();
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

    public static YamlConfiguration getConfig(String name) {
        return configs.get(name);
    }

    public static File configFile(String name) {
        return new File(pl.getDataFolder(), name + ".yml");
    }

    public static YamlConfiguration loadConfig(String name) {
        configs.remove(name);
        File configFile = configFile(name);
        if (!configFile.exists())
            saveResource(name + ".yml", pl.getDataFolder());
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configs.put(name, config);
        return config;
    }

    public static void saveConfig(String name) {
        pl.getLogger().info("Saving config: " + name);
        try {
            configs.get(name).save(pl.getDataFolder().getAbsolutePath() + File.separator + name + ".yml");
        } catch (IOException e) {
            e.printStackTrace();
            pl.getLogger().severe("Error while saving config: " + name);
        }
    }

    public static Set<ConfVal<?>> vals = new HashSet<>();

    public static <T> void reloadConfVal(ConfVal<T> val) {
        setConfVal(val, val.dVal);
    }

    @SuppressWarnings("unchecked")
    public static <T> void setConfVal(ConfVal<T> val, T dVal) {
        if (dVal instanceof Map) {
            vals.add(val);
            YamlConfiguration config = getConfig(val.config);
            if (!config.contains(val.name)) {
                config.set(val.name, dVal);
                val.value = dVal;
            } else {
                HashMap<String, Object> map = new HashMap<>();
                for (String key : config.getConfigurationSection(val.name).getKeys(false))
                    map.put(key, config.get(val.name + "." + key));
                val.value = (T) map;
            }
        } else {
            vals.add(val);
            YamlConfiguration config = getConfig(val.config);
            val.value = (T) config.get(val.name, dVal);
            if (!config.contains(val.name)) config.set(val.name, dVal);
        }
    }

    private static void saveResource(String resourcePath, File dataFolder) {
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

    private static InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = pl.getClass().getClassLoader().getResource(filename);

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
