package uwu.smsgamer.spygotutils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import uwu.smsgamer.spygotutils.SPYgotUtils;
import uwu.smsgamer.spygotutils.config.ConfVal;
import uwu.smsgamer.spygotutils.utils.ChatUtils;

public abstract class SmsCommand implements TabExecutor {
    public PluginCommand command;
    public ConfVal<String> noPermission;
    public boolean consoleAllowed;
    public String permissionBase;

    public SmsCommand(String cmdName) {
        this(cmdName, false);
    }

    public SmsCommand(String cmdName, boolean consoleAllowed) {
        this.command = SPYgotUtils.getInstance().plugin.getServer().getPluginCommand(cmdName);
        this.command.setExecutor(this);
        this.noPermission = new ConfVal<>("commands." + cmdName + ".no-permission", "messages", "%prefix% &cYou do not have permission to execute this command!");
        this.consoleAllowed = consoleAllowed;
        this.permissionBase = "spygotutils.command." + cmdName;
    }

    public boolean testPermission(CommandSender sender) {
        if (!consoleAllowed && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "No console command sender allowed for this command!");
            return false;
        }
        String perm = this.permissionBase;
        if (sender.hasPermission(perm))
            return true;
        ChatUtils.sendMessage(this.noPermission.getValue().replace("$perm$", perm), sender);
        return false;
    }

    public boolean testPermission(CommandSender sender, ConfVal<String> noPerm, String... appendages) {
        String perm = getPermission(appendages);
        if (sender.hasPermission(perm))
            return true;
        ChatUtils.sendMessage(noPerm.getValue().replace("$perm$", perm), sender);
        return false;
    }

    public String getPermission(String... appendages) {
        StringBuilder perm = new StringBuilder(this.permissionBase);
        for (String appendage : appendages) perm.append('.').append(appendage);
        return perm.toString();
    }
}
