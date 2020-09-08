package uk.org.whoami.easyban.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import uk.org.whoami.easyban.datasource.*;

import java.awt.Color;

import org.bukkit.command.*;

import uk.org.whoami.easyban.*;
import uk.org.whoami.easyban.settings.Settings;

public class UnbanCommand extends EasyBanCommand
{
    private DataSource database;
    private Settings config;
    
    public UnbanCommand(final DataSource database, Settings settings) {
        this.database = database;
        config = settings;
    }
    
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        this.database.unbanNick(args[0]);
        cs.getServer().broadcastMessage(args[0] + this.m._(" has been unbanned"));
        ConsoleLogger.info(args[0] + " has been unbanned");
        //Discord Log
        String commandSendToDiscord = "/eunban ";
        for (int i = 0; i < args.length; i++) {
        	commandSendToDiscord = commandSendToDiscord + args[i] + " ";
        	
        }
        if(config.isDiscordAuditLogEnabled()) {
            try {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("EasyBan Unban Command", null);
                eb.setColor(Color.RED);
                eb.setDescription(args[0] + " Has Been unbanned By " + this.admin);
                eb.setFooter("Command: " + commandSendToDiscord, null);
                EasyBan.discord.getDiscordBot().jda.getTextChannelById(config.getDiscordUnbanChannelID()).sendMessage(eb.build()).queue();
            } catch (Exception e) {
                cs.sendMessage("Discord Integration Error");
            }
        }
    }
}
