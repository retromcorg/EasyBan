package uk.org.whoami.easyban;

import org.bukkit.plugin.java.*;

import com.johnymuffin.discordcore.DiscordCore;

import uk.org.whoami.easyban.settings.*;
import org.bukkit.plugin.*;
import uk.org.whoami.easyban.datasource.*;
import org.bukkit.event.*;
import uk.org.whoami.easyban.listener.*;
import uk.org.whoami.easyban.tasks.*;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import uk.org.whoami.easyban.commands.*;
import uk.org.whoami.geoip.*;

public class EasyBan extends JavaPlugin {
    private DataSource database;
    private EasyBan plugin;
    private boolean AuthmeHook = false;
    private Settings settings;
    public static DiscordCore discord;

    public DiscordCore getDiscord() {
        return discord;
    }

    public void onDisable() {
        this.getServer().getScheduler().cancelTasks((Plugin) this);
        this.database.close();
        ConsoleLogger.info("EasyBan disabled; Version: " + this.getDescription().getVersion());
    }

    public void onEnable() {
        this.settings = Settings.getInstance();
        plugin = this;
        final String db = this.settings.getDatabase();
        Label_0168:
        {
            if (!db.equals("yaml")) {
                if (db.equals("hsql")) {
                    try {
                        this.database = new HSQLDataSource(this);
                        break Label_0168;
                    } catch (Exception ex) {
                        ConsoleLogger.info(ex.getMessage());
                        ConsoleLogger.info("Can't load database");
                        this.getServer().getPluginManager().disablePlugin((Plugin) this);
                        return;
                    }
                }
                if (db.equals("mysql")) {
                    try {
                        this.database = new MySQLDataSource(this.settings);
                        break Label_0168;
                    } catch (Exception ex) {
                        ConsoleLogger.info(ex.getMessage());
                        ConsoleLogger.info("Can't load database");
                        this.getServer().getPluginManager().disablePlugin((Plugin) this);
                        return;
                    }
                }
                ConsoleLogger.info("Unsupported database");
                this.getServer().getPluginManager().disablePlugin((Plugin) this);
                return;
            }
            this.database = new YamlDataSource();
        }
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (settings.isAuthmeHookEnabled()) {
            if (pm.getPlugin("AuthMe") != null) {
                Double versionD = Double.valueOf(pm.getPlugin("AuthMe").getDescription().getVersion());
                if (versionD >= 2.8) {
                    ConsoleLogger.info("AuthMe Hook Established | Authme Version: " + versionD);
                    AuthmeHook = true;
                } else {
                    ConsoleLogger.info("Easyban With Newer Versions Of AuthMe Has Better Functionality, You Should Consider Upgrading https://github.com/RhysB/RetroMC-Authme");
                }

            }
        }


        final EasyBanPlayerListener l = new EasyBanPlayerListener(this.database, AuthmeHook);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, (Listener) l, Event.Priority.Lowest, (Plugin) this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, (Listener) l, Event.Priority.Lowest, (Plugin) this);
        if (AuthmeHook) {
            final RetroLoginAuthme RLE = new RetroLoginAuthme(this.database);
            this.getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, RLE, Event.Priority.Lowest, this);
        }

        final GeoIPLookup geo = this.getGeoIPLookup();
        if (geo != null) {
            this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, (Listener) new EasyBanCountryListener(this.database, geo), Event.Priority.Lowest, (Plugin) this);
        }
        this.getServer().getScheduler().scheduleAsyncRepeatingTask((Plugin) this, (Runnable) new UnbanTask(this.database, this.settings), 60L, 1200L);
        this.getCommand("ekick").setExecutor((CommandExecutor) new KickCommand());
        this.getCommand("eban").setExecutor((CommandExecutor) new BanCommand(this.database, this.settings));
        this.getCommand("eunban").setExecutor((CommandExecutor) new UnbanCommand(this.database, this.settings));
        this.getCommand("ehistory").setExecutor((CommandExecutor) new HistoryCommand(this.database));
        this.getCommand("ealternative").setExecutor((CommandExecutor) new AlternativeCommand(this.database));
        this.getCommand("ebaninfo").setExecutor((CommandExecutor) new BanInfoCommand(this.database));
        this.getCommand("elistbans").setExecutor((CommandExecutor) new ListBansCommand(this.database));
        this.getCommand("elisttmpbans").setExecutor((CommandExecutor) new ListTemporaryBansCommand(this.database));
        this.getCommand("ebansubnet").setExecutor((CommandExecutor) new BanSubnetCommand(this.database));
        this.getCommand("eunbansubnet").setExecutor((CommandExecutor) new UnbanSubnetCommand(this.database));
        this.getCommand("elistsubnets").setExecutor((CommandExecutor) new ListSubnetBansCommand(this.database));
        this.getCommand("ebancountry").setExecutor((CommandExecutor) new BanCountryCommand(this.database));
        this.getCommand("eunbancountry").setExecutor((CommandExecutor) new UnbanCountryCommand(this.database));
        this.getCommand("elistcountries").setExecutor((CommandExecutor) new ListCountryBansCommand(this.database));
        this.getCommand("ewhitelist").setExecutor((CommandExecutor) new WhitelistCommand(this.database));
        this.getCommand("eunwhitelist").setExecutor((CommandExecutor) new UnwhitelistCommand(this.database));
        this.getCommand("elistwhite").setExecutor((CommandExecutor) new ListWhitelistCommand(this.database));
        ConsoleLogger.info("EasyBan enabled; Version: " + this.getDescription().getVersion());

        if (this.settings.isDiscordAuditLogEnabled()) {
            ConsoleLogger.info("Discord Integration Is Still A Early Feature, Please Expect Bugs And Make Sure To Have Discord Core Downloaded And Configed");
            //PluginManager pm = Bukkit.getServer().getPluginManager();
            if (pm.getPlugin("DiscordCore") == null) {
                ConsoleLogger.info("}---------------ERROR---------------{");
                ConsoleLogger.info("Easybans AuditLog Requires Discord Core");
                ConsoleLogger.info("Download it at: https://github.com/RhysB/Discord-Bot-Core");
                ConsoleLogger.info("}---------------ERROR---------------{");
                ConsoleLogger.info("Easyban Is Shutting Down Forcefully");
                pm.disablePlugin(this);
                return;
            }
            if (this.settings.getDiscordChannelID().isEmpty()) {
                ConsoleLogger.info("}---------------ERROR---------------{");
                ConsoleLogger.info("Easybans Discord needs a Channel Defined");
                ConsoleLogger.info("}---------------ERROR---------------{");
                ConsoleLogger.info("Easyban Is Shutting Down Forcefully");
                pm.disablePlugin(this);
                return;
            }
            discord = (DiscordCore) getServer().getPluginManager().getPlugin("DiscordCore");
        }
    }

    private GeoIPLookup getGeoIPLookup() {
        final Plugin pl = this.getServer().getPluginManager().getPlugin("GeoIPTools");
        if (pl != null) {
            return ((GeoIPTools) pl).getGeoIPLookup(364);
        }
        return null;
    }
}
