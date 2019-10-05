package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;

public class ListSubnetBansCommand extends EasyBanCommand
{
    private DataSource database;
    
    public ListSubnetBansCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        cs.sendMessage(this.m._("Banned subnets: "));
        this.sendListToSender(cs, this.database.getBannedSubnets());
    }
}
