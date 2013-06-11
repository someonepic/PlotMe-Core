package com.worldcretornica.plotme_core.commands;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdAddTime extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.addtime"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
				return true;
			}
			else
			{
				String id = PlotMeCoreManager.getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					Util.Send(p, RED + Util.C("MsgNoPlotFound"));
				}
				else
				{
					if(!PlotMeCoreManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotMeCoreManager.getPlotById(p,id);
						
						if(plot != null)
						{
							String name = p.getName();
							
							plot.resetExpire(PlotMeCoreManager.getMap(p).DaysToExpiration);
							Util.Send(p, Util.C("MsgPlotExpirationReset"));
							
							if(isAdv)
								PlotMe_Core.self.getLogger().info(LOG + name + " reset expiration on plot " + id);
						}
					}
					else
					{
						Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgHasNoOwner"));
					}
				}
			}
		}
		else
		{
			Util.Send(p, RED + Util.C("MsgPermissionDenied"));
		}
		return true;
	}
}
