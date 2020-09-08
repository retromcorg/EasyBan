package uk.org.whoami.easyban.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import uk.org.whoami.easyban.ConsoleLogger;
import uk.org.whoami.easyban.EasyBan;
import uk.org.whoami.easyban.datasource.DataSource;
import uk.org.whoami.easyban.settings.Message;
import uk.org.whoami.easyban.settings.Settings;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class EasyBanPlayerListener extends PlayerListener {
    private DataSource database;
    private Message msg;
    private boolean AuthmeHook;
    private EasyBan eb;

    public EasyBanPlayerListener(EasyBan easyBan, final DataSource database, boolean authmeHook) {
        eb = easyBan;
        this.database = database;
        this.msg = Message.getInstance();
        this.AuthmeHook = authmeHook;
    }

    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (event.getPlayer() == null || !event.getResult().equals((Object) PlayerLoginEvent.Result.ALLOWED)) {
            return;
        }
        final String name = event.getPlayer().getName();
        final String ip = event.getKickMessage();
        //Later versions use lowercase names
        if (this.database.isNickBanned(name) || this.database.isNickBanned(name.toLowerCase())) {
            HashMap<String, String> banInfo = null;
            banInfo = this.database.getBanInformation(name);
            if (banInfo == null) {
                banInfo = this.database.getBanInformation(name.toLowerCase());
            }
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

            //Normal EasyBan
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
        if (!AuthmeHook) {
            database.addIpToHistory(name, ip);
        }
        if (this.database.isNickBanned(name) || this.database.isNickBanned(name.toLowerCase())) {
            HashMap<String, String> banInfo = null;

            if (this.database.getBanInformation(name) == null) {
                banInfo = this.database.getBanInformation(name.toLowerCase());
            } else {
                banInfo = this.database.getBanInformation(name);
            }

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

            //Normal EasyBan
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
