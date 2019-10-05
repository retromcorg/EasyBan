package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;

public class ListWhitelistCommand extends EasyBanCommand
{
    private DataSource database;
    
    public ListWhitelistCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        cs.sendMessage(this.m._("Whitelist: "));
        this.sendListToSender(cs, this.database.getWhitelistedNicks());
    }
}
