package uk.org.whoami.easyban.tasks;

import net.dv8tion.jda.api.EmbedBuilder;
import uk.org.whoami.easyban.ConsoleLogger;
import uk.org.whoami.easyban.EasyBan;
import uk.org.whoami.easyban.datasource.DataSource;
import uk.org.whoami.easyban.settings.Settings;

import java.awt.*;
import java.util.Calendar;
import java.util.HashMap;

public class UnbanTask implements Runnable {
    private DataSource data;
    private Settings config;

    public UnbanTask(final DataSource data, final Settings settings) {
        this.data = data;
        config = settings;
    }

    @Override
    public void run() {
        final Calendar cal = Calendar.getInstance();
        final HashMap<String, Long> tmpBans = this.data.getTempBans();
        for (final String name : tmpBans.keySet()) {
            if (tmpBans.get(name) != 100000L && cal.getTimeInMillis() > tmpBans.get(name)) {
                ConsoleLogger.info("Temporary ban for " + name + " has been removed");
                //Discord
                if (config.isDiscordAuditLogEnabled()) {
                    try {
                        HashMap<String, String> banInfo = null;
                        banInfo = this.data.getBanInformation(name);
                        if (banInfo == null) {
                            banInfo = this.data.getBanInformation(name.toLowerCase());
                        }
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("EasyBan Unban Command", null);
                        eb.setColor(Color.RED);
                        eb.setDescription(name + " Has Been unbanned By Timer \n" + name + " was banned by " + banInfo.get("admin") + "\nReason: " + banInfo.get("reason"));
                        eb.setFooter("Unbanned Automatically On Timer", null);
                        EasyBan.discord.getDiscordBot().jda.getTextChannelById(config.getDiscordUnbanChannelID()).sendMessage(eb.build()).queue();
                    } catch (Exception e) {
                        ConsoleLogger.info("Discord Integration Error");
                    }
                }
                this.data.unbanNick(name);
            }
        }
    }
}
