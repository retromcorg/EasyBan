package uk.org.whoami.easyban.permission;

import com.nijiko.permissions.*;
import org.bukkit.command.*;
import com.nijikokun.bukkit.Permissions.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;

public class Permission
{
    private static PermissionHandler handler;
    
    public static boolean hasPermission(final CommandSender sender, final String cmd) {
        boolean perm = sender.hasPermission("easyban." + cmd.toLowerCase());
        if (Permission.handler == null) {
            final Plugin permissionsPlugin = sender.getServer().getPluginManager().getPlugin("Permissions");
            if (permissionsPlugin != null) {
                Permission.handler = ((Permissions)permissionsPlugin).getHandler();
            }
        }
        if (sender instanceof Player && Permission.handler != null) {
            perm = Permission.handler.has((Player)sender, "easyban." + cmd.toLowerCase());
        }
        return perm;
    }
}
