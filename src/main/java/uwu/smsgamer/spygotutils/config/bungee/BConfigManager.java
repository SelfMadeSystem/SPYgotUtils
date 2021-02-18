package uwu.smsgamer.spygotutils.config.bungee;

import net.md_5.bungee.config.*;
import uwu.smsgamer.spygotutils.config.*;

import java.io.*;
import java.util.*;

public class BConfigManager extends ConfigManager {
    public Map<String, Configuration> configs = new HashMap<>();
    public ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    @Override
    public void loadConfig(String name) {
        configs.remove(name);
        if (!pl.getDataFolder().exists()) {
            pl.getDataFolder().mkdir();
        }
        File configFile = new File(pl.getDataFolder(), name + ".yml");
        if (!configFile.exists())
            saveResource(name + ".yml", pl.getDataFolder());
        try {
            configs.put(name, provider.load(new File(pl.getDataFolder(), name + ".yml")));
        } catch (IOException e) {
            e.printStackTrace();
            pl.getLogger().severe("Error while loading config: " + name);
        }
    }

    @Override
    public void saveConfig(String name) {
        pl.getLogger().info("Saving config: " + name);
        try {
            provider.save(configs.get(name), new File(pl.getDataFolder().getAbsolutePath(), name + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
            pl.getLogger().severe("Error while saving config: " + name);
        }
    }

    @Override
    public Set<String> getConfigs() {
        return configs.keySet();
    }

    @Override
    public <T> void setConfVal(ConfVal<T> val, T dVal) {
        if (dVal instanceof Map) {
            vals.add(val);
            Configuration config = configs.get(val.config);
            if (!config.contains(val.name)) {
                config.set(val.name, dVal);
                val.value = dVal;
            } else {
                HashMap<String, Object> map = new HashMap<>();
                for (String key : config.getSection(val.name).getKeys())
                    map.put(key, config.get(val.name + "." + key));
                val.value = (T) map;
            }
        } else {
            vals.add(val);
            Configuration config = configs.get(val.config);
            val.value = config.get(val.name, dVal);
            if (!config.contains(val.name)) config.set(val.name, dVal);
        }
    }
}
