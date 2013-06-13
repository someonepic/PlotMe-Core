package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdAuction extends PlotCommand
{
	public boolean exec(Player p, String[] args) 
	{
		if(PlotMeCoreManager.isEconomyEnabled(p))
		{
			PlotMapInfo pmi = PlotMeCoreManager.getMap(p);
			
			if(pmi.CanPutOnSale)
			{
				if(PlotMe_Core.cPerms(p, "PlotMe.use.auction") || PlotMe_Core.cPerms(p, "PlotMe.admin.auction"))
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
							
							if(plot.owner.equalsIgnoreCase(name) || PlotMe_Core.cPerms(p, "PlotMe.admin.auction"))
							{
								World w = p.getWorld();
								
								if(plot.auctionned)
								{
									if(!plot.currentbidder.equalsIgnoreCase("") && !PlotMe_Core.cPerms(p, "PlotMe.admin.auction"))
									{
										Util.Send(p, RED + Util.C("MsgPlotHasBidsAskAdmin"));
									}
									else
									{
										if(!plot.currentbidder.equalsIgnoreCase(""))
										{
											EconomyResponse er = PlotMe_Core.economy.depositPlayer(plot.currentbidder, plot.currentbid);
											
											if(!er.transactionSuccess())
											{
												Util.Send(p, RED + er.errorMessage);
												Util.warn(er.errorMessage);
											}
											else
											{
											    for(Player player : Bukkit.getServer().getOnlinePlayers())
											    {
											        if(player.getName().equalsIgnoreCase(plot.currentbidder))
											        {
											        	Util.Send(player, Util.C("MsgAuctionCancelledOnPlot") + 
											            		" " + id + " " + Util.C("MsgOwnedBy") + " " + plot.owner + ". " + Util.moneyFormat(plot.currentbid));
											            break;
											        }
											    }
											}
										}
										
										plot.auctionned = false;
										PlotMeCoreManager.adjustWall(p.getLocation());
										PlotMeCoreManager.setSellSign(w, plot);
										plot.currentbid = 0;
										plot.currentbidder = "";
										
										plot.updateField("currentbid", 0);
										plot.updateField("currentbidder", "");
										plot.updateField("auctionned", false);
										
										Util.Send(p, Util.C("MsgAuctionCancelled"));
										
										if(isAdv)
											PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgStoppedTheAuctionOnPlot") + " " + id);
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
										Util.Send(p, RED + Util.C("MsgInvalidAmount"));
									}
									else
									{
										plot.currentbid = bid;
										plot.auctionned = true;
										PlotMeCoreManager.adjustWall(p.getLocation());
										PlotMeCoreManager.setSellSign(w, plot);
										
										plot.updateField("currentbid", bid);
										plot.updateField("auctionned", true);
										
										Util.Send(p, Util.C("MsgAuctionStarted"));
										
										if(isAdv)
											PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgStartedAuctionOnPlot") + " " + id + " " + Util.C("WordAt") + " " + bid);
									}
								}
							}
							else
							{
								Util.Send(p, RED + Util.C("MsgDoNotOwnPlot"));
							}
						}
						else
						{
							Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgHasNoOwner"));
						}
					}
				}
				else
				{
					Util.Send(p, RED + Util.C("MsgPermissionDenied"));
				}
			}
			else
			{
				Util.Send(p, RED + Util.C("MsgSellingPlotsIsDisabledWorld"));
			}
		}
		else
		{
			Util.Send(p, RED + Util.C("MsgEconomyDisabledWorld"));
		}
		return true;
	}
}
