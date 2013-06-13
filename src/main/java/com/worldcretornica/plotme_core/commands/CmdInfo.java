package com.worldcretornica.plotme_core.commands;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdInfo extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.use.info"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
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
						
						p.sendMessage(GREEN + Util.C("InfoId") + ": " + AQUA + id + 
								GREEN + " " + Util.C("InfoOwner") + ": " + AQUA + plot.owner + 
								GREEN + " " + Util.C("InfoBiome") + ": " + AQUA + Util.FormatBiome(plot.biome.name()));
						
						p.sendMessage(GREEN + Util.C("InfoExpire") + ": " + AQUA + ((plot.expireddate == null) ? Util.C("WordNever") : plot.expireddate.toString()) +
								GREEN + " " + Util.C("InfoFinished") + ": " + AQUA + ((plot.finished) ? Util.C("WordYes") : Util.C("WordNo")) +
								GREEN + " " + Util.C("InfoProtected") + ": " + AQUA + ((plot.protect) ? Util.C("WordYes") : Util.C("WordNo")));
						
						if(plot.allowedcount() > 0)
						{
							p.sendMessage(GREEN + Util.C("InfoHelpers") + ": " + AQUA + plot.getAllowed());
						}
						
						if(PlotMe_Core.allowToDeny && plot.deniedcount() > 0)
						{
							p.sendMessage(GREEN + Util.C("InfoDenied") + ": " + AQUA + plot.getDenied());
						}
						
						if(PlotMeCoreManager.isEconomyEnabled(p))
						{
							if(plot.currentbidder.equalsIgnoreCase(""))
							{
								p.sendMessage(GREEN + Util.C("InfoAuctionned") + ": " + AQUA + ((plot.auctionned) ? Util.C("WordYes") + 
										GREEN + " " + Util.C("InfoMinimumBid") + ": " + AQUA + Util.round(plot.currentbid) : Util.C("WordNo")) +
										GREEN + " " + Util.C("InfoForSale") + ": " + AQUA + ((plot.forsale) ? AQUA + Util.round(plot.customprice) : Util.C("WordNo")));
							}
							else
							{
								p.sendMessage(GREEN + Util.C("InfoAuctionned") + ": " + AQUA + ((plot.auctionned) ? Util.C("WordYes") + 
										GREEN + " " + Util.C("InfoBidder") + ": " + AQUA + plot.currentbidder + 
										GREEN + " " + Util.C("InfoBid") + ": " + AQUA + Util.round(plot.currentbid) : Util.C("WordNo")) +
										GREEN + " " + Util.C("InfoForSale") + ": " + AQUA + ((plot.forsale) ? AQUA + Util.round(plot.customprice) : Util.C("WordNo")));
							}
						}
					}
					else
					{
						Util.Send(p, RED + Util.C("MsgThisPlot") + " (" + id + ") " + Util.C("MsgHasNoOwner"));
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
