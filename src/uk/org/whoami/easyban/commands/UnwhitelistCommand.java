package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;
import uk.org.whoami.easyban.*;

public class UnwhitelistCommand extends EasyBanCommand
{
    private DataSource database;
    
    public UnwhitelistCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        this.database.unWhitelist(args[0]);
        cs.getServer().broadcastMessage(args[0] + this.m._(" has been removed from the whitelist"));
        ConsoleLogger.info(this.admin + " unwhitelisted " + args[0]);
    }
}
