package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;
import uk.org.whoami.easyban.util.*;
import uk.org.whoami.easyban.*;

public class UnbanSubnetCommand extends EasyBanCommand
{
    private DataSource database;
    
    public UnbanSubnetCommand(final DataSource database) {
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
            this.database.unbanSubnet(subnet);
            cs.getServer().broadcastMessage(args[0] + this.m._(" has been unbanned"));
            ConsoleLogger.info(args[0] + " has been unbanned by " + this.admin);
        }
        else {
            cs.sendMessage(this.m._("Invalid Subnet"));
        }
    }
}
