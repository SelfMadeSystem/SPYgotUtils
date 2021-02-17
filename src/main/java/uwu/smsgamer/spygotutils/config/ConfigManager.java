package uwu.smsgamer.spygotutils.config;

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
public abstract class ConfigManager {
    private static ConfigManager instance;
    public static ConfigManager getInstance() {
      return instance;
    }

    public static void setInstance(ConfigManager instance) {
        ConfigManager.instance = instance;
    }

    public boolean needToSave = false;

    protected Loader pl;

    public void setup(String... configs) {
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

    public File configFile(String name) {
        return new File(pl.getDataFolder(), name + ".yml");
    }

    public abstract void loadConfig(String name);

    public abstract void saveConfig(String name);
    public abstract Set<String> getConfigs();

    public Set<ConfVal<?>> vals = new HashSet<>();

    public <T> void reloadConfVal(ConfVal<T> val) {
        setConfVal(val, val.dVal);
    }

    @SuppressWarnings("unchecked")
    public abstract <T> void setConfVal(ConfVal<T> val, T dVal);

    protected void saveResource(String resourcePath, File dataFolder) {
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

    protected InputStream getResource(String filename) {
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
