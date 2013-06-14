package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdBiome extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.use.biome"))
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
					p.sendMessage(RED + Util.C("MsgNoPlotFound"));
				}
				else
				{
					if(!PlotMeCoreManager.isPlotAvailable(id, p))
					{
						World w = p.getWorld();
						
						if(args.length == 2)
						{
							org.bukkit.block.Biome biome = null;
							
							for(org.bukkit.block.Biome bio : org.bukkit.block.Biome.values())
							{
								if(bio.name().equalsIgnoreCase(args[1]))
								{
									biome = bio;
								}
							}
							
							if(biome == null)
							{
								Util.Send(p, RED + args[1] + RESET + " " + Util.C("MsgIsInvalidBiome"));
							}
							else
							{
								Plot plot = PlotMeCoreManager.getPlotById(p,id);
								String playername = p.getName();
								
								if(plot.owner.equalsIgnoreCase(playername) || PlotMe_Core.cPerms(p, "PlotMe.admin"))
								{
									PlotMapInfo pmi = PlotMeCoreManager.getMap(w);
									
									double price = 0;
									
									if(PlotMeCoreManager.isEconomyEnabled(w))
									{
										price = pmi.BiomeChangePrice;
										double balance = PlotMe_Core.economy.getBalance(playername);
										
										if(balance >= price)
										{
											EconomyResponse er = PlotMe_Core.economy.withdrawPlayer(playername, price);
											
											if(!er.transactionSuccess())
											{
												Util.Send(p, RED + er.errorMessage);
												Util.warn(er.errorMessage);
												return true;
											}
										}
										else
										{
											Util.Send(p, RED + Util.C("MsgNotEnoughBiome") + " " + Util.C("WordMissing") + " " + RESET + Util.moneyFormat(price - balance, false));
											return true;
										}
									}
									
									PlotMeCoreManager.setBiome(w, id, biome);
									plot.biome = biome;
									
									
								
									Util.Send(p, Util.C("MsgBiomeSet") + " " + ChatColor.BLUE + Util.FormatBiome(biome.name()) + " " + Util.moneyFormat(-price));
									
									if(isAdv)
									{
										PlotMe_Core.self.getLogger().info(LOG + playername + " " + Util.C("MsgChangedBiome") + " " + id + " " + Util.C("WordTo") + " " + 
												Util.FormatBiome(biome.name()) + ((price != 0) ? " " + Util.C("WordFor") + " " + price : ""));
									}
								}
								else
								{
									Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgNotYoursNotAllowedBiome"));
								}
							}
						}
						else
						{
							Plot plot = PlotMe_Core.plotmaps.get(w.getName().toLowerCase()).getPlot(id);
							
							Util.Send(p, Util.C("MsgPlotUsingBiome") + " " + ChatColor.BLUE + Util.FormatBiome(plot.biome.name()));
						}
					}
					else
					{
						Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgHasNoOwner"));
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
