package uwu.smsgamer.spygotutils.utils.python.bungee;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.plugin.Command;
import org.python.core.*;
import uwu.smsgamer.spygotutils.BungeeLoader;

public class BPyCommand extends Command {
    public PyFunction executor;

    public BPyCommand(String name, String... aliases) {
        super(name, null, aliases);
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeLoader.getInstance(), this);
    }

    public void setExecutor(PyFunction executor) {
        this.executor = executor;
    }

    @SuppressWarnings("unused")
    public void set_executor(PyFunction executor) {
        setExecutor(executor);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (executor == null)
            throw new IllegalStateException("Executor for PyCommand is null!");
        // let bungee handle exceptions
        executor.__call__(new PyObject[]{Py.java2py(sender), Py.java2py(args)});
    }
}
