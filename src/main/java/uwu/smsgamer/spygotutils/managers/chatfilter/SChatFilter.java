package uwu.smsgamer.spygotutils.managers.chatfilter;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import uwu.smsgamer.senapi.utils.StringUtils;
import uwu.smsgamer.spygotutils.config.ConfigManager;
import uwu.smsgamer.spygotutils.config.spigot.SConfigManager;
import uwu.smsgamer.spygotutils.managers.AbstractChatFilter;

import java.util.*;

public class SChatFilter extends AbstractChatFilter {
    public YamlConfiguration config;

    public SChatFilter() {
        reload();
    }

    @Override
    public void reload() {
        this.config = ((SConfigManager) ConfigManager.getInstance()).configs.get("chat-filter");
    }


    private ConfigurationSection getSection(String type, String key) {
        return config.getConfigurationSection(type).getConfigurationSection(key);
    }

    @Override
    public Iterable<String> keys(String type) {
        ConfigurationSection section = config.getConfigurationSection(type);
        if (section == null) return Collections.emptyList();
        return section.getKeys(false);
    }

    @Override
    public String preExec(String type, String key) {
        return getSection(type, key).getString("pre-exec");
    }

    @Override
    public String check(String type, String key) {
        return getSection(type, key).getString("check");
    }

    @Override
    public String postExec(String type, String key) {
        return getSection(type, key).getString("post-exec");
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
        return getSection(type, key).getString("replacement");
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
    public void execCommands(List<String> commands, String[] args) { // todo player name & papi
        if (commands == null) return;
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        for (String command : commands)
            Bukkit.dispatchCommand(sender,
              StringUtils.replaceArgsPlaceholders(StringUtils.colorize(command), args));
    }
}
