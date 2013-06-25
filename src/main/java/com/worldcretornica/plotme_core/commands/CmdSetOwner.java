package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class CmdSetOwner extends PlotCommand 
{
	public CmdSetOwner(PlotMe_Core instance) {
		super(instance);
	}

	public boolean exec(Player p, String[] args)
	{
		if (plugin.cPerms(p, "PlotMe.admin.setowner"))
		{
			if(!plugin.getPlotMeCoreManager().isPlotWorld(p))
			{
				p.sendMessage(RED + C("MsgNotPlotWorld"));
			}
			else
			{
				String id = plugin.getPlotMeCoreManager().getPlotId(p.getLocation());
				if(id.equals(""))
				{
					p.sendMessage(RED + C("MsgNoPlotFound"));
				}
				else
				{
					if(args.length < 2 || args[1].equals(""))
					{
						p.sendMessage(C("WordUsage") + ": " + RED + "/plotme " + C("CommandSetowner") + " <" + C("WordPlayer") + ">");
					}
					else
					{
						String newowner = args[1];
						String oldowner = "<" + C("WordNotApplicable") + ">";
						String playername = p.getName();
						
						if(!plugin.getPlotMeCoreManager().isPlotAvailable(id, p))
						{								
							Plot plot = plugin.getPlotMeCoreManager().getPlotById(p,id);
							
							PlotMapInfo pmi = plugin.getPlotMeCoreManager().getMap(p);
							oldowner = plot.owner;
							
							if(plugin.getPlotMeCoreManager().isEconomyEnabled(p))
							{
								if(pmi.RefundClaimPriceOnSetOwner && newowner != oldowner)
								{
									EconomyResponse er = plugin.getEconomy().depositPlayer(oldowner, pmi.ClaimPrice);
									
									if(!er.transactionSuccess())
									{
										p.sendMessage(RED + er.errorMessage);
										Util().warn(er.errorMessage);
										return true;
									}
									else
									{
									    for(Player player : Bukkit.getServer().getOnlinePlayers())
									    {
									        if(player.getName().equalsIgnoreCase(oldowner))
									        {
									            player.sendMessage(C("MsgYourPlot") + " " + id + " " + C("MsgNowOwnedBy") + " " + newowner + ". " + Util().moneyFormat(pmi.ClaimPrice));
									            break;
									        }
									    }
									}
								}
								
								if(!plot.currentbidder.equals(""))
								{
									EconomyResponse er = plugin.getEconomy().depositPlayer(plot.currentbidder, plot.currentbid);
									
									if(!er.transactionSuccess())
									{
										p.sendMessage(er.errorMessage);
										Util().warn(er.errorMessage);
									}
									else
									{
									    for(Player player : Bukkit.getServer().getOnlinePlayers())
									    {
									        if(player.getName().equalsIgnoreCase(plot.currentbidder))
									        {
									            player.sendMessage(C("WordPlot") + " " + id + " " + C("MsgChangedOwnerFrom") + " " + oldowner + " " + C("WordTo") + " " + newowner + ". " + Util().moneyFormat(plot.currentbid));
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
							
							plugin.getPlotMeCoreManager().setSellSign(p.getWorld(), plot);
							
							plot.updateField("currentbidder", "");
							plot.updateField("currentbid", 0);
							plot.updateField("auctionned", false);
							plot.updateField("forsale", false);
					
							plot.owner = newowner;
							
							plugin.getPlotMeCoreManager().setOwnerSign(p.getWorld(), plot);
							
							plot.updateField("owner", newowner);
						}
						else
						{
							plugin.getPlotMeCoreManager().createPlot(p.getWorld(), id,newowner);
						}
						
						p.sendMessage(C("MsgOwnerChangedTo") + " " + RED + newowner);
						
						if(isAdv)
							plugin.getLogger().info(LOG + playername + " " + C("MsgChangedOwnerOf") + " " + id + " " + C("WordFrom") + " " + oldowner + " " + C("WordTo") + " " + newowner);
					}
				}
			}
		}
		else
		{
			p.sendMessage(RED + C("MsgPermissionDenied"));
		}
		return true;
	}
}
