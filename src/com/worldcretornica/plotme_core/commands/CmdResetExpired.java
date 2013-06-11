package com.worldcretornica.plotme_core.commands;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.PlotRunnableDeleteExpire;
import com.worldcretornica.plotme_core.Util;

public class CmdResetExpired extends PlotCommand 
{
	public boolean exec(CommandSender s, String[] args)
	{
		if(PlotMe_Core.cPerms(s, "PlotMe.admin.resetexpired"))
		{
			if(args.length <= 1)
			{
				Util.Send(s, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandResetExpired") + " <" + Util.C("WordWorld") + "> " + RESET + "Example: " + RED + "/plotme " + Util.C("CommandResetExpired") + " plotworld ");
			}
			else
			{
				if(PlotMe_Core.worldcurrentlyprocessingexpired != null)
				{
					Util.Send(s, PlotMe_Core.cscurrentlyprocessingexpired.getName() + " " + Util.C("MsgAlreadyProcessingPlots"));
				}
				else
				{
					World w = s.getServer().getWorld(args[1]);
					
					if(w == null)
					{
						Util.Send(s, RED + Util.C("WordWorld") + " '" + args[1] + "' " + Util.C("MsgDoesNotExistOrNotLoaded"));
						return true;
					}
					else
					{					
						if(!PlotMeCoreManager.isPlotWorld(w))
						{
							Util.Send(s, RED + Util.C("MsgNotPlotWorld"));
							return true;
						}
						else
						{
							PlotMe_Core.worldcurrentlyprocessingexpired = w;
							PlotMe_Core.cscurrentlyprocessingexpired = s;
							PlotMe_Core.counterexpired = 50;
							PlotMe_Core.nbperdeletionprocessingexpired = 5;
							
							PlotMe_Core.self.scheduleTask(new PlotRunnableDeleteExpire(), 5, 50);
						}
					}
				}
			}
		}
		else
		{
			Util.Send(s, RED + Util.C("MsgPermissionDenied"));
		}
		return true;
	}
}
