package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;

public class HistoryCommand extends EasyBanCommand
{
    private DataSource database;
    
    public HistoryCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        cs.sendMessage(this.m._("Ips from ") + args[0]);
        this.sendListToSender(cs, this.database.getHistory(args[0]));
    }
}
