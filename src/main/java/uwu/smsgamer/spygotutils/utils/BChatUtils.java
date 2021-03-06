package uwu.smsgamer.spygotutils.utils;

import net.md_5.bungee.api.*;
import org.jetbrains.annotations.Nullable;
import uwu.smsgamer.senapi.config.ConfVal;

import java.util.List;

public class BChatUtils {
    public static final ConfVal<String> prefix = new ConfVal<>("prefix", "messages", "&8&l[&9SPYgotUtils&8&l]");
    public static final ConfVal<String> errorNoMessage = new ConfVal<>("messages.error-no-message", "messages",
      "%prefix% &cAn error occurred whilst executing this command.");
    public static final ConfVal<String> errorWithMessage = new ConfVal<>("messages.error-with-message", "messages",
      "%prefix% &cAn error occurred whilst executing this command: %msg%");

    public static void init() {}

    public static void execCmd(ConfVal<List<String>> commands, CommandSender player) {
        for (String s : commands.getValue()) execCmd(s, player);
    }

    public static void execCmd(List<String> commands, CommandSender player) {
        if (commands != null) for (String s : commands) execCmd(s, player);
    }

    public static void execCmd(String command, CommandSender player) {
        if (command == null || command.isEmpty()) return;
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), toChatString(command, player));
    }

    public static void sendMessage(ConfVal<String> message, CommandSender player) {
        sendMessage(message.getValue(), player);
    }

    public static void sendMessage(String message, CommandSender player) {
        if (message == null || message.isEmpty()) return;
        player.sendMessage(toChatString(message, player));
    }

    public static String toChatString(String message, @Nullable CommandSender player) {
        message = message.replace("%prefix%", prefix.getValue());
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void errorOccurred(CommandSender sender) {
        sendMessage(errorNoMessage, sender);
    }

    public static void errorOccurred(CommandSender sender, String message) {
        sendMessage(errorWithMessage.getValue().replace("%msg%", message), sender);
    }
}
