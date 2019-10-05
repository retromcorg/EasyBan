package uk.org.whoami.easyban.listener;

import uk.org.whoami.easyban.datasource.*;
import uk.org.whoami.geoip.*;
import uk.org.whoami.easyban.settings.*;
import org.bukkit.event.player.*;
import uk.org.whoami.easyban.*;
import java.net.*;

public class EasyBanCountryListener extends PlayerListener
{
    private DataSource database;
    private GeoIPLookup geo;
    private final Message m;
    
    public EasyBanCountryListener(final DataSource data, final GeoIPLookup geo) {
        this.m = Message.getInstance();
        this.database = data;
        this.geo = geo;
    }
    
    public void onPlayerLogin(final PlayerLoginEvent evt) {
        if (evt.getPlayer() == null || !evt.getResult().equals((Object)PlayerLoginEvent.Result.ALLOWED)) {
            return;
        }
        final String nick = evt.getPlayer().getName();
        final String ip = evt.getKickMessage();
        if (this.database.isNickWhitelisted(nick)) {
            return;
        }
        try {
            final InetAddress inet = InetAddress.getByName(ip);
            final String code = this.geo.getCountry(inet).getCode();
            if (this.database.isCountryBanned(code)) {
                ConsoleLogger.info("Player " + nick + "is from banned country " + code);
                evt.disallow(PlayerLoginEvent.Result.KICK_BANNED, this.m._("Your country has been banned"));
            }
        }
        catch (UnknownHostException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
    }
}
