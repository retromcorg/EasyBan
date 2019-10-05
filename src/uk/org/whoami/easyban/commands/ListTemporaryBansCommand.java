package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;
import java.text.*;
import java.util.*;

public class ListTemporaryBansCommand extends EasyBanCommand
{
    private DataSource database;
    
    public ListTemporaryBansCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        cs.sendMessage(this.m._("Temporary bans: "));
        for (final String key : this.database.getTempBans().keySet()) {
            cs.sendMessage(key + " : " + DateFormat.getDateTimeInstance().format(new Date(this.database.getTempBans().get(key))));
        }
    }
}
