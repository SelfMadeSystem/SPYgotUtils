package uwu.smsgamer.spygotutils.managers.chatfilter;

import uwu.smsgamer.spygotutils.managers.AbstractChatFilter;

import java.util.List;

public class BChatFilter extends AbstractChatFilter {
    @Override
    public boolean sendFilter(Object player, String msg, String json) {
        return false;
    }

    @Override
    public boolean chatFilter(Object player, String msg, String[] args) {
        return false;
    }

    @Override
    public boolean commandFilter(Object player, String msg, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> tabFilter(Object player, String msg, String[] args) {
        return null;
    }
}
