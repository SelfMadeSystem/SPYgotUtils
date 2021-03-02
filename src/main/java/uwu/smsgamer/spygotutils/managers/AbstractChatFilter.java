package uwu.smsgamer.spygotutils.managers;

import uwu.smsgamer.senapi.utils.*;
import uwu.smsgamer.spygotutils.utils.EvalUtils;

import java.util.List;

public abstract class AbstractChatFilter {
    public abstract Result sendFilter(Object player, String msg, String json);

    public Result chatReceive(Object player, String msg) {
        if (msg.startsWith("/")) {
            int i = msg.indexOf(' ');
            return commandFilter(player, msg, msg.substring(0, i), StringUtils.split(msg.substring(i), ' '));
        } else {
            return chatFilter(player, msg, StringUtils.split(msg, ' '));
        }
    }

    public abstract Result chatFilter(Object player, String msg, String[] args);
    public abstract Result commandFilter(Object player, String msg, String label, String[] args);
    public abstract Result tabFilter(Object player, String msg, String label, String[] args, List<String> completions);

    public static Evaluator newEvaluator(Object player, String msg, String label, String[] args) {
        Evaluator evaluator = EvalUtils.newEvaluator(player);
        evaluator.set("msg", msg);
        if (label != null) evaluator.set("label", label);
        if (args != null) evaluator.set("args", args);
        return evaluator;
    }

    public static class Result {
        public boolean cancel;
        public String message;
        public boolean isJson;
        public List<String> completions;

        public Result(boolean cancel) {
            this.cancel = cancel;
        }

        public Result(String message) {
            this.message = message;
        }

        public Result(String message, boolean isJson) {
            this.message = message;
            this.isJson = isJson;
        }

        public Result(List<String> completions) {
            this.completions = completions;
        }
    }
}
