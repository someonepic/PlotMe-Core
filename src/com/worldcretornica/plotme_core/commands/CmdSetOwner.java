package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdSetOwner extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.admin.setowner"))
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
					if(args.length < 2 || args[1].equals(""))
					{
						Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandSetowner") + " <" + Util.C("WordPlayer") + ">");
					}
					else
					{
						String newowner = args[1];
						String oldowner = "<" + Util.C("WordNotApplicable") + ">";
						String playername = p.getName();
						
						if(!PlotMeCoreManager.isPlotAvailable(id, p))
						{								
							Plot plot = PlotMeCoreManager.getPlotById(p,id);
							
							PlotMapInfo pmi = PlotMeCoreManager.getMap(p);
							oldowner = plot.owner;
							
							if(PlotMeCoreManager.isEconomyEnabled(p))
							{
								if(pmi.RefundClaimPriceOnSetOwner && newowner != oldowner)
								{
									EconomyResponse er = PlotMe_Core.economy.depositPlayer(oldowner, pmi.ClaimPrice);
									
									if(!er.transactionSuccess())
									{
										Util.Send(p, RED + er.errorMessage);
										Util.warn(er.errorMessage);
										return true;
									}
									else
									{
									    for(Player player : Bukkit.getServer().getOnlinePlayers())
									    {
									        if(player.getName().equalsIgnoreCase(oldowner))
									        {
									            Util.Send(player, Util.C("MsgYourPlot") + " " + id + " " + Util.C("MsgNowOwnedBy") + " " + newowner + ". " + Util.moneyFormat(pmi.ClaimPrice));
									            break;
									        }
									    }
									}
								}
								
								if(!plot.currentbidder.equals(""))
								{
									EconomyResponse er = PlotMe_Core.economy.depositPlayer(plot.currentbidder, plot.currentbid);
									
									if(!er.transactionSuccess())
									{
										Util.Send(p, er.errorMessage);
										Util.warn(er.errorMessage);
									}
									else
									{
									    for(Player player : Bukkit.getServer().getOnlinePlayers())
									    {
									        if(player.getName().equalsIgnoreCase(plot.currentbidder))
									        {
									            Util.Send(player, Util.C("WordPlot") + " " + id + " " + Util.C("MsgChangedOwnerFrom") + " " + oldowner + " " + Util.C("WordTo") + " " + newowner + ". " + Util.moneyFormat(plot.currentbid));
									            break;
									        }
									    }
									}
								}
							}
							
							plot.currentbidder = "";
							plot.currentbid = 0;
							plot.auctionned = false;
							plot.forsale = false;
							
							PlotMeCoreManager.setSellSign(p.getWorld(), plot);
							
							plot.updateField("currentbidder", "");
							plot.updateField("currentbid", 0);
							plot.updateField("auctionned", false);
							plot.updateField("forsale", false);
					
							plot.owner = newowner;
							
							PlotMeCoreManager.setOwnerSign(p.getWorld(), plot);
							
							plot.updateField("owner", newowner);
						}
						else
						{
							PlotMeCoreManager.createPlot(p.getWorld(), id,newowner);
						}
						
						Util.Send(p, Util.C("MsgOwnerChangedTo") + " " + RED + newowner);
						
						if(isAdv)
							PlotMe_Core.self.getLogger().info(LOG + playername + " " + Util.C("MsgChangedOwnerOf") + " " + id + " " + Util.C("WordFrom") + " " + oldowner + " " + Util.C("WordTo") + " " + newowner);
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
