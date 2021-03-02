package uwu.smsgamer.spygotutils.managers;

import uwu.smsgamer.senapi.utils.StringUtils;

import java.util.List;

public abstract class AbstractChatFilter {
    public abstract boolean sendFilter(Object player, String msg, String json);

    public boolean chatReceive(Object player, String msg) {
        if (msg.startsWith("/")) {
            int i = msg.indexOf(' ');
            return commandFilter(player, msg, msg.substring(0, i), StringUtils.split(msg.substring(i), ' '));
        } else {
            return chatFilter(player, msg, StringUtils.split(msg, ' '));
        }
    }

    public abstract boolean chatFilter(Object player, String msg, String[] args);
    public abstract boolean commandFilter(Object player, String msg, String label, String[] args);
    public abstract List<String> tabFilter(Object player, String msg, String[] args);
}
