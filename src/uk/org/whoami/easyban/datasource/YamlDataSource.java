package uk.org.whoami.easyban.datasource;

import org.bukkit.util.config.*;
import java.io.*;
import uk.org.whoami.easyban.util.*;
import java.net.*;
import java.util.*;

public class YamlDataSource extends Configuration implements DataSource
{
    private final String banPath = "bans";
    private final String historyPath = "history";
    private final String subnetPath = "subnets";
    private final String countryPath = "countries";
    private final String whitelistPath = "whitelist";
    private HashMap<String, List<String>> history;
    private HashMap<String, HashMap<String, String>> bans;
    private HashMap<String, HashMap<String, String>> subnets;
    private ArrayList<String> countries;
    private ArrayList<String> whitelist;
    
    public YamlDataSource() {
        super(new File("./plugins/EasyBan/bans.yml"));
        this.load();
        if (this.getProperty("bans") == null) {
            this.setProperty("bans", (Object)new HashMap());
        }
        if (this.getProperty("history") == null) {
            this.setProperty("history", (Object)new HashMap());
        }
        if (this.getProperty("subnets") == null) {
            this.setProperty("subnets", (Object)new HashMap());
        }
        if (this.getProperty("countries") == null) {
            this.setProperty("countries", (Object)new ArrayList());
        }
        if (this.getProperty("whitelist") == null) {
            this.setProperty("whitelist", (Object)new ArrayList());
        }
        this.history = (HashMap<String, List<String>>)this.getProperty("history");
        this.bans = (HashMap<String, HashMap<String, String>>)this.getProperty("bans");
        this.subnets = (HashMap<String, HashMap<String, String>>)this.getProperty("subnets");
        this.countries = (ArrayList<String>)this.getProperty("countries");
        this.whitelist = (ArrayList<String>)this.getProperty("whitelist");
        this.save();
    }
    
    public synchronized void addIpToHistory(final String nick, final String ip) {
        if (this.history.containsKey(nick)) {
            if (!this.history.get(nick).contains(ip)) {
                this.history.get(nick).add(ip);
            }
        }
        else {
            this.history.put(nick, new ArrayList<String>());
            this.history.get(nick).add(ip);
        }
        this.save();
    }
    
    public synchronized void banNick(final String nick, final String admin, final String reason, final Calendar until) {
        if (!this.bans.containsKey(nick)) {
            final HashMap<String, String> tmp = new HashMap<String, String>();
            tmp.put("admin", admin);
            if (reason != null) {
                tmp.put("reason", reason);
            }
            if (until != null) {
                tmp.put("until", String.valueOf(until.getTimeInMillis()));
            }
            this.bans.put(nick, tmp);
            this.save();
        }
    }
    
    public synchronized void unbanNick(final String nick) {
        if (this.bans.containsKey(nick)) {
            this.bans.remove(nick);
            this.save();
        }
    }
    
    public synchronized void banSubnet(final Subnet subnet, final String admin, final String reason) {
        if (!this.subnets.containsKey(subnet.toString())) {
            final HashMap<String, String> tmp = new HashMap<String, String>();
            tmp.put("admin", admin);
            tmp.put("reason", reason);
            this.subnets.put(subnet.toString(), tmp);
            this.save();
        }
    }
    
    public synchronized void unbanSubnet(final Subnet subnet) {
        if (this.subnets.containsKey(subnet.toString())) {
            this.subnets.remove(subnet.toString());
            this.save();
        }
    }
    
    public synchronized void banCountry(final String code) {
        if (!this.countries.contains(code)) {
            this.countries.add(code);
            this.save();
        }
    }
    
    public synchronized void whitelist(final String player) {
        if (!this.whitelist.contains(player)) {
            this.whitelist.add(player);
            this.save();
        }
    }
    
    public synchronized void unWhitelist(final String player) {
        this.whitelist.remove(player);
    }
    
    public synchronized void unbanCountry(final String code) {
        this.countries.remove(code);
    }
    
    public synchronized boolean isIpBanned(final String ip) {
        for (final String bannedNick : this.bans.keySet()) {
            if (this.history.containsKey(bannedNick) && this.history.get(bannedNick).contains(ip)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSubnetBanned(final String ip) {
        final Iterator<String> itl = this.subnets.keySet().iterator();
        while (itl.hasNext()) {
            try {
                final Subnet subnet = new Subnet(itl.next());
                if (subnet.isIpInSubnet(InetAddress.getByName(ip))) {
                    return true;
                }
                continue;
            }
            catch (UnknownHostException ex) {}
        }
        return false;
    }
    
    public synchronized boolean isNickBanned(final String nick) {
        return this.bans.containsKey(nick);
    }
    
    public synchronized boolean isCountryBanned(final String code) {
        return this.countries.contains(code);
    }
    
    public synchronized boolean isNickWhitelisted(final String player) {
        return this.whitelist.contains(player);
    }
    
    public String[] getHistory(final String nick) {
        if (this.history.containsKey(nick)) {
            return this.history.get(nick).toArray(new String[0]);
        }
        return new String[0];
    }
    
    public synchronized String[] getBannedNicks() {
        return this.bans.keySet().toArray(new String[0]);
    }
    
    public synchronized String[] getBannedSubnets() {
        return this.subnets.keySet().toArray(new String[0]);
    }
    
    public synchronized String[] getBannedCountries() {
        return this.countries.toArray(new String[0]);
    }
    
    public synchronized String[] getWhitelistedNicks() {
        return this.whitelist.toArray(new String[0]);
    }
    
    public String[] getNicks(final String ip) {
        final ArrayList<String> nicks = new ArrayList<String>();
        for (final Map.Entry<String, List<String>> entry : this.history.entrySet()) {
            if (entry.getValue().contains(ip)) {
                nicks.add(entry.getKey());
            }
        }
        return nicks.toArray(new String[0]);
    }
    
    public synchronized HashMap<String, Long> getTempBans() {
        final HashMap<String, Long> tmpBans = new HashMap<String, Long>();
        for (final String nick : this.bans.keySet()) {
            if (this.bans.get(nick).containsKey("until")) {
                tmpBans.put(nick, new Long(this.bans.get(nick).get("until")));
            }
        }
        return tmpBans;
    }
    
    public synchronized HashMap<String, String> getBanInformation(final String nick) {
        if (this.bans.containsKey(nick)) {
            return this.bans.get(nick);
        }
        return null;
    }
    
    public synchronized HashMap<String, String> getBanInformation(final Subnet subnet) {
        if (this.subnets.containsKey(subnet.toString())) {
            return this.subnets.get(subnet.toString());
        }
        return null;
    }
    
    public void close() {
    }
}
