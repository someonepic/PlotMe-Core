package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.SqlManager;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdDispose extends PlotCommand 
{
	public boolean exec(Player p, String[] args) 
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.admin.dispose") || PlotMe_Core.cPerms(p, "PlotMe.use.dispose"))
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
							Util.Send(p, RED + Util.C("MsgPlotProtectedNotDisposed"));
						}
						else
						{
							String name = p.getName();
							
							if(plot.owner.equalsIgnoreCase(name) || PlotMe_Core.cPerms(p, "PlotMe.admin.dispose"))
							{
								PlotMapInfo pmi = PlotMeCoreManager.getMap(p);
								
								double cost = pmi.DisposePrice;
								
								if(PlotMeCoreManager.isEconomyEnabled(p))
								{
									if(cost != 0 && PlotMe_Core.economy.getBalance(name) < cost)
									{
										Util.Send(p, RED + Util.C("MsgNotEnoughDispose"));
										return true;
									}
									
									EconomyResponse er = PlotMe_Core.economy.withdrawPlayer(name, cost);
									
									if(!er.transactionSuccess())
									{	
										Util.Send(p, RED + er.errorMessage);
										Util.warn(er.errorMessage);
										return true;
									}
								
									if(plot.auctionned)
									{
										String currentbidder = plot.currentbidder;
										
										if(!currentbidder.equals(""))
										{
											EconomyResponse er2 = PlotMe_Core.economy.depositPlayer(currentbidder, plot.currentbid);
											
											if(!er2.transactionSuccess())
											{
												Util.Send(p, RED + er2.errorMessage);
												Util.warn(er2.errorMessage);
											}
											else
											{
											    for(Player player : Bukkit.getServer().getOnlinePlayers())
											    {
											        if(player.getName().equalsIgnoreCase(currentbidder))
											        {
											            Util.Send(player, Util.C("WordPlot") + 
											            		" " + id + " " + Util.C("MsgOwnedBy") + " " + plot.owner + " " + Util.C("MsgWasDisposed") + " " + Util.moneyFormat(cost));
											            break;
											        }
											    }
											}
										}
									}
								}
									
								World w = p.getWorld();
								
								if(!PlotMeCoreManager.isPlotAvailable(id, p))
								{
									PlotMeCoreManager.removePlot(w, id);
								}
								
								PlotMeCoreManager.removeOwnerSign(w, id);
								PlotMeCoreManager.removeSellSign(w, id);
								PlotMeCoreManager.removeAuctionSign(w, id);
								
								SqlManager.deletePlot(PlotMeCoreManager.getIdX(id), PlotMeCoreManager.getIdZ(id), w.getName().toLowerCase());
								
								Util.Send(p, Util.C("MsgPlotDisposedAnyoneClaim"));
								
								if(isAdv)
									PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgDisposedPlot") + " " + id);
							}
							else
							{
								Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgNotYoursCannotDispose"));
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
