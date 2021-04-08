package uwu.smsgamer.spygotutils.commands.commands.spigot;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.python.core.*;
import org.python.util.InteractiveInterpreter;
import uwu.smsgamer.senapi.config.ConfVal;
import uwu.smsgamer.spygotutils.commands.SmsCommand;
import uwu.smsgamer.spygotutils.managers.PlayerShellManager;
import uwu.smsgamer.spygotutils.utils.ChatUtils;

import java.util.*;

public class ShellCommand extends SmsCommand {
    private static ShellCommand INSTANCE;
    public ConfVal<String> disable = new ConfVal<>("commands.pyshell.enable", "messages",
            "%prefix% &cDisabled.");
    public ConfVal<String> enable = new ConfVal<>("commands.pyshell.disable", "messages",
            "%prefix% &aEnabled.");
    public ConfVal<String> reset = new ConfVal<>("commands.pyshell.reset", "messages",
            "%prefix% &rReset.");
    public ConfVal<String> help = new ConfVal<>("commands.pyshell.help", "messages",
            "%prefix% &r/pyshell [toggle | stop, off, disable | begin, start, on, enable | reset | getlines | help]");
    public Map<UUID, Boolean> enabledPlayers = new HashMap<>();

    {
        INSTANCE = this;
    }


    private ShellCommand() {
        super("pyshell");
    }

    public static ShellCommand getInstance() {
        if (INSTANCE == null) new ShellCommand();
        return INSTANCE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!testPermission(sender)) return true;
        Player p = (Player) sender;
        UUID uuid = p.getUniqueId();
        switch (args.length) {
            case 0: {// toggle
                if (get(uuid)) {
                    disable(uuid);
                    ChatUtils.sendMessage(disable, sender);
                } else {
                    enable(uuid);
                    ChatUtils.sendMessage(enable, sender);
                }
                return true;
            }
            case 1: {
                switch (args[0].toLowerCase()) {
                    case "toggle": {
                        if (get(uuid)) {
                            disable(p);
                        } else {
                            enable(p);
                        }
                        return true;
                    }
                    case "begin":
                    case "start":
                    case "on":
                    case "enable": {
                        enable(p);
                        return true;
                    }
                    case "stop":
                    case "off":
                    case "disable": {
                        disable(p);
                        return true;
                    }
                    case "reset": {
                        reset(p);
                        break;
                    }
                    case "getlines": {
                        String lines = PlayerShellManager.getLines(p);
                        p.sendMessage(lines);
                        break;
                    }
                    case "help":
                    default: {
                        ChatUtils.sendMessage(help, sender);
                        return true;
                    }
                }
            }
            default: {
                ChatUtils.sendMessage(help, sender);
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.hasPermission(this.permissionBase)) {
            List<String> completions = new ArrayList<>();
            String toComplete = args[0].toLowerCase(java.util.Locale.ENGLISH);
            for (String str : new String[]{"toggle", "stop", "off", "disable", "begin", "start", "on", "enable", "reset", "getlines", "help"}) {
                if (StringUtil.startsWithIgnoreCase(str, toComplete)) {
                    completions.add(str);
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }

    public void reset(Player player) {
        PlayerShellManager.interpreters.put(player.getUniqueId(), PlayerShellManager.newInterpreter(player));
        ChatUtils.sendMessage(reset, player);
    }

    public void enable(Player player) {
        enable(player.getUniqueId());
        ChatUtils.sendMessage(enable, player);
    }

    public void enable(UUID uuid) {
        enabledPlayers.put(uuid, true);
    }

    public void disable(Player player) {
        disable(player.getUniqueId());
        ChatUtils.sendMessage(disable, player);
    }

    public void disable(UUID uuid) {
        enabledPlayers.put(uuid, false);
    }

    public boolean get(UUID uuid) {
        @Nullable
        Boolean b = enabledPlayers.get(uuid);
        return b != null && b;
    }

    public void interpret(Player player, String cmd) {
        cmd = cmd.replace("\\ ", " ");
        InteractiveInterpreter interpreter = PlayerShellManager.getInterpreter(player).interpreter;
        if (cmd.equals("\\")) {
            cmd = interpreter.buffer.toString();
            player.sendTitle("", "Finished command.", 10, 60, 10);
            interpreter.resetbuffer();
        } else player.sendMessage("> " + cmd);
        boolean b = false;
        try {
            b = PlayerShellManager.interpret(player, cmd);
        } catch (PyException e) {
            if (e.match(Py.SystemExit)) {
                reset(player);
                disable(player);
            } else {
                interpreter.showexception(e);
            }
        }
        if (b) {
            player.sendTitle("", "Unfinished command.", 10, 60, 10);
            interpreter.buffer.append(cmd).append('\n');
        } else {
            interpreter.resetbuffer();
        }
    }
}
