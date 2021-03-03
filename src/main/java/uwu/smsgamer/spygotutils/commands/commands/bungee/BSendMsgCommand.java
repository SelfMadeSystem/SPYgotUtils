package uwu.smsgamer.spygotutils.commands.commands.bungee;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import uwu.smsgamer.spygotutils.commands.*;
import uwu.smsgamer.spygotutils.config.ConfVal;
import uwu.smsgamer.spygotutils.utils.*;

import java.util.*;

public class BSendMsgCommand extends BSmsCommand {
    public ConfVal<String> noPlayer = new ConfVal<>("commands.send-message.no-player", "messages",
      "%prefix% &cPlayer &a%arg%&c doesn't exist!");
    public ConfVal<String> success = new ConfVal<>("commands.send-message.success", "messages",
      "%prefix% &rSent message to &a%arg%&r.");

    public BSendMsgCommand() {
        super("bsend-message", true, "bsend-msg", "bsendmsg", "bsendmessage");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (testPermission(sender)) {
            if (args.length < 2) return;
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player == null) {
                BChatUtils.sendMessage(noPlayer.getValue().replace("%arg%", args[0]), sender);
                return;
            }
            BChatUtils.sendMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), player);
            BChatUtils.sendMessage(success.getValue().replace("%arg%", player.getName()), sender);
        }
    }
}
