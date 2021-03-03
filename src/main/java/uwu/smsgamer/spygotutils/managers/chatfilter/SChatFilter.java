package uwu.smsgamer.spygotutils.managers.chatfilter;

import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import uwu.smsgamer.senapi.utils.StringUtils;
import uwu.smsgamer.senapi.utils.spigot.SStringUtils;
import uwu.smsgamer.spygotutils.managers.AbstractChatFilter;

import java.util.List;

public class SChatFilter extends AbstractChatFilter {
    @Override
    public void execCommands(List<String> commands, String[] args, Object player) {
        if (commands == null) return;
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        for (String command : commands)
            Bukkit.dispatchCommand(sender,
              StringUtils.replaceArgsPlaceholders(SStringUtils.replacePlaceholders((OfflinePlayer) player,
                StringUtils.colorize(command)), args));
    }
}
