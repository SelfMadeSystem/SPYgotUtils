package uwu.smsgamer.spygotutils;

import java.io.File;
import java.util.logging.Logger;

public interface Loader {
    File getDataFolder();
    File getFile();
    Logger getLogger();
}
