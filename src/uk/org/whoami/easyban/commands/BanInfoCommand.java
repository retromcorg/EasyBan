package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;
import java.text.*;
import java.util.*;

public class BanInfoCommand extends EasyBanCommand
{
    private DataSource database;
    
    public BanInfoCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        final HashMap<String, String> info = this.database.getBanInformation(args[0]);
        if (info == null) {
            cs.sendMessage(args[0] + this.m._(" is not banned"));
            return;
        }
        cs.sendMessage(args[0] + this.m._(" is banned"));
        if (info.containsKey("admin")) {
            cs.sendMessage(this.m._("Admin: ") + info.get("admin"));
        }
        if (info.containsKey("reason")) {
            cs.sendMessage(this.m._("Reason: ") + info.get("reason"));
        }
        if (info.containsKey("until")) {
            final Date until = new Date(new Long(info.get("until")));
            cs.sendMessage(this.m._("Until: ") + DateFormat.getDateTimeInstance().format(until));
        }
    }
}
