package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.event.PlotBidEvent;
import com.worldcretornica.plotme_core.event.PlotMeEventFactory;

public class CmdBid extends PlotCommand 
{
	public CmdBid(PlotMe_Core instance) {
		super(instance);
	}

	public boolean exec(Player p, String[] args) 
	{	
		if(plugin.getPlotMeCoreManager().isEconomyEnabled(p))
		{
			if(plugin.cPerms(p, "PlotMe.use.bid"))
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
						
						if(plot.auctionned)
						{
							String bidder = p.getName();
							
							if(plot.owner.equalsIgnoreCase(bidder))
							{
								p.sendMessage(RED + C("MsgCannotBidOwnPlot"));
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
										p.sendMessage(RED + C("MsgInvalidBidMustBeAbove") + " " + RESET + Util().moneyFormat(plot.currentbid, false));
									}
									else
									{
										double balance = plugin.getEconomy().getBalance(bidder);
										
										if(bid >= balance && !currentbidder.equals(bidder) ||
											currentbidder.equals(bidder) && bid > (balance + currentbid))
										{
											p.sendMessage(RED + C("MsgNotEnoughBid"));
										}
										else
										{
											PlotBidEvent event = PlotMeEventFactory.callPlotBidEvent(plugin, p.getWorld(), plot, p, bid);
											
											if(!event.isCancelled())
											{
												EconomyResponse er = plugin.getEconomy().withdrawPlayer(bidder, bid);
												
												if(er.transactionSuccess())
												{
													if(!currentbidder.equals(""))
													{
														EconomyResponse er2 = plugin.getEconomy().depositPlayer(currentbidder, currentbid);
														
														if(!er2.transactionSuccess())
														{
															p.sendMessage(er2.errorMessage);
															Util().warn(er2.errorMessage);
														}
														else
														{
															for(Player player : Bukkit.getServer().getOnlinePlayers())
															{
																if(player.getName().equalsIgnoreCase(currentbidder))
																{
																	player.sendMessage(C("MsgOutbidOnPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + ". " + Util().moneyFormat(bid));
																	break;
																}
															}
														}
													}
													
													plot.currentbidder = bidder;
													plot.currentbid = bid;
													
													plot.updateField("currentbidder", bidder);
													plot.updateField("currentbid", bid);
													
													plugin.getPlotMeCoreManager().setSellSign(p.getWorld(), plot);
													
													p.sendMessage(C("MsgBidAccepted") + " " + Util().moneyFormat(-bid));
													
													if(isAdv)
														plugin.getLogger().info(LOG + bidder + " bid " + bid + " on plot " + id);
												}
												else
												{
													p.sendMessage(er.errorMessage);
													Util().warn(er.errorMessage);
												}
											}
										}
									}
								}
								else
								{
									p.sendMessage(C("WordUsage") + ": " + RED + "/plotme " + 
											C("CommandBid") + " <" + C("WordAmount") + "> " + 
											RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandBid") + " 100");
								}
							}
						}
						else
						{
							p.sendMessage(RED + C("MsgPlotNotAuctionned"));
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
			p.sendMessage(RED + C("MsgEconomyDisabledWorld"));
		}
		return true;
	}
}
