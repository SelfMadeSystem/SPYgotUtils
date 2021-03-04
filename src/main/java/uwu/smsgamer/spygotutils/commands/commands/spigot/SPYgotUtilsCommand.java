package uwu.smsgamer.spygotutils.commands.commands.spigot;

import de.leonhard.storage.Config;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.smsgamer.spygotutils.SpigotLoader;
import uwu.smsgamer.spygotutils.commands.SmsCommand;
import uwu.smsgamer.spygotutils.config.*;
import uwu.smsgamer.spygotutils.managers.*;
import uwu.smsgamer.spygotutils.utils.ChatUtils;

import java.util.*;

public class SPYgotUtilsCommand extends SmsCommand {
    public ConfVal<String> noPermissionReload = new ConfVal<>("commands.spygotutils.no-permission-reload", "messages",
      "%prefix% &cYou do not have permission to execute this command!");
    public ConfVal<String> noPermissionVersion = new ConfVal<>("commands.spygotutils.no-permission-version", "messages",
      "%prefix% &cYou do not have permission to execute this command!");
    public ConfVal<String> usage = new ConfVal<>("commands.spygotutils.usage", "messages",
      "%prefix% &rUsage: /%label% <version/reload> [...]");
    public ConfVal<String> reloading = new ConfVal<>("commands.spygotutils.reloading", "messages",
      "%prefix% &rReloading...");
    public ConfVal<String> reloadSuccess = new ConfVal<>("commands.spygotutils.reload-success", "messages",
      "%prefix% &rReload success! Time took: %ms%ms");
    public ConfVal<String> reloadFail = new ConfVal<>("commands.spygotutils.reload-fail", "messages",
      "%prefix% &cReload failed! Please check console for more information. Time took: %ms%ms");
    public ConfVal<String> version = new ConfVal<>("commands.spygotutils.version", "messages",
      "%prefix% &rPlugin version is %plugin-ver%. Server version is %server-ver%. Bukkit version is %bukkit-ver%.");

    public SPYgotUtilsCommand() {
        super("spygotutils", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (testPermission(sender)) {
            if (args.length == 0) {
                ChatUtils.sendMessage(usage.getValue().replace("%label%", label), sender);
            } else {
                switch (args[0].toLowerCase()) {
                    case "reload":
                        if (testPermission(sender, noPermissionReload, "reload")) {
                            ChatUtils.sendMessage(reloading, sender);
                            long begin = System.currentTimeMillis();
                            boolean success = true;
                            for (Map.Entry<String, Config> s : ConfigManager.getInstance().getConfigs()) {
                                try {
                                    ConfigManager.getInstance().reloadConfig(s.getKey());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    success = false;
                                }
                            }
                            for (ConfVal<?> val : ConfigManager.getInstance().vals)
                                ConfigManager.getInstance().reloadConfVal(val);
                            ChatFilterManager.getInstance().reload();
                            PythonManager.onReload();
                            PythonManager.loadScripts(false);
                            long end = System.currentTimeMillis();
                            ChatUtils.sendMessage((success ? reloadSuccess : reloadFail).getValue()
                              .replace("%ms%", String.valueOf(end - begin)), sender);
                        }
                        break;
                    case "version":
                        if (testPermission(sender, noPermissionVersion, "version")) {
                            JavaPlugin plugin = SpigotLoader.getInstance();
                            ChatUtils.sendMessage(version.getValue()
                              .replace("%plugin-ver%", plugin.getDescription().getVersion())
                              .replace("%server-ver%", plugin.getServer().getVersion())
                              .replace("%bukkit-ver%", plugin.getServer().getBukkitVersion()), sender);
                        }
                        break;
                    default:
                        ChatUtils.sendMessage(usage.getValue().replace("%label%", label), sender);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
