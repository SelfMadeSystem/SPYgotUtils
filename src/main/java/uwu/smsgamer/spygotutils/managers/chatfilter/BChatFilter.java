package uwu.smsgamer.spygotutils.managers.chatfilter;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.plugin.PluginManager;
import uwu.smsgamer.senapi.utils.StringUtils;
import uwu.smsgamer.spygotutils.managers.AbstractChatFilter;

import java.util.List;

public class BChatFilter extends AbstractChatFilter {
    @Override
    public void execCommands(List<String> commands, String[] args, Object player) {
        if (commands == null) return;
        PluginManager manager = ProxyServer.getInstance().getPluginManager();
        CommandSender console = ProxyServer.getInstance().getConsole();
        for (String command : commands)
            manager.dispatchCommand(console,
              StringUtils.replaceArgsPlaceholders(StringUtils.colorize(
                command.replace("%player_name%", ((CommandSender) player).getName())), args));
    }
}
