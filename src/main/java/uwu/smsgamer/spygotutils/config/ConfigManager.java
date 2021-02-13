package uwu.smsgamer.spygotutils.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.smsgamer.spygotutils.SPYgotUtils;

import java.io.*;
import java.util.*;

/**
 * Static class to store configuration files.
 *
 * @author Sms_Gamer_3808 (Shoghi Simon)
 */
public class ConfigManager {
    public static boolean needToSave = false;

    public static HashMap<String, YamlConfiguration> configs = new HashMap<>();
    private static JavaPlugin pl;

    public static void setup(String... configs) {
        pl = SPYgotUtils.getInstance().plugin;
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
            pl.saveResource(name + ".yml", false);
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
}
