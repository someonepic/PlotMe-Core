package com.worldcretornica.plotme_core.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdReload extends PlotCommand 
{
	public boolean exec(CommandSender s, String[] args)
	{
		if (!(s instanceof Player) || PlotMe_Core.cPerms((Player) s, "PlotMe.admin.reload"))
		{
			PlotMe_Core.self.initialize();
			Util.Send(s, Util.C("MsgReloadedSuccess"));
			
			if(isAdv)
				PlotMe_Core.self.getLogger().info(LOG + s.getName() + " " + Util.C("MsgReloadedConfigurations"));
		}
		else
		{
			Util.Send(s, RED + Util.C("MsgPermissionDenied"));
		}
		return true;
	}
}
