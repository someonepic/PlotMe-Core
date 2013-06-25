package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class CmdAuto extends PlotCommand 
{
	public CmdAuto(PlotMe_Core instance) {
		super(instance);
	}

	public boolean exec(Player p, String[] args)
	{
		if (plugin.cPerms(p, "PlotMe.use.auto"))
		{			
			if(!plugin.getPlotMeCoreManager().isPlotWorld(p) && !plugin.getAllowWorldTeleport())
			{
				p.sendMessage(RED + C("MsgNotPlotWorld"));
			}
			else
			{
				World w;
				
				if(!plugin.getPlotMeCoreManager().isPlotWorld(p) && plugin.getAllowWorldTeleport())
				{
					if(args.length == 2)
					{
						w = Bukkit.getWorld(args[1]);
					}
					else
					{
						w = plugin.getPlotMeCoreManager().getFirstWorld();
					}
					
					if(w == null || !plugin.getPlotMeCoreManager().isPlotWorld(w))
					{
						p.sendMessage(RED + args[1] + " " + C("MsgWorldNotPlot"));
						return true;
					}
				}
				else
				{
					w = p.getWorld();
				}
				
				if(w == null)
				{
					p.sendMessage(RED + C("MsgNoPlotworldFound"));
				}
				else
				{
					if(plugin.getPlotMeCoreManager().getNbOwnedPlot(p, w) >= plugin.getPlotLimit(p) && !plugin.cPerms(p, "PlotMe.admin"))
						p.sendMessage(RED + C("MsgAlreadyReachedMaxPlots") + " (" + 
								plugin.getPlotMeCoreManager().getNbOwnedPlot(p, w) + "/" + plugin.getPlotLimit(p) + "). " + C("WordUse") + " " + RED + "/plotme " + C("CommandHome") + RESET + " " + C("MsgToGetToIt"));
					else
					{
						PlotMapInfo pmi = plugin.getPlotMeCoreManager().getMap(w);
						int limit = pmi.PlotAutoLimit;
						
						for(int i = 0; i < limit; i++)
						{
							for(int x = -i; x <= i; x++)
							{
								for(int z = -i; z <= i; z++)
								{
									String id = "" + x + ";" + z;
									
									if(plugin.getPlotMeCoreManager().isPlotAvailable(id, w))
									{									
										String name = p.getName();
										
										double price = 0;
										
										if(plugin.getPlotMeCoreManager().isEconomyEnabled(w))
										{
											price = pmi.ClaimPrice;
											double balance = plugin.getEconomy().getBalance(name);
											
											if(balance >= price)
											{
												EconomyResponse er = plugin.getEconomy().withdrawPlayer(name, price);
												
												if(!er.transactionSuccess())
												{
													p.sendMessage(RED + er.errorMessage);
													Util().warn(er.errorMessage);
													return true;
												}
											}
											else
											{
												p.sendMessage(RED + C("MsgNotEnoughAuto") + " " + C("WordMissing") + " " + RESET + Util().moneyFormat(price - balance, false));
												return true;
											}
										}
										
										plugin.getPlotMeCoreManager().createPlot(w, id, name);
										
										//plugin.getPlotMeCoreManager().adjustLinkedPlots(id, w);
										
										p.teleport(plugin.getPlotMeCoreManager().getPlotHome(w, id));
			
										p.sendMessage(C("MsgThisPlotYours") + " " + C("WordUse") + " " + RED + "/plotme " + C("CommandHome") + RESET + " " + C("MsgToGetToIt") + " " + Util().moneyFormat(-price));
										
										if(isAdv)
											plugin.getLogger().info(LOG + name + " " + C("MsgClaimedPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
										
										return true;
									}
								}
							}
						}
					
						p.sendMessage(RED + C("MsgNoPlotFound1") + " " + (limit^2) + " " + C("MsgNoPlotFound2"));
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
