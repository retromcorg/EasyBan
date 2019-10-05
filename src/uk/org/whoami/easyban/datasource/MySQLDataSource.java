package uk.org.whoami.easyban.datasource;

import uk.org.whoami.easyban.settings.*;
import uk.org.whoami.easyban.*;
import java.sql.*;

public class MySQLDataSource extends SQLDataSource
{
    private String databaseName;
    private String host;
    private String port;
    private String username;
    private String password;
    
    public MySQLDataSource(final Settings settings) throws ClassNotFoundException, SQLException {
        this.databaseName = settings.getMySQLDatabaseName();
        this.host = settings.getMySQLHost();
        this.port = settings.getMySQLPort();
        this.username = settings.getMySQLUsername();
        this.password = settings.getMySQLPassword();
        this.connect();
        this.setup();
        ConsoleLogger.info("Database setup finished");
    }
    
    @Override
    protected final synchronized void connect() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        ConsoleLogger.info("MySQL driver loaded");
        this.con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.databaseName, this.username, this.password);
        ConsoleLogger.info("Connected to Database");
    }
    
    @Override
    protected final synchronized void setup() throws SQLException {
        Statement st = null;
        try {
            st = this.con.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS player (player_id INTEGER AUTO_INCREMENT,player VARCHAR(20) NOT NULL,CONSTRAINT player_const_prim PRIMARY KEY (player_id),CONSTRAINT player_const_uniq UNIQUE(player));");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ip (ip_id INTEGER AUTO_INCREMENT,player_id INTEGER NOT NULL,ip VARCHAR(40) NOT NULL,CONSTRAINT ip_const_prim PRIMARY KEY (ip_id),CONSTRAINT ip_const_ref FOREIGN KEY (player_id) REFERENCES player (player_id));");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS player_ban (player_ban_id INTEGER AUTO_INCREMENT,player_id INTEGER NOT NULL,admin VARCHAR(20) NOT NULL,reason VARCHAR(100),until TIMESTAMP,CONSTRAINT player_ban_const_prim PRIMARY KEY (player_ban_id),CONSTRAINT player_ban_const_uniq UNIQUE (player_id),CONSTRAINT player_ban_const_ref FOREIGN KEY (player_id) REFERENCES player (player_id));");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS subnet_ban (subnet_ban_id INTEGER AUTO_INCREMENT,subnet VARCHAR(100) NOT NULL,admin VARCHAR(20) NOT NULL,reason VARCHAR(100),CONSTRAINT subnet_ban_const_prim PRIMARY KEY (subnet_ban_id),CONSTRAINT subnet_ban_const_uniq UNIQUE (subnet));");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS country_ban (country_ban_id INTEGER AUTO_INCREMENT,country VARCHAR(2) NOT NULL,CONSTRAINT country_ban_const_prim PRIMARY KEY (country_ban_id),CONSTRAINT country_ban_const_uniq UNIQUE (country));");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS whitelist (whitelist_id INTEGER AUTO_INCREMENT,player_id INTEGER NOT NULL,CONSTRAINT whitelist_const_prim PRIMARY KEY (whitelist_id),CONSTRAINT whitelist_const_ref FOREIGN KEY (player_id) REFERENCES player (player_id));");
        }
        finally {
            if (st != null) {
                try {
                    st.close();
                }
                catch (SQLException ex) {}
            }
        }
    }
    
    @Override
    public synchronized void close() {
        try {
            this.con.close();
        }
        catch (SQLException ex) {}
    }
}
