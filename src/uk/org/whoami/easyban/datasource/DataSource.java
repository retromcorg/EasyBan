package uk.org.whoami.easyban.datasource;

import uk.org.whoami.easyban.util.*;
import java.util.*;

public interface DataSource
{
    void addIpToHistory(final String p0, final String p1);
    
    void banNick(final String p0, final String p1, final String p2, final Calendar p3);
    
    void unbanNick(final String p0);
    
    void banSubnet(final Subnet p0, final String p1, final String p2);
    
    void unbanSubnet(final Subnet p0);
    
    void banCountry(final String p0);
    
    void unbanCountry(final String p0);
    
    void whitelist(final String p0);
    
    void unWhitelist(final String p0);
    
    boolean isIpBanned(final String p0);
    
    boolean isSubnetBanned(final String p0);
    
    boolean isNickBanned(final String p0);
    
    boolean isCountryBanned(final String p0);
    
    boolean isNickWhitelisted(final String p0);
    
    String[] getHistory(final String p0);
    
    String[] getBannedNicks();
    
    String[] getBannedSubnets();
    
    String[] getBannedCountries();
    
    String[] getWhitelistedNicks();
    
    String[] getNicks(final String p0);
    
    HashMap<String, Long> getTempBans();
    
    HashMap<String, String> getBanInformation(final String p0);
    
    HashMap<String, String> getBanInformation(final Subnet p0);
    
    void close();
}
