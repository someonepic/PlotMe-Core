package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.ClearReason;
import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdClear extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.admin.clear") || PlotMe_Core.cPerms(p, "PlotMe.use.clear"))
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
						Plot plot = PlotMeCoreManager.getPlotById(p,id);
						
						if(plot.protect)
						{
							Util.Send(p, RED + Util.C("MsgPlotProtectedCannotClear"));
						}
						else
						{
							String playername = p.getName();
							
							if(plot.owner.equalsIgnoreCase(playername) || PlotMe_Core.cPerms(p, "PlotMe.admin.clear"))
							{
								World w = p.getWorld();
								
								PlotMapInfo pmi = PlotMeCoreManager.getMap(w);
								
								double price = 0;
								
								if(PlotMeCoreManager.isEconomyEnabled(w))
								{
									price = pmi.ClearPrice;
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
										Util.Send(p, RED + Util.C("MsgNotEnoughClear") + " " + Util.C("WordMissing") + " " + RESET + (price - balance) + RED + " " + PlotMe_Core.economy.currencyNamePlural());
										return true;
									}
								}						
								
								PlotMeCoreManager.clear(w, plot, p, ClearReason.Clear);
								//RemoveLWC(w, plot, p);
								//PlotMeCoreManager.regen(w, plot);
								
								//Util.Send(p, Util.C("MsgPlotCleared") + " " + Util.moneyFormat(-price));
								
								if(isAdv)
									PlotMe_Core.self.getLogger().info(LOG + playername + " " + Util.C("MsgClearedPlot") + " " + id + ((price != 0) ? " " + Util.C("WordFor") + " " + price : ""));
							}
							else
							{
								Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgNotYoursNotAllowedClear"));
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
