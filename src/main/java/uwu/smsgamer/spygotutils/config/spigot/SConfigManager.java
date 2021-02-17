package uwu.smsgamer.spygotutils.config.spigot;

import org.bukkit.configuration.file.YamlConfiguration;
import uwu.smsgamer.spygotutils.config.*;

import java.io.*;
import java.util.*;

public class SConfigManager extends ConfigManager {
    public HashMap<String, YamlConfiguration> configs = new HashMap<>();

    @Override
    public void loadConfig(String name) {
        configs.remove(name);
        File configFile = configFile(name);
        if (!configFile.exists())
            saveResource(name + ".yml", pl.getDataFolder());
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configs.put(name, config);
    }

    @Override
    public void saveConfig(String name) {
        pl.getLogger().info("Saving config: " + name);
        try {
            configs.get(name).save(pl.getDataFolder().getAbsolutePath() + File.separator + name + ".yml");
        } catch (IOException e) {
            e.printStackTrace();
            pl.getLogger().severe("Error while saving config: " + name);
        }
    }

    @Override
    public Set<String> getConfigs() {
        return configs.keySet();
    }

    public YamlConfiguration getConfig(String name) {
        return configs.get(name);
    }

    @Override
    public <T> void setConfVal(ConfVal<T> val, T dVal) {
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
