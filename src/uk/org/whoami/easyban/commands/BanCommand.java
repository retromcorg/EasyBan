package uk.org.whoami.easyban.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.org.whoami.easyban.ConsoleLogger;
import uk.org.whoami.easyban.EasyBan;
import uk.org.whoami.easyban.datasource.DataSource;
import uk.org.whoami.easyban.settings.Settings;
import uk.org.whoami.easyban.util.Subnet;

import java.awt.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

public class BanCommand extends EasyBanCommand {
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

        Player player = cs.getServer().getPlayer(playerNick);
        if (player == null) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(args[0])) {
                    player = p;
                    break;
                }
            }
        }

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
            } else {
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
            String ip = player.getAddress().getAddress().getHostAddress();
            for(Player p: Bukkit.getServer().getOnlinePlayers()) {
                if(!p.getName().equalsIgnoreCase(player.getName())) {
                    if(p.getAddress().getAddress().getHostAddress().equalsIgnoreCase(ip)) {
                        p.kickPlayer(kickmsg);
                    }
                }
            }
            player.kickPlayer(kickmsg);
        }
        this.database.unbanNick(playerNick);
        this.database.unbanNick(playerNick.toLowerCase());

        this.database.banNick(playerNick.toLowerCase(), this.admin, reason, until);
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
        if (settings.isDiscordAuditLogEnabled()) {
            try {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("EasyBan Ban Command", null);
                eb.setColor(Color.RED);
                if(until != null) {
                    eb.setDescription("IGN: " + playerNick + "\nReason: " + reason + "\nUntil: " + DateFormat.getDateTimeInstance().format(until.getTime()));
                } else {
                    eb.setDescription("IGN: " + playerNick + "\nReason: " + reason + "\nUntil: permanent");
                }
                eb.setFooter("Command: " + commandSendToDiscord, null);
                EasyBan.discord.getDiscordBot().jda.getTextChannelById(config.getDiscordBanChannelID()).sendMessage(eb.build()).queue();
            } catch (Exception e) {
                cs.sendMessage("Discord Integration Error");
            }

        }


    }
}
