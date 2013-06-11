package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.SqlManager;
import com.worldcretornica.plotme_core.Util;

public class CmdReset extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.admin.reset") || PlotMe_Core.cPerms(p, "PlotMe.use.reset"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
			}
			else
			{
				Plot plot = PlotMeCoreManager.getPlotById(p.getLocation());
				
				if(plot == null)
				{
					Util.Send(p, RED + Util.C("MsgNoPlotFound"));
				}
				else
				{
					if(plot.protect)
					{
						Util.Send(p, RED + Util.C("MsgPlotProtectedCannotReset"));
					}
					else
					{
						String playername = p.getName();
						String id = plot.id;
						
						if(plot.owner.equalsIgnoreCase(playername) || PlotMe_Core.cPerms(p, "PlotMe.admin.reset"))
						{						
							World w = p.getWorld();
							
							PlotMeCoreManager.setBiome(w, id, Biome.PLAINS);
							PlotMeCoreManager.clear(w, plot);
							//RemoveLWC(w, plot);
							
							if(PlotMeCoreManager.isEconomyEnabled(p))
							{
								if(plot.auctionned)
								{
									String currentbidder = plot.currentbidder;
									
									if(!currentbidder.equals(""))
									{
										EconomyResponse er = PlotMe_Core.economy.depositPlayer(currentbidder, plot.currentbid);
										
										if(!er.transactionSuccess())
										{
											Util.Send(p, er.errorMessage);
											Util.warn(er.errorMessage);
										}
										else
										{
										    for(Player player : Bukkit.getServer().getOnlinePlayers())
										    {
										        if(player.getName().equalsIgnoreCase(currentbidder))
										        {
										            Util.Send(player, Util.C("WordPlot") + " " + id + " " + Util.C("MsgOwnedBy") + " " + plot.owner + " " + Util.C("MsgWasReset") + " " + Util.moneyFormat(plot.currentbid));
										            break;
										        }
										    }
										}
									}
								}
								
								PlotMapInfo pmi = PlotMeCoreManager.getMap(p);
								
								if(pmi.RefundClaimPriceOnReset)
								{
									EconomyResponse er = PlotMe_Core.economy.depositPlayer(plot.owner, pmi.ClaimPrice);
									
									if(!er.transactionSuccess())
									{
										Util.Send(p, RED + er.errorMessage);
										Util.warn(er.errorMessage);
										return true;
									}
									else
									{
									    for(Player player : Bukkit.getServer().getOnlinePlayers())
									    {
									        if(player.getName().equalsIgnoreCase(plot.owner))
									        {
									            Util.Send(player, Util.C("WordPlot") + " " + id + " " + Util.C("MsgOwnedBy") + " " + plot.owner + " " + Util.C("MsgWasReset") + " " + Util.moneyFormat(pmi.ClaimPrice));
									            break;
									        }
									    }
									}
								}
							}
							
							if(!PlotMeCoreManager.isPlotAvailable(id, p))
							{
								PlotMeCoreManager.getPlots(p).remove(id);
							}
							
							String name = p.getName();
							
							PlotMeCoreManager.removeOwnerSign(w, id);
							PlotMeCoreManager.removeSellSign(w, id);
							
							SqlManager.deletePlot(PlotMeCoreManager.getIdX(id), PlotMeCoreManager.getIdZ(id), w.getName().toLowerCase());
							
							Util.Send(p, Util.C("MsgPlotReset"));
							
							if(isAdv)
								PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgResetPlot") + " " + id);
						}
						else
						{
							Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgNotYoursNotAllowedReset"));
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
