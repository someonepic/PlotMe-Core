package com.worldcretornica.plotme_core.commands;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdComments extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.use.comments"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
			}
			else
			{
				if(args.length < 2)
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
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || plot.isAllowed(p.getName()) || PlotMe_Core.cPerms(p, "PlotMe.admin"))
							{
								if(plot.comments.size() == 0)
								{
									Util.Send(p, Util.C("MsgNoComments"));
								}
								else
								{
									Util.Send(p, Util.C("MsgYouHave") + " " + 
											AQUA + plot.comments.size() + RESET + " " + Util.C("MsgComments"));
									
									for(String[] comment : plot.comments)
									{
										Util.Send(p, AQUA + Util.C("WordFrom") + " : " + RED + comment[0]);
										Util.Send(p, ITALIC + comment[1]);
									}
									
								}
							}
							else
							{
								Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgNotYoursNotAllowedViewComments"));
							}
						}
						else
						{
							Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgHasNoOwner"));
						}
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
