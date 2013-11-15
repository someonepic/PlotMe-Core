package com.worldcretornica.plotme_core.commands;

import com.worldcretornica.plotme_core.PlotMe_Core;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdResetWorld extends PlotCommand {

    public CmdResetWorld(PlotMe_Core instance) {
        super(instance);
    }

    public boolean exec(CommandSender s, String[] args) {
        if (!(s instanceof Player) || plugin.cPerms((Player) s, "PlotMe.admin.resetworld")) {
            //TODO resetworld code
            //TODO resetworld event

            if (isAdv) {
                plugin.getLogger().info(LOG + s.getName() + " " + C("MsgReloadedConfigurations"));
            }
        } else {
            s.sendMessage(RED + C("MsgPermissionDenied"));
        }
        return true;
    }
}
