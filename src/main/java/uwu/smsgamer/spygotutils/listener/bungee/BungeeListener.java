package uwu.smsgamer.spygotutils.listener.bungee;

import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import uwu.smsgamer.spygotutils.managers.*;

public class BungeeListener implements Listener {
    private static BungeeListener instance;

    public static BungeeListener getInstance() {
        if (instance == null) instance = new BungeeListener();
        return instance;
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter.chatReceive(e.getSender(), e.getMessage());
        if (!result.didSomething) return;
        e.setCancelled(result.cancel);
        e.setMessage(result.message);
    }

    @EventHandler
    public void onTab(TabCompleteEvent e) {
        AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter
          .incomingTab(e.getSender(), e.getCursor(), e.getSuggestions());
        if (!result.didSomething) return;
        e.setCancelled(result.cancel);
        e.getSuggestions().clear();
        if (result.completions.isEmpty()) e.setCancelled(true);
        e.getSuggestions().addAll(result.completions);
    }
}
