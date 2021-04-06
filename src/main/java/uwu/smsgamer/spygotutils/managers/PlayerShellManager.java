package uwu.smsgamer.spygotutils.managers;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uwu.smsgamer.spygotutils.utils.python.PyInterpreter;

import java.io.*;
import java.util.*;

public class PlayerShellManager {
    public static final Map<UUID, PyInterpreter> interpreters = new HashMap<>();

    public static PyInterpreter newInterpreter(Player player) {
        PyInterpreter pyInterpreter = (PyInterpreter) new PyInterpreter().setFuns(PythonManager.defaultFuns).setVars(PythonManager.defaultVars);
        pyInterpreter.interpreter.setOut(new PlayerWriter(player, "\u00A7r"));
        pyInterpreter.interpreter.setErr(new PlayerWriter(player, "\u00A7c"));
        return pyInterpreter;
    }

    public static void removeInterpreter(Player player) {
        interpreters.remove(player);
    }

    public static boolean interpret(Player player, String command) {
        return getInterpreter(player).interpret(command);
    }

    public static void save(Player player, String fileName) {
    }

    public static String getLines(Player player) {
        return getInterpreter(player).getLines();
    }

    public static PyInterpreter getInterpreter(Player player) {
        return interpreters.computeIfAbsent(player.getUniqueId(), e -> newInterpreter(player));
    }

    public static class PlayerWriter extends Writer {
        public Player player;
        public String prefix;
        public StringBuilder sb = new StringBuilder();

        public PlayerWriter(Player player, String prefix) {
            this.player = player;
            this.prefix = prefix;
            this.sb.append(this.prefix);
        }

        @Override
        public void write(char[] chars, int off, int len) {
            for (int i = off; i < off + len; i++) {
                char c = chars[i];
                if (c == '\n') {
                    flush();
                } else {
                    sb.append(c);
                }
            }
        }

        @Override
        public void flush() {
            String trim = sb.toString().trim();
            if (trim.length() > prefix.length()) player.sendMessage(trim);
            sb.setLength(0);
            sb.append(prefix);
        }

        @Override
        public void close() {
        }
    }
}
