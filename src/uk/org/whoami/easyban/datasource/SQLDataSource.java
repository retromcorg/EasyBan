package uk.org.whoami.easyban.datasource;

import uk.org.whoami.easyban.*;
import uk.org.whoami.easyban.util.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public abstract class SQLDataSource implements DataSource
{
    protected Connection con;
    
    protected abstract void connect() throws ClassNotFoundException, SQLException;
    
    protected abstract void setup() throws SQLException;
    
    @Override
    public abstract void close();
    
    private synchronized void createNick(final String nick) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("SELECT player FROM player WHERE player=?");
            pst.setString(1, nick);
            if (!pst.executeQuery().next()) {
                pst.close();
                pst = this.con.prepareStatement("INSERT INTO player (player) VALUES(?);");
                pst.setString(1, nick);
                pst.executeUpdate();
            }
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex) {}
            }
        }
    }
    
    @Override
    public synchronized void addIpToHistory(final String nick, final String ip) {
        PreparedStatement pst = null;
        try {
            this.createNick(nick);
            pst = this.con.prepareStatement("SELECT ip FROM ip WHERE player_id=(SELECT player_id FROM player WHERE player= ?) AND ip=?;");
            pst.setString(1, nick);
            pst.setString(2, ip);
            if (!pst.executeQuery().next()) {
                pst.close();
                pst = this.con.prepareStatement("INSERT INTO ip (player_id,ip) VALUES((SELECT player_id FROM player WHERE player= ? ),?);");
                pst.setString(1, nick);
                pst.setString(2, ip);
                pst.executeUpdate();
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized void banNick(final String nick, final String admin, final String reason, final Calendar until) {
        PreparedStatement pst = null;
        try {
            this.createNick(nick);
            pst = this.con.prepareStatement("INSERT INTO player_ban (player_id,admin,reason,until) VALUES((SELECT player_id FROM player WHERE player= ? ),?,?,?);");
            pst.setString(1, nick);
            pst.setString(2, admin);
            if (reason != null) {
                pst.setString(3, reason);
            }
            else {
                pst.setNull(3, 12);
            }
            if (until != null) {
                pst.setTimestamp(4, new Timestamp(until.getTimeInMillis()));
            }
            else {
                pst.setTimestamp(4, new Timestamp(100000L));
            }
            pst.executeUpdate();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized void unbanNick(final String nick) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("DELETE FROM player_ban WHERE player_id=(SELECT player_id FROM player WHERE player=?);");
            pst.setString(1, nick);
            pst.executeUpdate();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized void banSubnet(final Subnet subnet, final String admin, final String reason) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("INSERT INTO subnet_ban (subnet,admin,reason) VALUES(?,?,?);");
            pst.setString(1, subnet.toString());
            pst.setString(2, admin);
            if (reason != null) {
                pst.setString(3, reason);
            }
            else {
                pst.setNull(3, 12);
            }
            pst.executeUpdate();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized void unbanSubnet(final Subnet subnet) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("DELETE FROM subnet_ban WHERE subnet=?;");
            pst.setString(1, subnet.toString());
            pst.executeUpdate();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized void banCountry(final String code) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("INSERT INTO country_ban (country) VALUES(?);");
            pst.setString(1, code);
            pst.executeUpdate();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized void unbanCountry(final String code) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("DELETE FROM country_ban WHERE country=?;");
            pst.setString(1, code);
            pst.executeUpdate();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized void whitelist(final String nick) {
        PreparedStatement pst = null;
        try {
            this.createNick(nick);
            pst = this.con.prepareStatement("INSERT INTO whitelist (player_id) VALUES(SELECT player_id FROM player WHERE player=?);");
            pst.setString(1, nick);
            pst.executeUpdate();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized void unWhitelist(final String nick) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("DELETE FROM whitelist WHERE player_id=(SELECT player_id FROM player WHERE player=?);");
            pst.setString(1, nick);
            pst.executeUpdate();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized boolean isIpBanned(final String ip) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("SELECT ip FROM ip WHERE player_id IN (SELECT player_id FROM player_ban) AND ip=?;");
            pst.setString(1, ip);
            return pst.executeQuery().next();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
            return false;
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized boolean isSubnetBanned(final String ip) {
        Statement st = null;
        try {
            st = this.con.createStatement();
            final ResultSet rs = st.executeQuery("SELECT subnet FROM subnet_ban;");
            while (rs.next()) {
                try {
                    final Subnet sub = new Subnet(rs.getString(1));
                    if (sub.isIpInSubnet(InetAddress.getByName(ip))) {
                        return true;
                    }
                    continue;
                }
                catch (UnknownHostException ex2) {}
            }
            return false;
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
            return false;
        }
        finally {
            if (st != null) {
                try {
                    st.close();
                }
                catch (SQLException ex3) {}
            }
        }
    }
    
    @Override
    public synchronized boolean isNickBanned(final String nick) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("SELECT player_id FROM player_ban WHERE player_id=(SELECT player_id FROM player WHERE player=?);");
            pst.setString(1, nick);
            return pst.executeQuery().next();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
            return false;
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized boolean isCountryBanned(final String code) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("SELECT country FROM country_ban WHERE country=?;");
            pst.setString(1, code);
            return pst.executeQuery().next();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
            return false;
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized boolean isNickWhitelisted(final String nick) {
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("SELECT player_id FROM whitelist WHERE player_id=(SELECT player_id FROM player WHERE player=?);");
            pst.setString(1, nick);
            return pst.executeQuery().next();
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
            return false;
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized String[] getHistory(final String nick) {
        final ArrayList<String> list = new ArrayList<String>();
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("SELECT ip FROM ip WHERE player_id=(SELECT player_id FROM player WHERE player=?);");
            pst.setString(1, nick);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
        return list.toArray(new String[0]);
    }
    
    @Override
    public synchronized String[] getBannedNicks() {
        final ArrayList<String> list = new ArrayList<String>();
        Statement st = null;
        try {
            st = this.con.createStatement();
            final ResultSet rs = st.executeQuery("SELECT player FROM player WHERE player_id IN (SELECT player_id FROM player_ban);");
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (st != null) {
                try {
                    st.close();
                }
                catch (SQLException ex2) {}
            }
        }
        return list.toArray(new String[0]);
    }
    
    @Override
    public synchronized String[] getBannedSubnets() {
        final ArrayList<String> list = new ArrayList<String>();
        Statement st = null;
        try {
            st = this.con.createStatement();
            final ResultSet rs = st.executeQuery("SELECT subnet FROM subnet_ban;");
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (st != null) {
                try {
                    st.close();
                }
                catch (SQLException ex2) {}
            }
        }
        return list.toArray(new String[0]);
    }
    
    @Override
    public synchronized String[] getBannedCountries() {
        final ArrayList<String> list = new ArrayList<String>();
        Statement st = null;
        try {
            st = this.con.createStatement();
            final ResultSet rs = st.executeQuery("SELECT country FROM country_ban;");
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (st != null) {
                try {
                    st.close();
                }
                catch (SQLException ex2) {}
            }
        }
        return list.toArray(new String[0]);
    }
    
    @Override
    public synchronized String[] getWhitelistedNicks() {
        final ArrayList<String> list = new ArrayList<String>();
        Statement st = null;
        try {
            st = this.con.createStatement();
            final ResultSet rs = st.executeQuery("SELECT player FROM player WHERE player_id IN (SELECT player_id FROM whitelist);");
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (st != null) {
                try {
                    st.close();
                }
                catch (SQLException ex2) {}
            }
        }
        return list.toArray(new String[0]);
    }
    
    @Override
    public synchronized String[] getNicks(final String ip) {
        final ArrayList<String> list = new ArrayList<String>();
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("SELECT player FROM player WHERE player_id IN (SELECT player_id FROM ip WHERE ip=?);");
            pst.setString(1, ip);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
        return list.toArray(new String[0]);
    }
    
    @Override
    public synchronized HashMap<String, Long> getTempBans() {
        final HashMap<String, Long> map = new HashMap<String, Long>();
        Statement st = null;
        try {
            st = this.con.createStatement();
            final ResultSet rs = st.executeQuery("SELECT player,until FROM player_ban JOIN player ON player_ban.player_id=player.player_id WHERE until IS NOT NULL;");
            while (rs.next()) {
                if (rs.getTimestamp(2).getTime() == 100000L) {
                    continue;
                }
                map.put(rs.getString(1), rs.getTimestamp(2).getTime());
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (st != null) {
                try {
                    st.close();
                }
                catch (SQLException ex2) {}
            }
        }
        return map;
    }
    
    @Override
    public synchronized HashMap<String, String> getBanInformation(final String nick) {
        final HashMap<String, String> map = new HashMap<String, String>();
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("SELECT admin,reason,until FROM player_ban WHERE player_id=(SELECT player_id FROM player WHERE player=?);");
            pst.setString(1, nick);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                map.put("admin", rs.getString(1));
                if (rs.getString(2) != null) {
                    map.put("reason", rs.getString(2));
                }
                if (rs.getTimestamp(3) != null) {
                    map.put("until", String.valueOf(rs.getTimestamp(3).getTime()));
                }
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
        return map;
    }
    
    @Override
    public synchronized HashMap<String, String> getBanInformation(final Subnet subnet) {
        final HashMap<String, String> map = new HashMap<String, String>();
        PreparedStatement pst = null;
        try {
            pst = this.con.prepareStatement("SELECT admin,reason FROM subnet_ban WHERE subnet=?;");
            pst.setString(1, subnet.toString());
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                map.put("admin", rs.getString(1));
                if (rs.getString(2) != null) {
                    map.put("reason", rs.getString(2));
                }
            }
        }
        catch (SQLException ex) {
            ConsoleLogger.info(ex.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex2) {}
            }
        }
        return map;
    }
}
