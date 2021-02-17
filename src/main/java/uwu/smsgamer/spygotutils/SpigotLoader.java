package uwu.smsgamer.spygotutils;

import me.godead.lilliputian.*;
import org.bukkit.plugin.java.JavaPlugin;

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
        final Lilliputian lilliputian = new Lilliputian(this);
        lilliputian.getDependencyBuilder()
          .addDependency(new Dependency(Repository.JITPACK,
            "com.github.retrooper", "packetevents", "v1.8-pre-4"))
          .addDependency(new Dependency(Repository.MAVENCENTRAL,
            "org.python", "jython-standalone", "2.7.2"))
          .addDependency(new Dependency(Repository.MAVENCENTRAL,
            "org.xerial", "sqlite-jdbc", "3.8.11.2"))
          .loadDependencies();

        new SPYgotUtils(this, null, true).onLoad();
    }

    @Override
    public void onEnable() {
        SPYgotUtils.getInstance().onEnable();
    }

    @Override
    public void onDisable() {
        SPYgotUtils.getInstance().onDisable();
    }

    @Override
    public File getFile() {
        return super.getFile();
    }
}
