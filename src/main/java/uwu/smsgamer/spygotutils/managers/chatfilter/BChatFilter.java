package uwu.smsgamer.spygotutils.managers.chatfilter;

import net.md_5.bungee.config.Configuration;
import uwu.smsgamer.spygotutils.config.ConfigManager;
import uwu.smsgamer.spygotutils.config.bungee.BConfigManager;
import uwu.smsgamer.spygotutils.managers.AbstractChatFilter;

import java.util.List;

public class BChatFilter extends AbstractChatFilter {
    public Configuration config;

    public BChatFilter() {
        reload();
    }

    @Override
    public void reload() {
        this.config = ((BConfigManager) ConfigManager.getInstance()).configs.get("chat-filter");
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
