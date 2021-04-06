package uwu.smsgamer.spygotutils.listener.spigot;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import uwu.smsgamer.spygotutils.commands.commands.spigot.ShellCommand;
import uwu.smsgamer.spygotutils.managers.*;

public class BukkitListener implements Listener {
    private static BukkitListener instance;

    public static BukkitListener getInstance() {
        if (instance == null) instance = new BukkitListener();
        return instance;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter.chatReceive(e.getPlayer(), e.getMessage());
        if (!result.didSomething) return;
        e.setCancelled(result.cancel);
        e.setMessage(result.message);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter.chatReceive(e.getPlayer(), e.getMessage());
        if (ShellCommand.getInstance().get(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            ShellCommand.getInstance().interpret(e.getPlayer(), e.getMessage());
            return;
        }
        if (!result.didSomething) return;
        e.setCancelled(result.cancel);
        e.setMessage(result.message);
    }

    @EventHandler
    public void onTab(TabCompleteEvent e) {
        if (!(e.getSender() instanceof Player)) return;
        AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter
          .incomingTab(e.getSender(), e.getBuffer(), e.getCompletions());
        if (!result.didSomething) return;
        e.setCancelled(result.cancel);
        e.setCompletions(result.completions);
    }
}
