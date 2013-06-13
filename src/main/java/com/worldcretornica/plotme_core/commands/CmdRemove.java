package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdRemove extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.admin.remove") || PlotMe_Core.cPerms(p, "PlotMe.use.remove"))
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
					Util.Send(p, RED + Util.C("MsgNoPlotFound"));
				}
				else
				{
					if(!PlotMeCoreManager.isPlotAvailable(id, p))
					{
						if(args.length < 2 || args[1].equalsIgnoreCase(""))
						{
							Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandRemove") + " <" + Util.C("WordPlayer") + ">");
						}
						else
						{
							Plot plot = PlotMeCoreManager.getPlotById(p,id);
							String playername = p.getName();
							String allowed = args[1];
							
							if(plot.owner.equalsIgnoreCase(playername) || PlotMe_Core.cPerms(p, "PlotMe.admin.remove"))
							{
								if(plot.isAllowed(allowed))
								{
									
									World w = p.getWorld();
									
									PlotMapInfo pmi = PlotMeCoreManager.getMap(w);
									
									double price = 0;
									
									if(PlotMeCoreManager.isEconomyEnabled(w))
									{
										price = pmi.RemovePlayerPrice;
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
											Util.Send(p, RED + Util.C("MsgNotEnoughRemove") + " " + Util.C("WordMissing") + " " + RESET + Util.moneyFormat(price - balance, false));
											return true;
										}
									}
									
									plot.removeAllowed(allowed);
																	
									Util.Send(p, Util.C("WordPlayer") + " " + RED + allowed + RESET + " " + Util.C("WorldRemoved") + ". " + Util.moneyFormat(-price));
									
									if(isAdv)
										PlotMe_Core.self.getLogger().info(LOG + playername + " " + Util.C("MsgRemovedPlayer") + " " + allowed + " " + Util.C("MsgFromPlot") + " " + id + ((price != 0) ? " " + Util.C("WordFor") + " " + price : ""));
								}
								else
								{
									Util.Send(p, Util.C("WordPlayer") + " " + RED + args[1] + RESET + " " + Util.C("MsgWasNotAllowed"));
								}
							}
							else
							{
								Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgNotYoursNotAllowedRemove"));
							}
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
