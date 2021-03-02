package uwu.smsgamer.spygotutils.managers.chatfilter;

import org.bukkit.configuration.file.YamlConfiguration;
import uwu.smsgamer.spygotutils.config.ConfigManager;
import uwu.smsgamer.spygotutils.config.spigot.SConfigManager;
import uwu.smsgamer.spygotutils.managers.AbstractChatFilter;

import java.util.List;

public class SChatFilter extends AbstractChatFilter {
    public YamlConfiguration config;

    public SChatFilter() {
        reload();
    }

    @Override
    public void reload() {
        this.config = ((SConfigManager) ConfigManager.getInstance()).configs.get("chat-filter");
    }

    @Override
    public Result sendFilter(Object player, String msg, String json) {
        return new Result(false);
    }

    @Override
    public Result chatFilter(Object player, String msg, String[] args) {
        return new Result(false);
    }

    @Override
    public Result commandFilter(Object player, String msg, String label, String[] args) {
        return new Result(false);
    }

    @Override
    public Result tabFilter(Object player, String msg, String label, String[] args, List<String> completions) {
        return new Result(completions);
    }
}
