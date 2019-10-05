package uk.org.whoami.easyban.listener;

import uk.org.whoami.easyban.datasource.*;
import java.text.*;
import uk.org.whoami.easyban.settings.*;
import uk.org.whoami.easyban.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;

public class EasyBanPlayerListener extends PlayerListener
{
    private DataSource database;
    private Message msg;
    private boolean AuthmeHook;
    
    public EasyBanPlayerListener(final DataSource database, boolean authme) {
        this.database = database;
        this.msg = Message.getInstance();
        this.AuthmeHook = authme;
    }
    
    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (event.getPlayer() == null || !event.getResult().equals((Object)PlayerLoginEvent.Result.ALLOWED)) {
            return;
        }
        final String name = event.getPlayer().getName();
        final String ip = event.getKickMessage();
        if (this.database.isNickBanned(name)) {
            final HashMap<String, String> banInfo = this.database.getBanInformation(name);
            String kickmsg = this.msg._("You have been banned by ") + banInfo.get("admin");
            if (banInfo.containsKey("reason")) {
                kickmsg = kickmsg + " " + this.msg._("Reason: ") + banInfo.get("reason");
            }
            if (banInfo.containsKey("until")) {
                final Long unixTime = Long.parseLong(banInfo.get("until"));
                kickmsg = kickmsg + " " + this.msg._("Until: ") + DateFormat.getDateTimeInstance().format(new Date(unixTime));
            }
            if (Settings.getInstance().isAppendCustomBanMessageEnabled()) {
                kickmsg = kickmsg + " " + this.msg._("custom_ban");
            }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickmsg);
            ConsoleLogger.info("Ban for " + name + " detected");
            return;
        }
        if (this.database.isIpBanned(ip)) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, this.msg._("You are banned"));
            ConsoleLogger.info("IP Ban for " + name + " detected");
            return;
        }
        if (this.database.isNickWhitelisted(event.getPlayer().getName())) {
            ConsoleLogger.info("Whitelist entry for " + name + " found");
            return;
        }
        if (this.database.isSubnetBanned(ip)) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, this.msg._("Your subnet is banned"));
            ConsoleLogger.info("Subnet ban for " + name + "/" + ip + " detected");
        }
    }
    
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        final Player player = event.getPlayer();
        final String name = player.getName();
        final String ip = player.getAddress().getAddress().getHostAddress();
        if(!AuthmeHook) {
            database.addIpToHistory(name, ip);
        }
        if (this.database.isNickBanned(name)) {
            final HashMap<String, String> banInfo = this.database.getBanInformation(name);
            String kickmsg = this.msg._("You have been banned by ") + banInfo.get("admin");
            if (banInfo.containsKey("reason")) {
                kickmsg = kickmsg + " " + this.msg._("Reason: ") + banInfo.get("reason");
            }
            if (banInfo.containsKey("until")) {
                final Long unixTime = Long.parseLong(banInfo.get("until"));
                kickmsg = kickmsg + " " + this.msg._("Until: ") + DateFormat.getDateTimeInstance().format(new Date(unixTime));
            }
            if (Settings.getInstance().isAppendCustomBanMessageEnabled()) {
                kickmsg = kickmsg + " " + this.msg._("custom_ban");
            }
            player.kickPlayer(kickmsg);
            ConsoleLogger.info("Ban for " + name + " detected");
            return;
        }
        if (this.database.isIpBanned(ip)) {
            player.kickPlayer(this.msg._("You are banned"));
            ConsoleLogger.info("IP Ban for " + name + " detected");
            return;
        }
        if (this.database.isNickWhitelisted(event.getPlayer().getName())) {
            ConsoleLogger.info("Whitelist entry for " + name + " found");
            return;
        }
        if (this.database.isSubnetBanned(ip)) {
            player.kickPlayer(this.msg._("Your subnet is banned"));
            ConsoleLogger.info("Subnet ban for " + name + "/" + ip + " detected");
        }
    }
}
