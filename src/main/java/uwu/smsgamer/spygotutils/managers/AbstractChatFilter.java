package uwu.smsgamer.spygotutils.managers;

import de.leonhard.storage.Yaml;
import de.leonhard.storage.sections.FlatFileSection;
import org.python.core.*;
import uwu.smsgamer.senapi.utils.*;
import uwu.smsgamer.spygotutils.config.ConfigManager;
import uwu.smsgamer.spygotutils.utils.EvalUtils;

import java.util.List;
import java.util.stream.*;

public abstract class AbstractChatFilter {
    public Yaml config;

    public AbstractChatFilter() {
        reload();
        System.out.println(config.singleLayerKeySet());
    }

    public void reload() {
        this.config = ConfigManager.getInstance().getConfig("chat-filter");
    }

    public Iterable<String> keys(String type) {
        FlatFileSection section = config.getSection(type);
        return section.singleLayerKeySet();
    }

    public String preExec(String type, String key) {
        return config.getString(type + "." + key + ".pre-exec");
    }

    public String check(String type, String key) {
        return config.getString(type + "." + key + ".check");
    }

    public String postExec(String type, String key) {
        return config.getString(type + "." + key + ".post-exec");
    }

    public boolean cancel(String type, String key) {
        return config.getBoolean(type + "." + key + ".cancel");
    }

    public boolean isJson(String type, String key) {
        return config.getBoolean(type + "." + key + ".is-json");
    }

    public int weight(String type, String key) {
        return config.getInt(type + "." + key + "." + "weight");
    }

    public String replacement(String type, String key) {
        return config.get(type + "." + key + ".replacement", null);
    }

    public Object tabReplacement(String type, String key) {
        return config.get(type + "." + key + ".replacement");
    }

    public List<String> commands(String type, String key) {
        return config.getStringList(type + "." + key + ".execute-commands");
    }

    public Result chatReceive(Object player, String msg) {
        if (msg.startsWith("/")) {
            int i = msg.indexOf(' ');
            return incomingCommand(player, msg, msg.substring(0, i == -1 ? msg.length() : i),
              StringUtils.split(msg.substring(i + 1), ' '));
        } else {
            return incomingChat(player, msg, StringUtils.split(msg, ' '));
        }
    }

    public Result outgoingChat(Object player, String msg, String json) {
        String type = "outgoing-chat";
        Result result = new Result(json == null ? msg : json, true);
        Evaluator evaluator = newEvaluator(player, msg, null, null);
        if (json != null) evaluator.set("json", json);

        for (String key : keys(type)) {
            preExec(evaluator, preExec(type, key));

            if (check(evaluator, check(type, key), type + "." + key)) {
                int weight = weight(type, key);
                if (weight > result.weight) {
                    result.didSomething = true;
                    result.cancel = cancel(type, key);
                    result.weight = weight;

                    result.isJson = isJson(type, key);

                    String r = evalToString(evaluator, replacement(type, key));
                    if (r != null) result.message = r;
                }

                execCommands(commands(type, key), new String[0], player);
            }

            postExec(evaluator, result, postExec(type, key));
        }

        return result;
    }

    public Result incomingCommand(Object player, String msg, String label, String[] args) {
        String type = "incoming-command";
        Result result = new Result(msg);
        Evaluator evaluator = newEvaluator(player, msg, label, args);

        for (String key : keys(type)) {
            preExec(evaluator, preExec(type, key));

            if (check(evaluator, check(type, key), type + "." + key)) {
                int weight = weight(type, key);
                if (weight > result.weight) {
                    result.didSomething = true;
                    result.cancel = cancel(type, key);
                    result.weight = weight;

                    String r = evalToString(evaluator, replacement(type, key));
                    if (r != null) result.message = r;
                }

                execCommands(commands(type, key), args, player);
            }

            postExec(evaluator, result, postExec(type, key));
        }

        return result;
    }

