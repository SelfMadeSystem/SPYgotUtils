package uwu.smsgamer.spygotutils.commands.commands.spigot;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import uwu.smsgamer.senapi.utils.spigot.SConsolePlayer;
import uwu.smsgamer.senapi.utils.Evaluator;
import uwu.smsgamer.spygotutils.commands.SmsCommand;
import uwu.smsgamer.spygotutils.config.ConfVal;
import uwu.smsgamer.spygotutils.utils.*;

import java.util.*;

public class EvaluateCommand extends SmsCommand {
    public ConfVal<String> success = new ConfVal<>("commands.evaluate.success", "messages", "%prefix% &rEvaluation result: %result%");
    public ConfVal<String> error = new ConfVal<>("commands.evaluate.error", "messages", "%prefix% &rEvaluation error: %msg%");

    public EvaluateCommand() {
        super("evaluate", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (testPermission(sender)) {
            try {
                Evaluator evaluator = EvalUtils.newEvaluator(sender instanceof OfflinePlayer ? (OfflinePlayer) sender : SConsolePlayer.getInstance());
                sender.sendMessage(ChatUtils.toChatString(success.getValue(), sender).replace("%result%", evaluator.eval(String.join(" ", args)).toString()));
            } catch (Exception e) {
                sender.sendMessage(ChatUtils.toChatString(error.getValue(), sender).replace("%msg%", Objects.toString(e.getMessage())));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
