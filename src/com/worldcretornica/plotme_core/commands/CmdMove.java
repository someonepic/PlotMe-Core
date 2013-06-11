package com.worldcretornica.plotme_core.commands;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdMove extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.admin.move"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
			}
			else
			{
				if(args.length < 3 || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase(""))
				{
					Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandMove") + " <" + Util.C("WordIdFrom") + "> <" + Util.C("WordIdTo") + "> " + 
							RESET + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandMove") + " 0;1 2;-1");
				}
				else
				{
					String plot1 = args[1];
					String plot2 = args[2];
					World w = p.getWorld();
					
					if(!PlotMeCoreManager.isValidId(w, plot1) || !PlotMeCoreManager.isValidId(w, plot2))
					{
						Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandMove") + " <" + Util.C("WordIdFrom") + "> <" + Util.C("WordIdTo") + "> " + 
								RESET + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandMove") + " 0;1 2;-1");
						return true;
					}
					else
					{
						if(PlotMeCoreManager.movePlot(p.getWorld(), plot1, plot2))
						{
							Util.Send(p, Util.C("MsgPlotMovedSuccess"));
							
							if(isAdv)
								PlotMe_Core.self.getLogger().info(LOG + p.getName() + " " + Util.C("MsgExchangedPlot") + " " + plot1 + " " + Util.C("MsgAndPlot") + " " + plot2);
						}
						else
							Util.Send(p, RED + Util.C("ErrMovingPlot"));
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
