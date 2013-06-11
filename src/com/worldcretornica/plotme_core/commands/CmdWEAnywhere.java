package com.worldcretornica.plotme_core.commands;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdWEAnywhere extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.weanywhere"))
		{
			String name = p.getName();
			
			if(PlotMe_Core.isIgnoringWELimit(p) && !PlotMe_Core.defaultWEAnywhere || !PlotMe_Core.isIgnoringWELimit(p) && PlotMe_Core.defaultWEAnywhere)
			{
				PlotMe_Core.removeIgnoreWELimit(p);
			}
			else
			{
				PlotMe_Core.addIgnoreWELimit(p);					
			}
			
			if(PlotMe_Core.isIgnoringWELimit(p))
			{
				Util.Send(p, Util.C("MsgWorldEditAnywhere"));
				
				if(isAdv)
					PlotMe_Core.self.getLogger().info(LOG + name + " enabled WorldEdit anywhere");
			}
			else
			{
				Util.Send(p, Util.C("MsgWorldEditInYourPlots"));
				
				if(isAdv)
					PlotMe_Core.self.getLogger().info(LOG + name + " disabled WorldEdit anywhere");
			}
		}
		else
		{
			Util.Send(p, RED + Util.C("MsgPermissionDenied"));
		}
		return true;
	}
}
