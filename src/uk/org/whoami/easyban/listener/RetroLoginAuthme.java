package uk.org.whoami.easyban.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import uk.org.whoami.authme.event.AuthLoginEvent;
import uk.org.whoami.easyban.datasource.DataSource;
import uk.org.whoami.easyban.settings.Message;

public class RetroLoginAuthme extends CustomEventListener implements Listener {
    private DataSource database;
    private Message msg;

    public RetroLoginAuthme(DataSource database) {
        this.database = database;
        this.msg = Message.getInstance();
    }

    public void onCustomEvent(Event event) {
        if(event instanceof AuthLoginEvent) {
            Player p = ((AuthLoginEvent) event).getPlayer();
            if(p == null) {
                return;
            }
            System.out.println("Player Login Recieved From Authme: " + p.getName());
            this.database.addIpToHistory(p.getName(), p.getAddress().getAddress().getHostAddress());
        }


    }
}