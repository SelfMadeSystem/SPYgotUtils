package uwu.smsgamer.spygotutils.commands.commands.spigot;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import uwu.smsgamer.spygotutils.commands.SmsCommand;
import uwu.smsgamer.spygotutils.config.ConfVal;
import uwu.smsgamer.spygotutils.utils.*;

import java.util.*;

public class SendMsgCommand extends SmsCommand {
    public ConfVal<String> noPlayer = new ConfVal<>("commands.send-message.no-player", "messages",
      "%prefix% &cPlayer &a%arg%&c doesn't exist!");
    public ConfVal<String> success = new ConfVal<>("commands.send-message.success", "messages",
      "%prefix% &rSent message to &a%arg%&r.");

    public SendMsgCommand() {
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
        if (sender.hasPermission(this.permissionBase) && args.length == 0) return null;
        return Collections.emptyList();
    }
}
