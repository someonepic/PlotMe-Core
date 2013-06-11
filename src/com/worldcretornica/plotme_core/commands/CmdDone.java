package com.worldcretornica.plotme_core.commands;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdDone extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if(PlotMe_Core.cPerms(p, "PlotMe.use.done") || PlotMe_Core.cPerms(p, "PlotMe.admin.done"))
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
						String name = p.getName();
						
						if(plot.owner.equalsIgnoreCase(name) || PlotMe_Core.cPerms(p, "PlotMe.admin.done"))
						{							
							if(plot.finished)
							{
								plot.setUnfinished();
								Util.Send(p, Util.C("MsgUnmarkFinished"));
								
								if(isAdv)
									PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("WordMarked") + " " + id + " " + Util.C("WordFinished"));
							}
							else
							{
								plot.setFinished();
								Util.Send(p, Util.C("MsgMarkFinished"));
								
								if(isAdv)
									PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("WordMarked") + " " + id + " " + Util.C("WordUnfinished"));
							}
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
