package uk.org.whoami.easyban.tasks;

import uk.org.whoami.easyban.datasource.*;
import uk.org.whoami.easyban.*;

import java.awt.Color;
import java.util.*;

import net.dv8tion.jda.core.EmbedBuilder;
import uk.org.whoami.easyban.settings.Settings;

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
				if(config.isDiscordAuditLogEnabled()) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("EasyBan Unban Command", null);
					eb.setColor(Color.RED);
					eb.setDescription(name + " Has Been unbanned By Timer \n" + name + " was banned by " + this.data.getBanInformation(name).get("admin") + "\nReason: " + this.data.getBanInformation(name).get("reason"));
					eb.setFooter("Unbanned Automatically On Timer", null);
					uk.org.whoami.easyban.EasyBan.discord.Discord().DiscordSendEmbedToChannel(config.getDiscordChannelID(), eb);
					this.data.unbanNick(name);
				}
			}
		}
	}
}
