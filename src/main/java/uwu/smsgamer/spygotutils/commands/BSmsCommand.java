package uwu.smsgamer.spygotutils.commands;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import uwu.smsgamer.spygotutils.*;
import uwu.smsgamer.senapi.config.ConfVal;
import uwu.smsgamer.spygotutils.utils.BChatUtils;

public abstract class BSmsCommand extends Command {
    public ConfVal<String> noPermission;
    public boolean consoleAllowed;
    public String permissionBase;

    public BSmsCommand(String cmdName, String... aliases) {
        this(cmdName, false, aliases);
    }

    public BSmsCommand(String cmdName, boolean consoleAllowed, String... aliases) {
        super(cmdName, null, aliases);
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeLoader.getInstance(), this);
        this.noPermission = new ConfVal<>("commands." + cmdName.substring(1) + ".no-permission", "messages", "%prefix% &cYou do not have permission to execute this command!");
        this.consoleAllowed = consoleAllowed;
        this.permissionBase = "spygotutils.command." + cmdName.substring(1);
    }

    public boolean testPermission(CommandSender sender) {
        if (!consoleAllowed && !(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatColor.RED + "No console command sender allowed for this command!");
            return false;
        }
        String perm = this.permissionBase;
        if (sender.hasPermission(perm))
            return true;
        BChatUtils.sendMessage(this.noPermission.getValue().replace("%perm%", perm), sender);
        return false;
    }

    public boolean testPermission(CommandSender sender, ConfVal<String> noPerm, String... appendages) {
        String perm = getPermission(appendages);
        if (sender.hasPermission(perm))
            return true;
        BChatUtils.sendMessage(noPerm.getValue().replace("%perm%", perm), sender);
        return false;
    }

    public String getPermission(String... appendages) {
        StringBuilder perm = new StringBuilder(this.permissionBase);
        for (String appendage : appendages) perm.append('.').append(appendage);
        return perm.toString();
    }
}
