package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class CmdAuction extends PlotCommand
{
	public CmdAuction(PlotMe_Core instance) {
		super(instance);
	}

	public boolean exec(Player p, String[] args) 
	{
		if(plugin.getPlotMeCoreManager().isEconomyEnabled(p))
		{
			PlotMapInfo pmi = plugin.getPlotMeCoreManager().getMap(p);
			
			if(pmi.CanPutOnSale)
			{
				if(plugin.cPerms(p, "PlotMe.use.auction") || plugin.cPerms(p, "PlotMe.admin.auction"))
				{
					String id = plugin.getPlotMeCoreManager().getPlotId(p.getLocation());
					
					if(id.equals(""))
					{
						p.sendMessage(RED + C("MsgNoPlotFound"));
					}
					else
					{
						if(!plugin.getPlotMeCoreManager().isPlotAvailable(id, p))
						{
							Plot plot = plugin.getPlotMeCoreManager().getPlotById(p,id);
							
							String name = p.getName();
							
							if(plot.owner.equalsIgnoreCase(name) || plugin.cPerms(p, "PlotMe.admin.auction"))
							{
								World w = p.getWorld();
								
								if(plot.auctionned)
								{
									if(!plot.currentbidder.equals("") && !plugin.cPerms(p, "PlotMe.admin.auction"))
									{
										p.sendMessage(RED + C("MsgPlotHasBidsAskAdmin"));
									}
									else
									{
										if(!plot.currentbidder.equals(""))
										{
											EconomyResponse er = plugin.getEconomy().depositPlayer(plot.currentbidder, plot.currentbid);
											
											if(!er.transactionSuccess())
											{
												p.sendMessage(RED + er.errorMessage);
												plugin.getUtil().warn(er.errorMessage);
											}
											else
											{
											    for(Player player : Bukkit.getServer().getOnlinePlayers())
											    {
											        if(player.getName().equalsIgnoreCase(plot.currentbidder))
											        {
											        	player.sendMessage(C("MsgAuctionCancelledOnPlot") + 
											            		" " + id + " " + C("MsgOwnedBy") + " " + plot.owner + ". " + plugin.getUtil().moneyFormat(plot.currentbid));
											            break;
											        }
											    }
											}
										}
										
										plot.auctionned = false;
										plugin.getPlotMeCoreManager().adjustWall(p.getLocation());
										plugin.getPlotMeCoreManager().setSellSign(w, plot);
										plot.currentbid = 0;
										plot.currentbidder = "";
										
										plot.updateField("currentbid", 0);
										plot.updateField("currentbidder", "");
										plot.updateField("auctionned", false);
										
										p.sendMessage(C("MsgAuctionCancelled"));
										
										if(isAdv)
											plugin.getLogger().info(LOG + name + " " + C("MsgStoppedTheAuctionOnPlot") + " " + id);
									}
								}
								else
								{									
									double bid = 1;
									
									if(args.length == 2)
									{
										try  
										{  
											bid = Double.parseDouble(args[1]);  
										}  
										catch( Exception e){}
									}
									
									if(bid < 0)
									{
										p.sendMessage(RED + C("MsgInvalidAmount"));
									}
									else
									{
										plot.currentbid = bid;
										plot.auctionned = true;
										plugin.getPlotMeCoreManager().adjustWall(p.getLocation());
										plugin.getPlotMeCoreManager().setSellSign(w, plot);
										
										plot.updateField("currentbid", bid);
										plot.updateField("auctionned", true);
										
										p.sendMessage(C("MsgAuctionStarted"));
										
										if(isAdv)
											plugin.getLogger().info(LOG + name + " " + C("MsgStartedAuctionOnPlot") + " " + id + " " + C("WordAt") + " " + bid);
									}
								}
							}
							else
							{
								p.sendMessage(RED + C("MsgDoNotOwnPlot"));
							}
						}
						else
						{
							p.sendMessage(RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
						}
					}
				}
				else
				{
					p.sendMessage(RED + C("MsgPermissionDenied"));
				}
			}
			else
			{
				p.sendMessage(RED + C("MsgSellingPlotsIsDisabledWorld"));
			}
		}
		else
		{
			p.sendMessage(RED + C("MsgEconomyDisabledWorld"));
		}
		return true;
	}
}
