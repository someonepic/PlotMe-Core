package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdHome extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.use.home") || PlotMe_Core.cPerms(p, "PlotMe.admin.home.other"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p) && !PlotMe_Core.allowWorldTeleport)
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
			}
			else
			{
				boolean found = false;
				String playername = p.getName();
				int nb = 1;
				World w;
				
				if(!PlotMeCoreManager.isPlotWorld(p) && PlotMe_Core.allowWorldTeleport)
				{
					w = PlotMeCoreManager.getFirstWorld();
				}
				else
				{
					w = p.getWorld();
				}
				
				if(args[0].contains(":"))
				{
					try{
						if(args[0].split(":").length == 1 || args[0].split(":")[1].equals(""))
						{
							Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandHome") + ":# " + 
									RESET + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandHome") + ":1");
							return true;
						}
						else
						{
							nb = Integer.parseInt(args[0].split(":")[1]);
						}
					}catch(Exception ex)
					{
						Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandHome") + ":# " + 
								RESET + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandHome") + ":1");
						return true;
					}
				}
				
				if(args.length == 2)
				{
					if(Bukkit.getWorld(args[1]) == null)
					{
						if(PlotMe_Core.cPerms(p, "PlotMe.admin.home.other"))
						{
							playername = args[1];
						}
					}
					else
					{
						w = Bukkit.getWorld(args[1]);
					}
				}
				
				if(args.length == 3)
				{
					if(Bukkit.getWorld(args[2]) == null)
					{
						Util.Send(p, RED + args[2] + Util.C("MsgWorldNotPlot"));
						return true;
					}
					else
					{
						w = Bukkit.getWorld(args[2]);
					}
				}
				
				if(!PlotMeCoreManager.isPlotWorld(w))
				{
						Util.Send(p, RED + w.getName() + Util.C("MsgWorldNotPlot"));
				}
				else
				{
					int i = nb - 1;
							
					for(Plot plot : PlotMeCoreManager.getPlots(w).values())
					{
						if(plot.owner.equalsIgnoreCase(playername))
						{
							if(i == 0)
							{							
								PlotMapInfo pmi = PlotMeCoreManager.getMap(w);
								
								double price = 0;
														
								if(PlotMeCoreManager.isEconomyEnabled(w))
								{
									price = pmi.PlotHomePrice;
									double balance = PlotMe_Core.economy.getBalance(playername);
									
									if(balance >= price)
									{
										EconomyResponse er = PlotMe_Core.economy.withdrawPlayer(playername, price);
										
										if(!er.transactionSuccess())
										{
											Util.Send(p, RED + er.errorMessage);
											return true;
										}
									}
									else
									{
										Util.Send(p, RED + Util.C("MsgNotEnoughTp") + " " + Util.C("WordMissing") + " " + RESET + Util.moneyFormat(price - balance, false));
										return true;
									}
								}
								
								p.teleport(PlotMeCoreManager.getPlotHome(w, plot.id));
								
								if(price != 0)
									Util.Send(p, Util.moneyFormat(-price));
								
								return true;
							}else{
								i--;
							}
						}
					}
					
					if(!found)
					{
						if(nb > 0)
						{
							if(!playername.equalsIgnoreCase(p.getName()))
							{
								Util.Send(p, RED + playername + " " + Util.C("MsgDoesNotHavePlot") + " #" + nb);
							}else{
								Util.Send(p, RED + Util.C("MsgPlotNotFound") + " #" + nb);
							}
						}
						else if(!playername.equalsIgnoreCase(p.getName()))
						{
							Util.Send(p, RED + playername + " " + Util.C("MsgDoesNotHavePlot"));
						}
						else
						{
							Util.Send(p, RED + Util.C("MsgYouHaveNoPlot"));
						}
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
