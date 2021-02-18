package uwu.smsgamer.spygotutils.commands;

import uwu.smsgamer.spygotutils.commands.commands.bungee.*;
import uwu.smsgamer.spygotutils.commands.commands.spigot.*;

public class CommandManager {
    public static void spigotCommands() {
        new EvaluateCommand();
        new SendMsgCommand();
        new SPYgotUtilsCommand();
    }

    public static void bungeeCommands() {
        new BEvaluateCommand();
        new BSendMsgCommand();
        new BSPYgotUtilsCommand();
    }
}