    public Result incomingChat(Object player, String msg, String[] args) {
        String type = "incoming-chat";
        Result result = new Result(msg);
        Evaluator evaluator = newEvaluator(player, msg, args[0], args);

        for (String key : keys(type)) {
            preExec(evaluator, preExec(type, key));

            if (check(evaluator, check(type, key), type + "." + key)) {
                int weight = weight(type, key);
                if (weight > result.weight) {
                    result.didSomething = true;
                    result.cancel = cancel(type, key);
                    result.weight = weight;

                    String r = evalToString(evaluator, replacement(type, key));
                    if (r != null) result.message = r;
                }

                execCommands(commands(type, key), args, player);
            }

            postExec(evaluator, result, postExec(type, key));
        }

        return result;
    }

    public Result incomingTab(Object player, String msg, List<String> completions) {
        int i = msg.indexOf(' ');
        String label = msg.substring(0, i == -1 ? msg.length() : i);
        String[] args = StringUtils.split(msg.substring(i + 1), ' ');

        String type = "incoming-tab";
        Result result = new Result(completions);
        Evaluator evaluator = newEvaluator(player, msg, label, args);
        evaluator.set("completions", completions);

        for (String key : keys(type)) {
            preExec(evaluator, preExec(type, key));

            if (check(evaluator, check(type, key), type + "." + key)) {
                int weight = weight(type, key);
                if (weight > result.weight) {
                    result.didSomething = true;
                    result.cancel = cancel(type, key);
                    result.weight = weight;


                    Object replacement = tabReplacement(type, key);

                    if (replacement == null) {
                        if (!result.cancel) result.completions = null;
                    } else if (replacement.getClass().equals(String.class)) {
                        List<String> strings = evalToList(evaluator, replacement.toString());
                        System.out.println(strings);
                        result.completions = strings;
                    } else if (replacement instanceof List) {
                        result.completions = ((List<?>) replacement).stream().map(Object::toString).collect(Collectors.toList());
                    }
                }

                execCommands(commands(type, key), args, player);
            }

            postExec(evaluator, result, postExec(type, key));
        }

        return result;
    }

    public static Evaluator newEvaluator(Object player, String msg, String label, String[] args) {
        Evaluator evaluator = EvalUtils.newEvaluator(player);
        evaluator.set("msg", msg);
        if (label != null) evaluator.set("label", label);
        if (args != null) evaluator.set("args", args);
        return evaluator;
    }

    public void preExec(Evaluator evaluator, String exec) {
        try {
            if (exec != null && !exec.isEmpty()) evaluator.exec(exec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean check(Evaluator evaluator, String eval, String place) {
        try {
            PyObject result = evaluator.eval(eval);
            if (result instanceof PyBoolean) return ((PyBoolean) result).getBooleanValue();
            System.err.println(place + " returns a " + result.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void postExec(Evaluator evaluator, Result result, String exec) {
        try {
            evaluator.set("result", result);
            if (exec != null && !exec.isEmpty()) evaluator.exec(exec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String evalToString(Evaluator evaluator, String eval) {
        if (eval == null) return null;
        try {
            return evaluator.eval(eval).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> evalToList(Evaluator evaluator, String eval) {
        if (eval == null) return null;
        try {
            PyObject result = evaluator.eval(eval);
            if (result.isSequenceType())
                return StreamSupport.stream(result.asIterable().spliterator(), false)
                  .map(Object::toString).collect(Collectors.toList());
            System.err.println("Result is of type: " + result.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract void execCommands(List<String> commands, String[] args, Object player);

    public static class Result {
        public boolean didSomething;
        public int weight = -1;
        public boolean cancel;
        public String message;
        public boolean isJson;
        public List<String> completions;

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


        public String toString() {
            return "Result{" +
              "didSomething=" + didSomething +
              ", weight=" + weight +
              ", cancel=" + cancel +
              ", message='" + message + '\'' +
              ", isJson=" + isJson +
              ", completions=" + completions +
              '}';
        }
    }
}
