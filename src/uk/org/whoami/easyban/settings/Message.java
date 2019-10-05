package uk.org.whoami.easyban.settings;

import org.bukkit.util.config.*;
import java.io.*;
import java.util.*;

public class Message extends Configuration
{
    private static Message singleton;
    private final HashMap<String, String> map;
    
    private Message() {
        super(new File("./plugins/EasyBan/messages.yml"));
        this.map = new HashMap<String, String>();
        this.loadDefaults();
        this.getMessages();
    }
    
    private void loadDefaults() {
        this.map.put(" has been kicked", " &chas been kicked");
        this.map.put("You have been kicked", "&cYou have been kicked");
        this.map.put(" has been banned", " &chas been banned");
        this.map.put("You have been banned", "&cYou have been banned");
        this.map.put(" has been unbanned", " &chas been unbanned");
        this.map.put("Invalid Subnet", "&cInvalid Subnet");
        this.map.put("Banned players: ", "&cBanned players: ");
        this.map.put("Banned subnets: ", "&cBanned subnets: ");
        this.map.put("Ips from ", "&cIps from ");
        this.map.put(" is not banned", " &cis not banned");
        this.map.put(" is banned", " &cis banned");
        this.map.put("Reason: ", "&cReason: ");
        this.map.put("Until: ", "&cUntil: ");
        this.map.put("Admin: ", "&cAdmin: ");
        this.map.put("Wrong time format", "&cWrong time format");
        this.map.put("You are banned until: ", "&cYou are banned until: ");
        this.map.put("You are banned", "&cYou are banned");
        this.map.put("Your country has been banned", "&cYour country has been banned");
        this.map.put("Temporary bans: ", "&cTemporary bans: ");
        this.map.put("A country has been banned: ", "&cA country has been banned: ");
        this.map.put("A country has been unbanned: ", "&cA country has been unbanned: ");
        this.map.put("Banned countries: ", "&cBanned countries: ");
        this.map.put(" has been whitelisted", " &chas been whitelisted");
        this.map.put(" has been removed from the whitelist", " &chas been removed from the whitelist");
        this.map.put("Whitelist: ", "&cWhitelist: ");
        this.map.put("Alternative nicks of ", "&cAlternative nicks of ");
        this.map.put("Your subnet is banned", "&cYour subnet is banned");
        this.map.put("Users who connected from IP", "&cUsers who connected from IP");
        this.map.put("You have been banned by ", "&cYou have been banned by ");
        this.map.put("custom_kick", "&cComplain on http://example.com");
        this.map.put("custom_ban", "&cComplain on http://example.com");
    }
    
    public String _(final String message) {
        final String ret = this.map.get(message);
        if (ret != null) {
            return ret.replace("&", "§");
        }
        return message;
    }
    
    private void getMessages() {
        this.load();
        for (final String key : this.map.keySet()) {
            if (this.getString(key) == null) {
                this.setProperty(key, (Object)this.map.get(key));
            }
            else {
                this.map.put(key, this.getString(key));
            }
        }
        this.save();
    }
    
    public static Message getInstance() {
        if (Message.singleton == null) {
            Message.singleton = new Message();
        }
        return Message.singleton;
    }
    
    static {
        Message.singleton = null;
    }
}
