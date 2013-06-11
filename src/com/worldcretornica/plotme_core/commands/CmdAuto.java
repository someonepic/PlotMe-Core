package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdAuto extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.use.auto"))
		{			
			if(!PlotMeCoreManager.isPlotWorld(p) && !PlotMe_Core.allowWorldTeleport)
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
			}
			else
			{
				World w;
				
				if(!PlotMeCoreManager.isPlotWorld(p) && PlotMe_Core.allowWorldTeleport)
				{
					if(args.length == 2)
					{
						w = Bukkit.getWorld(args[1]);
					}
					else
					{
						w = PlotMeCoreManager.getFirstWorld();
					}
					
					if(w == null || !PlotMeCoreManager.isPlotWorld(w))
					{
						Util.Send(p, RED + args[1] + " " + Util.C("MsgWorldNotPlot"));
						return true;
					}
				}
				else
				{
					w = p.getWorld();
				}
				
				if(w == null)
				{
					Util.Send(p, RED + Util.C("MsgNoPlotworldFound"));
				}
				else
				{
					if(PlotMeCoreManager.getNbOwnedPlot(p, w) >= PlotMe_Core.getPlotLimit(p) && !PlotMe_Core.cPerms(p, "PlotMe.admin"))
						Util.Send(p, RED + Util.C("MsgAlreadyReachedMaxPlots") + " (" + 
								PlotMeCoreManager.getNbOwnedPlot(p, w) + "/" + PlotMe_Core.getPlotLimit(p) + "). " + Util.C("WordUse") + " " + RED + "/plotme " + Util.C("CommandHome") + RESET + " " + Util.C("MsgToGetToIt"));
					else
					{
						PlotMapInfo pmi = PlotMeCoreManager.getMap(w);
						int limit = pmi.PlotAutoLimit;
						
						for(int i = 0; i < limit; i++)
						{
							for(int x = -i; x <= i; x++)
							{
								for(int z = -i; z <= i; z++)
								{
									String id = "" + x + ";" + z;
									
									if(PlotMeCoreManager.isPlotAvailable(id, w))
									{									
										String name = p.getName();
										
										double price = 0;
										
										if(PlotMeCoreManager.isEconomyEnabled(w))
										{
											price = pmi.ClaimPrice;
											double balance = PlotMe_Core.economy.getBalance(name);
											
											if(balance >= price)
											{
												EconomyResponse er = PlotMe_Core.economy.withdrawPlayer(name, price);
												
												if(!er.transactionSuccess())
												{
													Util.Send(p, RED + er.errorMessage);
													Util.warn(er.errorMessage);
													return true;
												}
											}
											else
											{
												Util.Send(p, RED + Util.C("MsgNotEnoughAuto") + " " + Util.C("WordMissing") + " " + RESET + Util.moneyFormat(price - balance, false));
												return true;
											}
										}
										
										//Plot plot = PlotMeCoreManager.createPlot(w, id, name);
										
										//PlotMeCoreManager.adjustLinkedPlots(id, w);
										
										p.teleport(PlotMeCoreManager.getPlotHome(w, id));
										
										//p.teleport(new Location(w, PlotMeCoreManager.bottomX(plot.id, w) + (PlotMeCoreManager.topX(plot.id, w) - 
												//PlotMeCoreManager.bottomX(plot.id, w))/2, pmi.RoadHeight + 2, PlotMeCoreManager.bottomZ(plot.id, w) - 2));
			
										Util.Send(p, Util.C("MsgThisPlotYours") + " " + Util.C("WordUse") + " " + RED + "/plotme " + Util.C("CommandHome") + RESET + " " + Util.C("MsgToGetToIt") + " " + Util.moneyFormat(-price));
										
										if(isAdv)
											PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgClaimedPlot") + " " + id + ((price != 0) ? " " + Util.C("WordFor") + " " + price : ""));
										
										return true;
									}
								}
							}
						}
					
						Util.Send(p, RED + Util.C("MsgNoPlotFound1") + " " + (limit^2) + " " + Util.C("MsgNoPlotFound2"));
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
