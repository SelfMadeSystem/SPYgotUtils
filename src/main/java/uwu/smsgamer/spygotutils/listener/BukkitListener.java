package uwu.smsgamer.spygotutils.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import uwu.smsgamer.spygotutils.managers.ChatFilterManager;

public class BukkitListener implements Listener {
    private static BukkitListener instance;

    public static BukkitListener getInstance() {
        if (instance == null) instance = new BukkitListener();
        return instance;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        ChatFilterManager.getInstance().commandReceiveEvent(e);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        ChatFilterManager.getInstance().chatReceiveEvent(e);
    }

    @EventHandler
    public void onTab(TabCompleteEvent e) {
        if (e.getSender() instanceof Player) ChatFilterManager.getInstance().tabReceiveEvent(e);
    }
}
