package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;
import uk.org.whoami.easyban.settings.*;
import uk.org.whoami.easyban.*;

public class WhitelistCommand extends EasyBanCommand
{
    private DataSource database;
    
    public WhitelistCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        this.database.whitelist(args[0]);
        final Settings settings = Settings.getInstance();
        if (settings.isWhitelistPublic()) {
            cs.getServer().broadcastMessage(args[0] + this.m._(" has been whitelisted"));
        }
        ConsoleLogger.info(this.admin + " whitelisted " + args[0]);
    }
}
