package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.ClearReason;
import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.event.PlotMeEventFactory;
import com.worldcretornica.plotme_core.event.PlotResetEvent;

public class CmdReset extends PlotCommand 
{
	public CmdReset(PlotMe_Core instance) {
		super(instance);
	}

	public boolean exec(Player p, String[] args)
	{
		if (plugin.cPerms(p, "PlotMe.admin.reset") || plugin.cPerms(p, "PlotMe.use.reset"))
		{
			if(!plugin.getPlotMeCoreManager().isPlotWorld(p))
			{
				p.sendMessage(RED + C("MsgNotPlotWorld"));
			}
			else
			{
				Plot plot = plugin.getPlotMeCoreManager().getPlotById(p.getLocation());
				
				if(plot == null)
				{
					p.sendMessage(RED + C("MsgNoPlotFound"));
				}
				else
				{
					if(plot.protect)
					{
						p.sendMessage(RED + C("MsgPlotProtectedCannotReset"));
					}
					else
					{
						String playername = p.getName();
						String id = plot.id;
						
						if(plot.owner.equalsIgnoreCase(playername) || plugin.cPerms(p, "PlotMe.admin.reset"))
						{						
							World w = p.getWorld();
							
							PlotResetEvent event = PlotMeEventFactory.callPlotResetEvent(plugin, w, plot, p);
							
							if(!event.isCancelled())
							{
								plugin.getPlotMeCoreManager().setBiome(w, id, Biome.PLAINS);
								plugin.getPlotMeCoreManager().clear(w, plot, p, ClearReason.Reset);
								//RemoveLWC(w, plot);
								PlotMapInfo pmi = plugin.getPlotMeCoreManager().getMap(p);
								
								if(plugin.getPlotMeCoreManager().isEconomyEnabled(p))
								{
									if(plot.auctionned)
									{
										String currentbidder = plot.currentbidder;
										
										if(!currentbidder.equals(""))
										{
											EconomyResponse er = plugin.getEconomy().depositPlayer(currentbidder, plot.currentbid);
											
											if(!er.transactionSuccess())
											{
												p.sendMessage(er.errorMessage);
												Util().warn(er.errorMessage);
											}
											else
											{
											    for(Player player : Bukkit.getServer().getOnlinePlayers())
											    {
											        if(player.getName().equalsIgnoreCase(currentbidder))
											        {
											            player.sendMessage(C("WordPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgWasReset") + " " + Util().moneyFormat(plot.currentbid));
											            break;
											        }
											    }
											}
										}
									}
									
									if(pmi.RefundClaimPriceOnReset)
									{
										EconomyResponse er = plugin.getEconomy().depositPlayer(plot.owner, pmi.ClaimPrice);
										
										if(!er.transactionSuccess())
										{
											p.sendMessage(RED + er.errorMessage);
											Util().warn(er.errorMessage);
											return true;
										}
										else
										{
										    for(Player player : Bukkit.getServer().getOnlinePlayers())
										    {
										        if(player.getName().equalsIgnoreCase(plot.owner))
										        {
										            player.sendMessage(C("WordPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgWasReset") + " " + Util().moneyFormat(pmi.ClaimPrice));
										            break;
										        }
										    }
										}
									}
								}
								
								if(!plugin.getPlotMeCoreManager().isPlotAvailable(id, p))
								{
									plugin.getPlotMeCoreManager().removePlot(w, id);
								}
								
								String name = p.getName();
								
								plugin.getPlotMeCoreManager().removeOwnerSign(w, id);
								plugin.getPlotMeCoreManager().removeSellSign(w, id);
								
								plugin.getSqlManager().deletePlot(plugin.getPlotMeCoreManager().getIdX(id), plugin.getPlotMeCoreManager().getIdZ(id), w.getName().toLowerCase());
								
								pmi.addFreed(id);
								
								//p.sendMessage(C("MsgPlotReset"));
								
								if(isAdv)
									plugin.getLogger().info(LOG + name + " " + C("MsgResetPlot") + " " + id);
							}
						}
						else
						{
							p.sendMessage(RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedReset"));
						}
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
