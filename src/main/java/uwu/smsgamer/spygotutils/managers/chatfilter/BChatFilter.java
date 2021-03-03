package uwu.smsgamer.spygotutils.managers.chatfilter;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import uwu.smsgamer.senapi.utils.StringUtils;
import uwu.smsgamer.spygotutils.config.ConfigManager;
import uwu.smsgamer.spygotutils.config.bungee.BConfigManager;
import uwu.smsgamer.spygotutils.managers.AbstractChatFilter;

import java.util.*;

public class BChatFilter extends AbstractChatFilter {
    public Configuration config;

    public BChatFilter() {
        reload();
    }

    @Override
    public void reload() {
        this.config = ((BConfigManager) ConfigManager.getInstance()).configs.get("chat-filter");
    }

    private Configuration getSection(String type, String key) {
        return config.getSection(type).getSection(key);
    }

    @Override
    public Iterable<String> keys(String type) {
        Configuration section = config.getSection(type);
        if (section == null) return Collections.emptyList();
        return section.getKeys();
    }

    @Override
    public String preExec(String type, String key) {
        return getSection(type, key).getString("pre-exec", null);
    }

    @Override
    public String check(String type, String key) {
        return getSection(type, key).getString("check", null);
    }

    @Override
    public String postExec(String type, String key) {
        return getSection(type, key).getString("post-exec", null);
    }

    @Override
    public boolean cancel(String type, String key) {
        return getSection(type, key).getBoolean("cancel");
    }

    @Override
    public boolean isJson(String type, String key) {
        return getSection(type, key).getBoolean("is-json");
    }

    @Override
    public int weight(String type, String key) {
        return getSection(type, key).getInt("weight");
    }

    @Override
    public String replacement(String type, String key) {
        return getSection(type, key).getString("replacement", null);
    }

    @Override
    public Object tabReplacement(String type, String key) {
        return getSection(type, key).get("replacement");
    }

    @Override
    public List<String> commands(String type, String key) {
        return getSection(type, key).getStringList("execute-commands");
    }

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
