package uwu.smsgamer.spygotutils.managers;

public class ChatFilterManager {
    private static ChatFilterManager instance;
    public final AbstractChatFilter chatFilter;

    public static ChatFilterManager getInstance() {
        if (instance == null) throw new IllegalStateException("Instance is null!");
        return instance;
    }

    public ChatFilterManager(AbstractChatFilter chatFilter) {
        instance = this;
        this.chatFilter = chatFilter;
        reload();
    }

    public void reload() {
        chatFilter.reload();
    }
}
