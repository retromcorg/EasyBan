package uk.org.whoami.easyban.settings;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.util.config.*;
import java.io.*;

public final class Settings extends Configuration
{
    public static final String PLUGIN_FOLDER = "./plugins/EasyBan";
    public static final String MESSAGE_FILE = "./plugins/EasyBan/messages.yml";
    public static final String SETTINGS_FILE = "./plugins/EasyBan/config.yml";
    public static final String DATABASE_FILE = "./plugins/EasyBan/bans.yml";
    private static Settings singleton;
    
    private Settings() {
        super(new File("./plugins/EasyBan/config.yml"));
        this.reload();
    }
    
    public void reload() {
        this.load();
        this.write();
        this.save();
    }
    
    private void write() {
        this.getDatabase();
        this.getMySQLDatabaseName();
        this.getMySQLHost();
        this.getMySQLPort();
        this.getMySQLUsername();
        this.getMySQLPassword();
        this.isKickPublic();
        this.isKickReasonPublic();
        this.isBanPublic();
        this.isBanReasonPublic();
        this.isBanUntilPublic();
        this.isSubnetBanPublic();
        this.isSubnetBanReasonPublic();
        this.isCountryBanPublic();
        this.isAppendCustomKickMessageEnabled();
        this.isAppendCustomBanMessageEnabled();
        this.isDiscordAuditLogEnabled();
        this.getDiscordBanChannelID();
        this.getDiscordUnbanChannelID();
        this.isAuthmeHookEnabled();
    }

    public Boolean isAuthmeHookEnabled() {
        final String key = "authmehook";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }

    public Boolean isDiscordAuditLogEnabled() {
        final String key = "discord.enable";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)false);
        }
        return this.getBoolean(key, true);
    }
    public String getDiscordBanChannelID() {
        final String key = "discord.channel.ban";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)"");
        }
        return this.getString(key);
    }

    public String getDiscordUnbanChannelID() {
        final String key = "discord.channel.unban";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)"");
        }
        return this.getString(key);
    }
    
    public String getDatabase() {
        final String key = "database";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)"yaml");
        }
        return this.getString(key);
    }
    
    public String getMySQLDatabaseName() {
        final String key = "MySQLDatabaseName";
        if (this.getString("schema") != null) {
            final String s = this.getString("schema");
            this.removeProperty("schema");
            this.setProperty(key, (Object)s);
        }
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)"easyban");
        }
        return this.getString(key);
    }
    
    public String getMySQLHost() {
        final String key = "MySQLHost";
        if (this.getString("host") != null) {
            final String s = this.getString("host");
            this.removeProperty("host");
            this.setProperty(key, (Object)s);
        }
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)"127.0.0.1");
        }
        return this.getString(key);
    }
    
    public String getMySQLPort() {
        final String key = "MySQLPort";
        if (this.getString("port") != null) {
            final String s = this.getString("port");
            this.removeProperty("port");
            this.setProperty(key, (Object)s);
        }
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)"3306");
        }
        return this.getString(key);
    }
    
    public String getMySQLUsername() {
        final String key = "MySQLUsername";
        if (this.getString("username") != null) {
            final String s = this.getString("username");
            this.removeProperty("username");
            this.setProperty(key, (Object)s);
        }
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)"easyban");
        }
        return this.getString(key);
    }
    
    public String getMySQLPassword() {
        final String key = "MySQLPassword";
        if (this.getString("password") != null) {
            final String s = this.getString("password");
            this.removeProperty("password");
            this.setProperty(key, (Object)s);
        }
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)"CHANGEME");
        }
        return this.getString(key);
    }
    
    public boolean isKickPublic() {
        final String key = "settings.message.kick.public";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }
    
    public boolean isKickReasonPublic() {
        final String key = "settings.message.kick.publicReason";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }
    
    public boolean isAppendCustomKickMessageEnabled() {
        final String key = "settings.message.kick.appendCustomMessage";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)false);
        }
        return this.getBoolean(key, false);
    }
    
    public boolean isBanPublic() {
        final String key = "settings.message.ban.public";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }
    
    public boolean isBanReasonPublic() {
        final String key = "settings.message.ban.publicReason";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }
    
    public boolean isBanUntilPublic() {
        final String key = "settings.message.ban.publicUntil";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }
    
    public boolean isAppendCustomBanMessageEnabled() {
        final String key = "settings.message.ban.appendCustomMessage";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)false);
        }
        return this.getBoolean(key, false);
    }
    
    public boolean isSubnetBanPublic() {
        final String key = "settings.message.subnetBan.public";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }
    
    public boolean isSubnetBanReasonPublic() {
        final String key = "settings.message.subnetBan.publicReason";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }
    
    public boolean isCountryBanPublic() {
        final String key = "settings.message.countryBan.public";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }
    
    public boolean isWhitelistPublic() {
        final String key = "settings.message.whitelist.public";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }
    
    public static Settings getInstance() {
        if (Settings.singleton == null) {
            Settings.singleton = new Settings();
        }
        return Settings.singleton;
    }
}
