package uwu.smsgamer.spygotutils.managers;

import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.out.chat.WrappedPacketOutChat;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.python.core.*;
import uwu.smsgamer.senapi.utils.*;
import uwu.smsgamer.spygotutils.config.ConfigManager;
import uwu.smsgamer.spygotutils.config.spigot.SConfigManager;
import uwu.smsgamer.spygotutils.utils.*;

import java.util.*;
import java.util.stream.*;

public class ChatFilterManager { // TODO: 2021-02-21 CLEAN THIS SHIT UP (so much stuff can be reused as funcs)
    private static ChatFilterManager instance;
    private YamlConfiguration conf;

    public static ChatFilterManager getInstance() {
        if (instance == null) instance = new ChatFilterManager();
        return instance;
    }

    public ChatFilterManager() {
        instance = this;
        reload();
    }

    public void reload() {
        conf = ((SConfigManager) ConfigManager.getInstance()).getConfig("chat-filter");
    }

    public void packetSendEvent(PacketPlaySendEvent e) {
        if (!conf.contains("outgoing-chat")) return;

        WrappedPacketOutChat chat = new WrappedPacketOutChat(e.getNMSPacket());
        String msg = chat.getMessage();

        Evaluator evaluator = EvalUtils.newEvaluator(e.getPlayer());
        evaluator.set("msg", msg);
        for (String key : conf.getConfigurationSection("outgoing-chat").getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection("outgoing-chat." + key);
            try {
                preExec(evaluator, section);
                PyObject result = evaluator.eval(section.getString("check"));
                boolean checkResult = false;
                if (result.getClass().equals(PyBoolean.class)) {
                    checkResult = ((PyBoolean) result).getBooleanValue();
                    if (checkResult) {
                        if (section.getBoolean("cancel")) e.setCancelled(true);
                        else {
                            String s = evaluator.eval(section.getString("replacement")).toString();
                            s = ChatUtils.toChatString(s, e.getPlayer());
                            WrappedPacketOutChat newChat = new WrappedPacketOutChat(
                              s, WrappedPacketOutChat.ChatPosition.CHAT,
                              e.getPlayer().getUniqueId(), false);
                            e.setNMSPacket(new NMSPacket(newChat.asNMSPacket()));
                        }
                        ChatUtils.execCmd(section.getStringList("execute-commands"), e.getPlayer());
                    }
                } else {
                    System.out.println(key + ":" + result.getClass());
                }
                postExec(evaluator, section, checkResult);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void commandReceiveEvent(PlayerCommandPreprocessEvent e) {
        if (!conf.contains("incoming-command")) return;

        String message = e.getMessage();
        String[] args = message.substring(message.indexOf(" ") + 1).split(" ");
        Evaluator evaluator = EvalUtils.newEvaluator(e.getPlayer());
        evaluator.set("msg", message);
        int indOf = message.indexOf(" ");
        evaluator.set("label", message.substring(0, indOf < 0 ? message.length() : indOf));
        evaluator.set("name", e.getPlayer().getName());
        evaluator.set("args", args);
        for (String key : conf.getConfigurationSection("incoming-command").getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection("incoming-command." + key);
            try {
                preExec(evaluator, section);
                String check = section.getString("check");
                check = StringUtils.replaceArgsPlaceholders(check, args);
                PyObject result = evaluator.eval(check);
                boolean checkResult = false;
                if (result.getClass().equals(PyBoolean.class)) {
                    checkResult = ((PyBoolean) result).getBooleanValue();
                    if (checkResult) {
                        if (section.getBoolean("cancel")) e.setCancelled(true);
                        else {
                            String s = evaluator.eval(section.getString("replacement")).toString();
                            e.setMessage(s);
                        }
                        execCmd(section.getStringList("execute-commands"), args, e.getPlayer());
                    }
                } else {
                    System.out.println(key + ":" + result.getClass());
                }
                postExec(evaluator, section, checkResult);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void chatReceiveEvent(AsyncPlayerChatEvent e) {
        if (!conf.contains("incoming-chat")) return;

        String message = e.getMessage();
        String[] args = message.substring(message.indexOf(" ") + 1).split(" ");
        Evaluator evaluator = EvalUtils.newEvaluator(e.getPlayer());
        evaluator.set("msg", message);
        int indOf = message.indexOf(" ");
        evaluator.set("label", message.substring(0, indOf < 0 ? message.length() : indOf));
        evaluator.set("name", e.getPlayer().getName());
        evaluator.set("args", args);
        for (String key : conf.getConfigurationSection("incoming-chat").getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection("incoming-chat." + key);
            try {
                preExec(evaluator, section);
                String check = section.getString("check");
                check = StringUtils.replaceArgsPlaceholders(check, args);
                PyObject result = evaluator.eval(check);
                boolean checkResult = false;
                if (result.getClass().equals(PyBoolean.class)) {
                    checkResult = ((PyBoolean) result).getBooleanValue();
                    if (checkResult) {
                        if (section.getBoolean("cancel")) e.setCancelled(true);
                        else {
                            String s = evaluator.eval(section.getString("replacement")).toString();
                            e.setMessage(s);
                        }
                    }
                    execCmd(section.getStringList("execute-commands"), args, e.getPlayer());
                } else {
                    System.out.println(key + ":" + result.getClass());
                }
                postExec(evaluator, section, checkResult);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void tabReceiveEvent(TabCompleteEvent e) {
        if (!conf.contains("incoming-tab")) return;

        Player p = (Player) e.getSender();
        String message = e.getBuffer();
        String[] args = message.substring(message.indexOf(" ") + 1).split(" ");
        Evaluator evaluator = EvalUtils.newEvaluator(p);
        evaluator.set("msg", message);
        int indOf = message.indexOf(" ");
        evaluator.set("label", message.substring(0, indOf < 0 ? message.length() : indOf));
        evaluator.set("name", p.getName());
        evaluator.set("args", args);
        evaluator.set("completions", e.getCompletions());
        for (String key : conf.getConfigurationSection("incoming-tab").getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection("incoming-tab." + key);
            try {
                preExec(evaluator, section);
                String check = section.getString("check");
                check = StringUtils.replaceArgsPlaceholders(check, args);
                PyObject result = evaluator.eval(check);
                boolean checkResult = false;
                if (result.getClass().equals(PyBoolean.class)) {
                    checkResult = ((PyBoolean) result).getBooleanValue();
                    if (checkResult) {
                        if (section.getBoolean("cancel")) e.setCancelled(true);
                        else {
                            Object replacement = section.get("replacement");
                            List<String> replacements = new ArrayList<>();
                            if (replacement instanceof List)
                                replacements = ((List<?>) replacement).stream().map(Object::toString).collect(Collectors.toList());
                            else if (replacement instanceof String) {
                                PyObject rResult = evaluator.eval((String) replacement);
                                if (rResult.isSequenceType()) {
                                    replacements = StreamSupport.stream(rResult.asIterable().spliterator(), false)
                                      .map(Object::toString).collect(Collectors.toList());
                                }
                            }
                            e.setCompletions(replacements);
                        }
                    }
                    execCmd(section.getStringList("execute-commands"), args, p);
                } else {
                    System.out.println(key + ":" + result.getClass());
                }
                postExec(evaluator, section, checkResult);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void preExec(Evaluator evaluator, ConfigurationSection section) {
        String preExec = section.getString("pre-exec");
        if (preExec != null && !preExec.isEmpty())
            evaluator.exec(preExec);
    }

    private void postExec(Evaluator evaluator, ConfigurationSection section, boolean checkResult) {
        String postExec = section.getString("post-exec");
        if (postExec != null && !postExec.isEmpty()) {
            evaluator.set("check", checkResult);
            evaluator.exec(postExec);
        }
    }

    public static void execCmd(List<String> commands, String[] args, CommandSender player) {
        if (commands != null)
            for (String s : commands) ChatUtils.execCmd(StringUtils.replaceArgsPlaceholders(s, args), player);
    }
}
