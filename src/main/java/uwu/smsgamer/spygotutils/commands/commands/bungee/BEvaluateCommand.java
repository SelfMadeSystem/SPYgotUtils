package uwu.smsgamer.spygotutils.commands.commands.bungee;

import net.md_5.bungee.api.CommandSender;
import uwu.smsgamer.senapi.utils.Evaluator;
import uwu.smsgamer.spygotutils.commands.BSmsCommand;
import uwu.smsgamer.spygotutils.config.ConfVal;
import uwu.smsgamer.spygotutils.utils.*;

import java.util.Objects;

public class BEvaluateCommand extends BSmsCommand {
    public ConfVal<String> success = new ConfVal<>("commands.evaluate.success", "messages", "%prefix% &rEvaluation result: %result%");
    public ConfVal<String> error = new ConfVal<>("commands.evaluate.error", "messages", "%prefix% &rEvaluation error: %msg%");

    public BEvaluateCommand() {
        super("evaluate", true, "eval");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (testPermission(sender)) {
            try {
                Evaluator evaluator = EvalUtils.newEvaluator(sender);
                sender.sendMessage(BChatUtils.toChatString(success.getValue(), sender).replace("%result%", evaluator.eval(String.join(" ", args)).toString()));
            } catch (Exception e) {
                sender.sendMessage(BChatUtils.toChatString(error.getValue(), sender).replace("%msg%", Objects.toString(e.getMessage())));
            }
        }
    }
}
