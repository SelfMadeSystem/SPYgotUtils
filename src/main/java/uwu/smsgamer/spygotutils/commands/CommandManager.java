package uwu.smsgamer.spygotutils.commands;

import uwu.smsgamer.spygotutils.commands.commands.*;

public class CommandManager {
    public static void setupCommands() {
        new EvaluateCommand();
        new SendMsgCommand();
        new SPYgotUtilsCommand();
    }
}
