package com.worldcretornica.plotme_core.commands;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.PlotMe_Core;

public class CmdWEAnywhere extends PlotCommand 
{
	public CmdWEAnywhere(PlotMe_Core instance) 
	{
		super(instance);
	}

	public boolean exec(Player p, String[] args)
	{
		if(plugin.cPerms(p, "PlotMe.admin.weanywhere"))
		{
			String name = p.getName();
			
			if(plugin.getPlotMeCoreManager().isPlayerIgnoringWELimit(p.getName()) && !plugin.getDefaultWEAnywhere() || 
					!plugin.getPlotMeCoreManager().isPlayerIgnoringWELimit(p.getName()) && plugin.getDefaultWEAnywhere())
			{
				plugin.getPlotMeCoreManager().removePlayerIgnoringWELimit(p.getName());
			}
			else
			{
				plugin.getPlotMeCoreManager().addPlayerIgnoringWELimit(p.getName());					
			}
			
			if(plugin.getPlotMeCoreManager().isPlayerIgnoringWELimit(p.getName()))
			{
				p.sendMessage(C("MsgWorldEditAnywhere"));
				
				if(isAdv)
					plugin.getLogger().info(LOG + name + " enabled WorldEdit anywhere");
			}
			else
			{
				p.sendMessage(C("MsgWorldEditInYourPlots"));
				
				if(isAdv)
					plugin.getLogger().info(LOG + name + " disabled WorldEdit anywhere");
			}
		}
		else
		{
			p.sendMessage(RED + C("MsgPermissionDenied"));
		}
		return true;
	}
}
