package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;
import uk.org.whoami.easyban.*;

public class UnbanCountryCommand extends EasyBanCommand
{
    private DataSource database;
    
    public UnbanCountryCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        this.database.unbanCountry(args[0]);
        cs.getServer().broadcastMessage(this.m._("A country has been unbanned: ") + args[0]);
        ConsoleLogger.info(this.admin + " unbanned the country " + args[0]);
    }
}
