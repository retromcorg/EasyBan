package uk.org.whoami.easyban.commands;

import uk.org.whoami.easyban.settings.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import uk.org.whoami.easyban.permission.*;

public abstract class EasyBanCommand implements CommandExecutor
{
    protected String admin;
    protected Message m;
    
    public EasyBanCommand() {
        this.m = Message.getInstance();
    }
    
    protected abstract void execute(final CommandSender p0, final Command p1, final String p2, final String[] p3);
    
    public boolean onCommand(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        this.admin = ((cs instanceof Player) ? ((Player)cs).getName() : "Console");
        if (Permission.hasPermission(cs, cmd)) {
            this.execute(cs, cmnd, cmd, args);
        }
        return true;
    }
    
    protected final void sendListToSender(final CommandSender sender, final String[] list) {
        for (int i = 0; i < list.length; i += 4) {
            String send = "";
            for (int y = 0; y < 4 && i + y < list.length; ++y) {
                send = send + list[i + y] + ", ";
            }
            sender.sendMessage(send);
        }
    }
}
