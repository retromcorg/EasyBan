package uk.org.whoami.easyban;

import java.util.logging.*;

public class ConsoleLogger
{
    private static final Logger log;
    
    public static void info(final String message) {
        ConsoleLogger.log.info("[EasyBan] " + message);
    }
    
    static {
        log = Logger.getLogger("Minecraft");
    }
}
