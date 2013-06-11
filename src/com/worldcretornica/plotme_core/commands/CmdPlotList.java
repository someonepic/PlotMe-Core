package com.worldcretornica.plotme_core.commands;

import java.util.Calendar;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdPlotList extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if(PlotMe_Core.cPerms(p, "PlotMe.use.list"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
				return true;
			}
			else
			{
				String name;
				
				if(PlotMe_Core.cPerms(p, "PlotMe.admin.list") && args.length == 2)
				{
					name = args[1];
					Util.Send(p, Util.C("MsgListOfPlotsWhere") + " " + AQUA + name + RESET + " " + Util.C("MsgCanBuild"));
				}
				else
				{
					name = p.getName();
					Util.Send(p, Util.C("MsgListOfPlotsWhereYou"));
				}
								
				for(Plot plot : PlotMeCoreManager.getPlots(p).values())
				{
					StringBuilder addition = new StringBuilder();
						
					if(plot.expireddate != null)
					{
						java.util.Date tempdate = plot.expireddate; 
						
						if(tempdate.compareTo(Calendar.getInstance().getTime()) < 0)
						{
							addition.append(RED + " @" + plot.expireddate.toString() + RESET);
						}
						else
						{
							addition.append(" @" + plot.expireddate.toString());
						}
					}
					
					if(plot.auctionned)
					{
						addition.append(" " + Util.C("WordAuction") + ": " + GREEN + Util.round(plot.currentbid) + RESET + ((!plot.currentbidder.equals("")) ? " " + plot.currentbidder : "") );
					}
					
					if(plot.forsale)
					{
						addition.append(" " + Util.C("WordSell") + ": " + GREEN + Util.round(plot.customprice) + RESET);
					}
						
					if(plot.owner.equalsIgnoreCase(name))
					{
						if(plot.allowedcount() == 0)
						{
							if(name.equalsIgnoreCase(p.getName()))
								p.sendMessage("  " + plot.id + " -> " + AQUA + ITALIC + Util.C("WordYours") + RESET + addition);
							else
								p.sendMessage("  " + plot.id + " -> " + AQUA + ITALIC + plot.owner + RESET + addition);
						}
						else
						{
							StringBuilder helpers = new StringBuilder();
							for(int i = 0 ; i < plot.allowedcount(); i++)
							{
								helpers.append(AQUA).append(plot.allowed().toArray()[i]).append(RESET).append(", ");
							}
							if(helpers.length() > 2)
							{
								helpers.delete(helpers.length() - 2, helpers.length());
							}
							
							if(name.equalsIgnoreCase(p.getName()))
							{
								p.sendMessage("  " + plot.id + " -> " + AQUA + ITALIC + Util.C("WordYours") + RESET + addition + ", " + Util.C("WordHelpers") + ": " + helpers);
							}
							else
							{
								p.sendMessage("  " + plot.id + " -> " + AQUA + ITALIC + plot.owner + RESET + addition + ", " + Util.C("WordHelpers") + ": " + helpers);
							}
						}
					}
					else if(plot.isAllowed(name))
					{
						StringBuilder helpers = new StringBuilder();
						for(int i = 0 ; i < plot.allowedcount(); i++)
						{
							if(p.getName().equalsIgnoreCase((String) plot.allowed().toArray()[i]))
							{
								if(name.equalsIgnoreCase(p.getName()))
								{
									helpers.append(AQUA).append(ITALIC).append("You").append(RESET).append(", ");
								}
								else
								{
									helpers.append(AQUA).append(ITALIC).append(name).append(RESET).append(", ");
								}
							}
							else
							{
								helpers.append(AQUA).append(plot.allowed().toArray()[i]).append(RESET).append(", ");
							}
						}
						if(helpers.length() > 2)
						{
							helpers.delete(helpers.length() - 2, helpers.length());
						}
						
						if(plot.owner.equalsIgnoreCase(p.getName()))
						{
							p.sendMessage("  " + plot.id + " -> " + AQUA + Util.C("WordYours") + RESET + addition + ", " + Util.C("WordHelpers") + ": " + helpers);
						}
						else
						{
							p.sendMessage("  " + plot.id + " -> " + AQUA + plot.owner + Util.C("WordPossessive") + RESET + addition + ", " + Util.C("WordHelpers") + ": " + helpers);
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
