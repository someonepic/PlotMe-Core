package com.worldcretornica.plotme_core.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdTP extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.admin.tp"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p) && !PlotMe_Core.allowWorldTeleport)
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
			}
			else
			{
				if(args.length == 2 || (args.length == 3 && PlotMe_Core.allowWorldTeleport))
				{
					String id = args[1];
					
					World w;
					
					if(args.length == 3)
					{
						String world = args[2];
						
						w = Bukkit.getWorld(world);
					}
					else
					{
						if(!PlotMeCoreManager.isPlotWorld(p))
						{
							w = PlotMeCoreManager.getFirstWorld();
						}
						else
						{
							w = p.getWorld();
						}
					}
					
					if(!PlotMeCoreManager.isValidId(w, id))
					{
						if(PlotMe_Core.allowWorldTeleport)
							Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandTp") + " <" + Util.C("WordId") + "> [" + Util.C("WordWorld") + "] " + RESET + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandTp") + " 5;-1 ");
						else
							Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandTp") + " <" + Util.C("WordId") + "> " + RESET + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandTp") + " 5;-1 ");
						return true;
					}
					else
					{
						if(w == null || !PlotMeCoreManager.isPlotWorld(w))
						{
							Util.Send(p, RED + Util.C("MsgNoPlotworldFound"));
						}
						else
						{
							//Location bottom = PlotMeCoreManager.getPlotBottomLoc(w, id);
							//Location top = PlotMeCoreManager.getPlotTopLoc(w, id);
							
							//p.teleport(new Location(w, bottom.getX() + (top.getBlockX() - bottom.getBlockX())/2, PlotMeCoreManager.getMap(w).RoadHeight + 2, bottom.getZ() - 2));
							p.teleport(PlotMeCoreManager.getPlotHome(w, id));
						}
					}
				}
				else
				{
					if(PlotMe_Core.allowWorldTeleport)
						Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandTp") + " <" + Util.C("WordId") + "> [" + Util.C("WordWorld") + "] " + RESET + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandTp") + " 5;-1 ");
					else
						Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandTp") + " <" + Util.C("WordId") + "> " + RESET + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandTp") + " 5;-1 ");
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
