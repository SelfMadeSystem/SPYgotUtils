package uwu.smsgamer.spygotutils.commands.commands.bungee;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import uwu.smsgamer.spygotutils.commands.SmsCommand;
import uwu.smsgamer.spygotutils.config.ConfVal;
import uwu.smsgamer.spygotutils.utils.ChatUtils;

import java.util.*;

public class BSendMsgCommand extends SmsCommand {
    public ConfVal<String> noPlayer = new ConfVal<>("commands.send-msg.noPlayer", "messages",
      "%prefix% &cPlayer &a%arg%&c doesn't exist!");
    public ConfVal<String> success = new ConfVal<>("commands.send-msg.success", "messages",
      "%prefix% &rSent message to &a%arg%&r.");

    public BSendMsgCommand() {
        super("send-message", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (testPermission(sender)) {
            if (args.length < 2) return false;
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                ChatUtils.sendMessage(noPlayer.getValue().replace("%arg%", args[0]), sender);
                return true;
            }
            ChatUtils.sendMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), player);
            ChatUtils.sendMessage(success.getValue().replace("%arg%", player.getName()), sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
