package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class CmdBiome extends PlotCommand 
{
	public CmdBiome(PlotMe_Core instance) {
		super(instance);
	}

	public boolean exec(Player p, String[] args)
	{
		if (plugin.cPerms(p, "PlotMe.use.biome"))
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
					if(!plugin.getPlotMeCoreManager().isPlotAvailable(id, p))
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
								p.sendMessage(RED + args[1] + RESET + " " + C("MsgIsInvalidBiome"));
							}
							else
							{
								Plot plot = plugin.getPlotMeCoreManager().getPlotById(p,id);
								String playername = p.getName();
								
								if(plot.owner.equalsIgnoreCase(playername) || plugin.cPerms(p, "PlotMe.admin"))
								{
									PlotMapInfo pmi = plugin.getPlotMeCoreManager().getMap(w);
									
									double price = 0;
									
									if(plugin.getPlotMeCoreManager().isEconomyEnabled(w))
									{
										price = pmi.BiomeChangePrice;
										double balance = plugin.getEconomy().getBalance(playername);
										
										if(balance >= price)
										{
											EconomyResponse er = plugin.getEconomy().withdrawPlayer(playername, price);
											
											if(!er.transactionSuccess())
											{
												p.sendMessage(RED + er.errorMessage);
												Util().warn(er.errorMessage);
												return true;
											}
										}
										else
										{
											p.sendMessage(RED + C("MsgNotEnoughBiome") + " " + C("WordMissing") + " " + RESET + Util().moneyFormat(price - balance, false));
											return true;
										}
									}
									
									plugin.getPlotMeCoreManager().setBiome(w, id, biome);
									plot.biome = biome;
									
									
								
									p.sendMessage(C("MsgBiomeSet") + " " + ChatColor.BLUE + Util().FormatBiome(biome.name()) + " " + Util().moneyFormat(-price));
									
									if(isAdv)
									{
										plugin.getLogger().info(LOG + playername + " " + C("MsgChangedBiome") + " " + id + " " + C("WordTo") + " " + 
												Util().FormatBiome(biome.name()) + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
									}
								}
								else
								{
									p.sendMessage(RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedBiome"));
								}
							}
						}
						else
						{
							Plot plot = plugin.getPlotMeCoreManager().getMap(w).getPlot(id);
							
							p.sendMessage(C("MsgPlotUsingBiome") + " " + ChatColor.BLUE + Util().FormatBiome(plot.biome.name()));
						}
					}
					else
					{
						p.sendMessage(RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
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
