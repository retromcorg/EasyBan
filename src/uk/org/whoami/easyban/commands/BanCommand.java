package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.datasource.*;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import uk.org.whoami.easyban.util.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import uk.org.whoami.easyban.settings.*;

import java.awt.Color;
import java.text.*;
import uk.org.whoami.easyban.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.PluginManager;

import com.johnymuffin.discordcore.DiscordCore;

import net.dv8tion.jda.core.EmbedBuilder;

public class BanCommand extends EasyBanCommand
{
    private DataSource database;
    private Settings config;
    
    public BanCommand(final DataSource database, Settings settings) {
        this.database = database;
        config = settings;
    }

    java.time.LocalDateTime changeDateTimeByString(java.time.LocalDateTime current, String part,
                                                   boolean increaseOrDecrease) {
        java.time.LocalDateTime newDateTime = null;

        Integer amount = Integer.parseInt(part.replaceAll("\\D", ""));
        if (part.contains("S") || part.contains("s"))
            newDateTime = increaseOrDecrease ? current.plusSeconds(amount) : current.minusSeconds(amount);
        else if (part.contains("m"))
            newDateTime = increaseOrDecrease ? current.plusMinutes(amount) : current.minusMinutes(amount);
        else if (part.contains("h") || part.contains("H"))
            newDateTime = increaseOrDecrease ? current.plusHours(amount) : current.minusHours(amount);
        else if (part.contains("D") || part.contains("d"))
            newDateTime = increaseOrDecrease ? current.plusDays(amount) : current.minusDays(amount);
        else if (part.contains("W") || part.contains("w"))
            newDateTime = increaseOrDecrease ? current.plusDays(amount * 7) : current.minusDays(amount * 7);
        else if (part.contains("M"))
            newDateTime = increaseOrDecrease ? current.plusMonths(amount) : current.minusMonths(amount);
        else if (part.contains("Y") || part.contains("y"))
            newDateTime = increaseOrDecrease ? current.plusYears(amount) : current.minusYears(amount);
        else {

        }

        return newDateTime;
    }


    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        String playerNick = args[0];
        final Player player = cs.getServer().getPlayer(playerNick);
        String reason = null;
        Calendar until = null;
        if (player != null) {
            playerNick = player.getName();
        }
        if (args.length > 1) {
            int to = args.length - 1;
            if (Subnet.isParseableInteger(args[args.length - 1])) {
                until = Calendar.getInstance();
                final int min = Integer.parseInt(args[args.length - 1]);
                until.add(12, min);
            }
            else {
                try {
                    //Tempban
                    LocalDateTime dateTime = LocalDateTime.now();
                    LocalDateTime time = changeDateTimeByString(dateTime, args[args.length - 1], true);
                    long epoch = time.atZone(ZoneId.systemDefault()).toEpochSecond();
                    long current = System.currentTimeMillis() / 1000L;
                    //Turn into Easy Ban Format
                    until = Calendar.getInstance();
                    final int min = (int) ((epoch - current) / 60L);
                    until.add(12, min);
                    to = args.length - 1;
                } catch (Exception e) {
                    //Most likely a Perm ban at this point :(
                    until = null;
                    to = args.length;
                }
            }
            String tmp = "";
            for (int i = 1; i < to; ++i) {
                tmp = tmp + args[i] + " ";
            }
            if (tmp.length() > 0) {
                reason = tmp;
            }
        }
        final Settings settings = Settings.getInstance();
        if (player != null) {
            String kickmsg = this.m._("You have been banned by ") + this.admin;
            if (reason != null) {
                kickmsg = kickmsg + " " + this.m._("Reason: ") + reason;
            }
            if (until != null) {
                kickmsg = kickmsg + " " + this.m._("Until: ") + DateFormat.getDateTimeInstance().format(until.getTime());
            }
            if (settings.isAppendCustomBanMessageEnabled()) {
                kickmsg = kickmsg + " " + this.m._("custom_ban");
            }
            player.kickPlayer(kickmsg);
        }
        this.database.unbanNick(playerNick);
        this.database.banNick(playerNick, this.admin, reason, until);
        if (settings.isBanPublic()) {
        	
            cs.getServer().broadcastMessage(playerNick + this.m._(" has been banned"));
            if (settings.isBanReasonPublic() && reason != null) {
                cs.getServer().broadcastMessage(this.m._("Reason: ") + reason);
            }
            if (settings.isBanUntilPublic() && until != null) {
                cs.getServer().broadcastMessage(this.m._("Until: ") + DateFormat.getDateTimeInstance().format(until.getTime()));
            }
        }
        ConsoleLogger.info(playerNick + " has been banned by " + this.admin);
        String commandSendToDiscord = "/eban ";
        for (int i = 0; i < args.length; i++) {
        	commandSendToDiscord = commandSendToDiscord + args[i] + " ";
        	
        }
        //Discord
        if(settings.isDiscordAuditLogEnabled()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("EasyBan Ban Command", null);
            eb.setColor(Color.RED);
            eb.setDescription(playerNick + " Has Been Banned By " + this.admin);
            eb.setFooter("Command: " + commandSendToDiscord, null);
            uk.org.whoami.easyban.EasyBan.discord.Discord().DiscordSendEmbedToChannel(config.getDiscordChannelID(), eb);
        }


    }
}
