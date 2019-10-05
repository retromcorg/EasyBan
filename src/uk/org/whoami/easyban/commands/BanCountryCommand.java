package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;
import org.bukkit.command.*;
import uk.org.whoami.easyban.settings.*;
import uk.org.whoami.easyban.*;

public class BanCountryCommand extends EasyBanCommand
{
    private DataSource database;
    
    public BanCountryCommand(final DataSource database) {
        this.database = database;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        this.database.banCountry(args[0]);
        final Settings settings = Settings.getInstance();
        if (settings.isCountryBanPublic()) {
            cs.getServer().broadcastMessage(this.m._("A country has been banned: ") + args[0]);
        }
        ConsoleLogger.info(this.admin + " banned the country " + args[0]);
    }
}
