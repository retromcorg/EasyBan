package uk.org.whoami.easyban.commands;

import org.bukkit.command.*;
import uk.org.whoami.easyban.settings.*;
import uk.org.whoami.easyban.*;
import org.bukkit.entity.*;

public class KickCommand extends EasyBanCommand
{
    @Override
    protected void execute(final CommandSender cs, final Command cmnd, final String cmd, final String[] args) {
        if (args.length == 0) {
            return;
        }
        final Player player = cs.getServer().getPlayer(args[0]);
        if (player != null) {
            final String name = player.getDisplayName();
            String kickmsg = this.m._("You have been kicked");
            String reason = "";
            if (args.length > 1) {
                kickmsg = kickmsg + " " + this.m._("Reason: ");
                for (int i = 1; i < args.length; ++i) {
                    kickmsg = kickmsg + args[i] + " ";
                    reason = reason + args[i] + " ";
                }
            }
            if (Settings.getInstance().isAppendCustomKickMessageEnabled()) {
                kickmsg = kickmsg + " " + this.m._("custom_kick");
            }
            player.kickPlayer(kickmsg);
            final Settings settings = Settings.getInstance();
            if (settings.isKickPublic()) {
                cs.getServer().broadcastMessage(name + this.m._(" has been kicked"));
                if (!reason.isEmpty() && settings.isKickReasonPublic()) {
                    cs.getServer().broadcastMessage(name + this.m._("Reason: ") + reason);
                }
            }
            ConsoleLogger.info(player.getName() + " has been kicked by " + this.admin);
        }
    }
}
