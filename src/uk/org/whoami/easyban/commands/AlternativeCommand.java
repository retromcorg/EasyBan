package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;
import java.util.*;
import java.net.*;

public class AlternativeCommand extends EasyBanCommand
{
    private DataSource database;
    
    public AlternativeCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        try {
            InetAddress.getByName(args[0]);
            cs.sendMessage(this.m._("Users who connected from IP") + args[0]);
            this.sendListToSender(cs, this.database.getNicks(args[0]));
        }
        catch (UnknownHostException ex) {
            final ArrayList<String> nicks = new ArrayList<String>();
            for (final String ip : this.database.getHistory(args[0])) {
                Collections.addAll(nicks, this.database.getNicks(ip));
            }
            cs.sendMessage(this.m._("Alternative nicks of ") + args[0]);
            this.sendListToSender(cs, nicks.toArray(new String[0]));
        }
    }
}
