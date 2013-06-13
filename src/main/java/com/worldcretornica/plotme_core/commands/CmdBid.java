package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdBid extends PlotCommand 
{
	public boolean exec(Player p, String[] args) 
	{	
		if(PlotMeCoreManager.isEconomyEnabled(p))
		{
			if(PlotMe_Core.cPerms(p, "PlotMe.use.bid"))
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
						
						if(plot.auctionned)
						{
							String bidder = p.getName();
							
							if(plot.owner.equalsIgnoreCase(bidder))
							{
								Util.Send(p, RED + Util.C("MsgCannotBidOwnPlot"));
							}
							else
							{
								if(args.length == 2)
								{
									double bid = 0;
									double currentbid = plot.currentbid;
									String currentbidder = plot.currentbidder;
									
									try  
									{  
										bid = Double.parseDouble(args[1]);  
									}  
									catch( Exception e){}
																		
									if(bid < currentbid || (bid == currentbid && !currentbidder.equals("")))
									{
										Util.Send(p, RED + Util.C("MsgInvalidBidMustBeAbove") + " " + RESET + Util.moneyFormat(plot.currentbid, false));
									}
									else
									{
										double balance = PlotMe_Core.economy.getBalance(bidder);
										
										if(bid >= balance && !currentbidder.equals(bidder) ||
											currentbidder.equals(bidder) && bid > (balance + currentbid))
										{
											Util.Send(p, RED + Util.C("MsgNotEnoughBid"));
										}
										else
										{
											EconomyResponse er = PlotMe_Core.economy.withdrawPlayer(bidder, bid);
											
											if(er.transactionSuccess())
											{
												if(!currentbidder.equals(""))
												{
													EconomyResponse er2 = PlotMe_Core.economy.depositPlayer(currentbidder, currentbid);
													
													if(!er2.transactionSuccess())
													{
														Util.Send(p, er2.errorMessage);
														Util.warn(er2.errorMessage);
													}
													else
													{
														for(Player player : Bukkit.getServer().getOnlinePlayers())
														{
															if(player.getName().equalsIgnoreCase(currentbidder))
															{
																Util.Send(player, Util.C("MsgOutbidOnPlot") + " " + id + " " + Util.C("MsgOwnedBy") + " " + plot.owner + ". " + Util.moneyFormat(bid));
																break;
															}
														}
													}
												}
												
												plot.currentbidder = bidder;
												plot.currentbid = bid;
												
												plot.updateField("currentbidder", bidder);
												plot.updateField("currentbid", bid);
												
												PlotMeCoreManager.setSellSign(p.getWorld(), plot);
												
												Util.Send(p, Util.C("MsgBidAccepted") + " " + Util.moneyFormat(-bid));
												
												if(isAdv)
													PlotMe_Core.self.getLogger().info(LOG + bidder + " bid " + bid + " on plot " + id);
											}
											else
											{
												Util.Send(p, er.errorMessage);
												Util.warn(er.errorMessage);
											}
										}
									}
								}
								else
								{
									Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + 
											Util.C("CommandBid") + " <" + Util.C("WordAmount") + "> " + 
											RESET + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandBid") + " 100");
								}
							}
						}
						else
						{
							Util.Send(p, RED + Util.C("MsgPlotNotAuctionned"));
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
			Util.Send(p, RED + Util.C("MsgEconomyDisabledWorld"));
		}
		return true;
	}
}
