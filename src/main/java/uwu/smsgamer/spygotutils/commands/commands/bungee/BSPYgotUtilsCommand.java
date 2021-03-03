package uwu.smsgamer.spygotutils.commands.commands.bungee;

import de.leonhard.storage.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import uwu.smsgamer.spygotutils.BungeeLoader;
import uwu.smsgamer.spygotutils.commands.BSmsCommand;
import uwu.smsgamer.spygotutils.config.*;
import uwu.smsgamer.spygotutils.managers.*;
import uwu.smsgamer.spygotutils.utils.BChatUtils;

import java.util.Map;

public class BSPYgotUtilsCommand extends BSmsCommand {
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
      "%prefix% &rPlugin version is %plugin-ver%. Server version is %proxy-ver%.");

    public BSPYgotUtilsCommand() {
        super("bspygotutils", true, "bspygot_utils", "bspyutils", "bspyu", "bpygot_utils", "bpygotutils", "bpyutils", "bpyu", "bpython-utils", "bpythonutils", "bpythonu");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (testPermission(sender)) {
            if (args.length == 0) {
                BChatUtils.sendMessage(usage.getValue().replace("%label%", "bspygotutils"), sender);
            } else {
                switch (args[0].toLowerCase()) {
                    case "reload":
                        if (testPermission(sender, noPermissionReload, "reload")) {
                            BChatUtils.sendMessage(reloading, sender);
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
                            PythonManager.loadScripts();
                            long end = System.currentTimeMillis();
                            BChatUtils.sendMessage((success ? reloadSuccess : reloadFail).getValue()
                              .replace("%ms%", String.valueOf(end - begin)), sender);
                        }
                        break;
                    case "version":
                        if (testPermission(sender, noPermissionVersion, "version")) {
                            Plugin plugin = BungeeLoader.getInstance();
                            BChatUtils.sendMessage(version.getValue()
                              .replace("%plugin-ver%", plugin.getDescription().getVersion())
                              .replace("%proxy-ver%", plugin.getProxy().getVersion()), sender);
                        }
                        break;
                    default:
                        BChatUtils.sendMessage(usage.getValue().replace("%label%", "bspygotutils"), sender);
                }
            }
        }
    }
}
