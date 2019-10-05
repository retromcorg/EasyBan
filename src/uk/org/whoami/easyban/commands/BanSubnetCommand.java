package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;
import uk.org.whoami.easyban.util.*;
import uk.org.whoami.easyban.settings.*;
import uk.org.whoami.easyban.*;

public class BanSubnetCommand extends EasyBanCommand
{
    private DataSource database;
    
    public BanSubnetCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        Subnet subnet = null;
        try {
            subnet = new Subnet(args[0]);
        }
        catch (IllegalArgumentException ex) {}
        if (subnet != null) {
            String reason = null;
            if (args.length == 1) {
                this.database.banSubnet(subnet, this.admin, null);
            }
            else {
                reason = "";
                for (int i = 1; i < args.length; ++i) {
                    reason = reason + args[i] + " ";
                }
                this.database.banSubnet(subnet, this.admin, reason);
            }
            final Settings settings = Settings.getInstance();
            if (settings.isSubnetBanPublic()) {
                cs.getServer().broadcastMessage(subnet.toString() + this.m._(" has been banned"));
            }
            if (reason != null && settings.isSubnetBanReasonPublic()) {
                cs.getServer().broadcastMessage(this.m._("Reason: ") + reason);
            }
            ConsoleLogger.info(subnet.toString() + " has been banned by " + this.admin);
        }
        else {
            cs.sendMessage(this.m._("Invalid Subnet"));
        }
    }
}
