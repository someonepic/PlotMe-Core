package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdBuy extends PlotCommand 
{
	public boolean exec(Player p, String[] args) 
	{
		if(PlotMeCoreManager.isEconomyEnabled(p))
		{
			if(PlotMe_Core.cPerms(p, "PlotMe.use.buy") || PlotMe_Core.cPerms(p, "PlotMe.admin.buy"))
			{
				Location l = p.getLocation();
				String id = PlotMeCoreManager.getPlotId(l);
				
				if(id.equals(""))
				{
					Util.Send(p, RED + Util.C("MsgNoPlotFound"));
				}
				else
				{
					if(!PlotMeCoreManager.isPlotAvailable(id, p))
					{
						Plot plot = PlotMeCoreManager.getPlotById(p,id);
						
						if(!plot.forsale)
						{
							Util.Send(p, RED + Util.C("MsgPlotNotForSale"));
						}
						else
						{
							String buyer = p.getName();
							
							if(plot.owner.equalsIgnoreCase(buyer))
							{
								Util.Send(p, RED + Util.C("MsgCannotBuyOwnPlot"));
							}
							else
							{
								int plotlimit = PlotMe_Core.getPlotLimit(p);
								
								if(plotlimit != -1 && PlotMeCoreManager.getNbOwnedPlot(p) >= plotlimit)
								{
									Util.Send(p, Util.C("MsgAlreadyReachedMaxPlots") + " (" + 
											PlotMeCoreManager.getNbOwnedPlot(p) + "/" + PlotMe_Core.getPlotLimit(p) + "). " + 
											Util.C("WordUse") + " " + RED + "/plotme " + Util.C("CommandHome") + RESET + " " + Util.C("MsgToGetToIt"));
								}
								else
								{
									World w = p.getWorld();
									
									double cost = plot.customprice;
									
									if(PlotMe_Core.economy.getBalance(buyer) < cost)
									{
										Util.Send(p, RED + Util.C("MsgNotEnoughBuy"));
									}
									else
									{
										EconomyResponse er = PlotMe_Core.economy.withdrawPlayer(buyer, cost);
										
										if(er.transactionSuccess())
										{
											String oldowner = plot.owner;
											
											if(!oldowner.equalsIgnoreCase("$Bank$"))
											{
												EconomyResponse er2 = PlotMe_Core.economy.depositPlayer(oldowner, cost);
												
												if(!er2.transactionSuccess())
												{
													Util.Send(p, RED + er2.errorMessage);
													Util.warn(er2.errorMessage);
												}
												else
												{
													for(Player player : Bukkit.getServer().getOnlinePlayers())
													{
														if(player.getName().equalsIgnoreCase(oldowner))
														{
															Util.Send(player, Util.C("WordPlot") + " " + id + " " + 
																	Util.C("MsgSoldTo") + " " + buyer + ". " + Util.moneyFormat(cost));
															break;
														}
													}
												}
											}
											
											plot.owner = buyer;
											plot.customprice = 0;
											plot.forsale = false;
											
											plot.updateField("owner", buyer);
											plot.updateField("customprice", 0);
											plot.updateField("forsale", false);
											
											PlotMeCoreManager.adjustWall(l);
											PlotMeCoreManager.setSellSign(w, plot);
											PlotMeCoreManager.setOwnerSign(w, plot);
											
											Util.Send(p, Util.C("MsgPlotBought") + " " + Util.moneyFormat(-cost));
											
											if(isAdv)
												PlotMe_Core.self.getLogger().info(LOG + buyer + " " + Util.C("MsgBoughtPlot") + " " + id + " " + Util.C("WordFor") + " " + cost);
										}
										else
										{
											Util.Send(p, RED + er.errorMessage);
											Util.warn(er.errorMessage);
										}
									}
								}
							}
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
