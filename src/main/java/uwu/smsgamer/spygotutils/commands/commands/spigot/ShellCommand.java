package uwu.smsgamer.spygotutils.commands.commands.spigot;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.python.util.InteractiveInterpreter;
import uwu.smsgamer.senapi.config.ConfVal;
import uwu.smsgamer.spygotutils.commands.SmsCommand;
import uwu.smsgamer.spygotutils.managers.PlayerShellManager;
import uwu.smsgamer.spygotutils.utils.ChatUtils;

import java.util.*;

public class ShellCommand extends SmsCommand {
    public ConfVal<String> disable = new ConfVal<>("commands.pyshell.enable", "messages",
            "%prefix% &cDisabled.");
    public ConfVal<String> enable = new ConfVal<>("commands.pyshell.disable", "messages",
            "%prefix% &aEnabled.");
    public ConfVal<String> reset = new ConfVal<>("commands.pyshell.reset", "messages",
            "%prefix% &rReset.");
    public ConfVal<String> help = new ConfVal<>("commands.pyshell.help", "messages",
            "%prefix% &r/pyshell [toggle|stop, off, disable|begin, start, on, enable|reset|help]");
    private static ShellCommand INSTANCE;
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
                            disable(uuid);
                            ChatUtils.sendMessage(disable, sender);
                        } else {
                            enable(uuid);
                            ChatUtils.sendMessage(enable, sender);
                        }
                        return true;
                    }
                    case "begin":
                    case "start":
                    case "on":
                    case "enable": {
                        enable(uuid);
                        ChatUtils.sendMessage(enable, sender);
                        return true;
                    }
                    case "stop":
                    case "off":
                    case "disable": {
                        disable(uuid);
                        ChatUtils.sendMessage(disable, sender);
                        return true;
                    }
                    case "reset": {
                        PlayerShellManager.interpreters.put(uuid, PlayerShellManager.newInterpreter(p));
                        ChatUtils.sendMessage(reset, sender);
                        break;
                    }
                    case "help":
                    default: {
                        ChatUtils.sendMessage(help, sender);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void enable(UUID uuid) {
        enabledPlayers.put(uuid, true);
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
        if (PlayerShellManager.interpret(player, cmd)) {
            player.sendTitle("", "Unfinished command.", 10, 60, 10);
            interpreter.buffer.append(cmd).append('\n');
        } else {
            interpreter.resetbuffer();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
