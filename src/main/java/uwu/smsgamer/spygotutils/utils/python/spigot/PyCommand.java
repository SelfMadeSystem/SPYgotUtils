package uwu.smsgamer.spygotutils.utils.python.spigot;

import org.bukkit.*;
import org.bukkit.command.*;
import org.python.core.*;

import java.lang.reflect.*;
import java.util.*;

public class PyCommand extends Command {
    public PyFunction executor;
    public PyFunction tabCompleter;

    public PyCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        registerCommand(this);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (executor == null) {
            throw new IllegalStateException("Executor for PyCommand is null!");
        }
        // let bukkit handle exceptions
        PyObject obj = executor.__call__(new PyObject[]{Py.java2py(sender), Py.java2py(label), Py.java2py(args)});
        if (obj instanceof PyBoolean) {
            return ((PyBoolean) obj).getBooleanValue();
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (tabCompleter == null) {
            return super.tabComplete(sender, alias, args);
        }
        // let bukkit handle exceptions
        PyObject obj = tabCompleter.__call__(new PyObject[]{Py.java2py(sender), Py.java2py(alias), Py.java2py(args)});
        if (obj instanceof PySequenceList) {
            List<String> s = new ArrayList<>();
            for (Object o : ((PySequenceList) obj)) {
                s.add(o.toString());
            }
            return s;
        }
        return super.tabComplete(sender, alias, args);
    }

    public void setExecutor(PyFunction executor) {
        this.executor = executor;
    }

    public void setTabCompleter(PyFunction tabCompleter) {
        this.tabCompleter = tabCompleter;
    }

    @SuppressWarnings("unused")
    public void set_executor(PyFunction executor) {
        setExecutor(executor);
    }

    @SuppressWarnings("unused")
    public void set_tab_completer(PyFunction tabCompleter) {
        setTabCompleter(tabCompleter);
    }

    public static void registerCommand(Command command) {
        // Very Dirty Hack.
        try {
            getCommandMap(Bukkit.getServer()).register("spygotscripts", command);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException |
          IllegalArgumentException | InvocationTargetException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    private static SimpleCommandMap getCommandMap(Server server) throws NoSuchMethodException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
        Method getCommandMapMethod = server.getClass().getMethod("getCommandMap");
        return (SimpleCommandMap) getCommandMapMethod.invoke(server);
    }
}
