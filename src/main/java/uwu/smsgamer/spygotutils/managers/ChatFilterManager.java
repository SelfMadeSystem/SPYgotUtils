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
import uwu.smsgamer.spygotutils.utils.*;

import java.util.List;

public class ChatFilterManager {
    private static ChatFilterManager instance;
    private YamlConfiguration conf;

    public static ChatFilterManager getInstance() {
        if (instance == null) instance = new ChatFilterManager();
        return instance;
    }

    public ChatFilterManager() {
        instance = this;
        conf = ConfigManager.getConfig("chat-filter");
    }

    public void reload() {
        conf = ConfigManager.getConfig("chat-filter");
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
                String exec = section.getString("exec");
                if (exec != null && !exec.isEmpty())
                    evaluator.exec(exec);
                PyObject result = evaluator.eval(section.getString("check"));
                if (result.getClass().equals(PyBoolean.class)) {
                    if (((PyBoolean) result).getBooleanValue()) {
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
                String exec = section.getString("exec");
                if (exec != null && !exec.isEmpty())
                    evaluator.exec(exec);
                String check = section.getString("check");
                check = StringUtils.replaceArgsPlaceholders(check, args);
                PyObject result = evaluator.eval(check);
                if (result.getClass().equals(PyBoolean.class)) {
                    if (((PyBoolean) result).getBooleanValue()) {
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
                String exec = section.getString("exec");
                if (exec != null && !exec.isEmpty())
                    evaluator.exec(exec);
                String check = section.getString("check");
                check = StringUtils.replaceArgsPlaceholders(check, args);
                PyObject result = evaluator.eval(check);
                if (result.getClass().equals(PyBoolean.class)) {
                    if (((PyBoolean) result).getBooleanValue()) {
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
        for (String key : conf.getConfigurationSection("incoming-tab").getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection("incoming-tab." + key);
            try {
                String exec = section.getString("exec");
                if (exec != null && !exec.isEmpty())
                    evaluator.exec(exec);
                String check = section.getString("check");
                check = StringUtils.replaceArgsPlaceholders(check, args);
                PyObject result = evaluator.eval(check);
                if (result.getClass().equals(PyBoolean.class)) {
                    if (((PyBoolean) result).getBooleanValue()) {
                        if (section.getBoolean("cancel")) e.setCancelled(true);
                        else {
                            if (section.contains("replacement"))
                                e.setCompletions(section.getStringList("replacement"));
                        }
                    }
                    execCmd(section.getStringList("execute-commands"), args, p);
                } else {
                    System.out.println(key + ":" + result.getClass());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void execCmd(List<String> commands, String[] args, CommandSender player) {
        if (commands != null)
            for (String s : commands) ChatUtils.execCmd(StringUtils.replaceArgsPlaceholders(s, args), player);
    }
}
