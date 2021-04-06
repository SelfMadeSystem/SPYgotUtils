package uwu.smsgamer.spygotutils.commands.commands.spigot;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.python.util.InteractiveInterpreter;
import uwu.smsgamer.spygotutils.commands.SmsCommand;
import uwu.smsgamer.spygotutils.managers.PlayerShellManager;

import java.util.*;

public class ShellCommand extends SmsCommand {
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
                    sender.sendMessage("Disabled.");
                } else {
                    enable(uuid);
                    sender.sendMessage("Enabled.");
                }
                return true;
            }
            case 1: {
                switch (args[0].toLowerCase()) {
                    case "toggle": {
                        if (get(uuid)) {
                            disable(uuid);
                            sender.sendMessage("Disabled.");
                        } else {
                            enable(uuid);
                            sender.sendMessage("Enabled.");
                        }
                        return true;
                    }
                    case "begin":
                    case "start":
                    case "on":
                    case "enable": {
                        enable(uuid);
                        sender.sendMessage("Enabled.");
                        return true;
                    }
                    case "reset": {
                        PlayerShellManager.interpreters.put(uuid, PlayerShellManager.newInterpreter(p));
                    }
                    case "stop":
                    case "off":
                    case "disable": {
                        disable(uuid);
                        sender.sendMessage("Disabled.");
                        return true;
                    }
                    case "help":
                    default: {
                        sender.sendMessage("toggle | enable | disable");
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
            interpreter.resetbuffer();
        }
        if (PlayerShellManager.interpret(player, cmd)) {
            player.sendMessage("> " + cmd);
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
