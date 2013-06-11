package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdProtect extends PlotCommand
{
	public boolean exec(Player p, String[] args) 
	{
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.protect") || PlotMe_Core.cPerms(p, "PlotMe.use.protect"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
				return true;
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
						
						String name = p.getName();
						
						if(plot.owner.equalsIgnoreCase(name) || PlotMe_Core.cPerms(p, "PlotMe.admin.protect"))
						{
							if(plot.protect)
							{
								plot.protect = false;
								PlotMeCoreManager.adjustWall(p.getLocation());
								
								plot.updateField("protected", false);
								
								Util.Send(p, Util.C("MsgPlotNoLongerProtected"));
								
								if(isAdv)
									PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgUnprotectedPlot") + " " + id);
							}
							else
							{
								PlotMapInfo pmi = PlotMeCoreManager.getMap(p);
								
								double cost = 0;
								
								if(PlotMeCoreManager.isEconomyEnabled(p))
								{
									cost = pmi.ProtectPrice;
									
									if(PlotMe_Core.economy.getBalance(name) < cost)
									{
										Util.Send(p, RED + Util.C("MsgNotEnoughProtectPlot"));
										return true;
									}
									else
									{
										EconomyResponse er = PlotMe_Core.economy.withdrawPlayer(name, cost);
										
										if(!er.transactionSuccess())
										{
											Util.Send(p, RED + er.errorMessage);
											Util.warn(er.errorMessage);
											return true;
										}
									}
								
								}
								
								plot.protect = true;
								PlotMeCoreManager.adjustWall(p.getLocation());
								
								plot.updateField("protected", true);
								
								Util.Send(p, Util.C("MsgPlotNowProtected") + " " + Util.moneyFormat(-cost));
								
								if(isAdv)
									PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgProtectedPlot") + " " + id);
								
							}
						}
						else
						{
							Util.Send(p, RED + Util.C("MsgDoNotOwnPlot"));
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
