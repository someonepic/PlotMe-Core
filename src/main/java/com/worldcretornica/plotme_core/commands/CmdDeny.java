package com.worldcretornica.plotme_core.commands;

import java.util.List;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdDeny extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.admin.deny") || PlotMe_Core.cPerms(p, "PlotMe.use.deny"))
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
							Util.Send(p, Util.C("WordUsage") + " " + RED + "/plotme " + Util.C("CommandDeny") + " <" + Util.C("WordPlayer") + ">");
						}
						else
						{
						
							Plot plot = PlotMeCoreManager.getPlotById(p,id);
							String playername = p.getName();
							String denied = args[1];
							
							if(plot.owner.equalsIgnoreCase(playername) || PlotMe_Core.cPerms(p, "PlotMe.admin.deny"))
							{
								if(plot.isDenied(denied))
								{
									Util.Send(p, Util.C("WordPlayer") + " " + RED + args[1] + RESET + " " + Util.C("MsgAlreadyDenied"));
								}
								else
								{
									World w = p.getWorld();
									
									PlotMapInfo pmi = PlotMeCoreManager.getMap(w);
									
									double price = 0;
									
									if(PlotMeCoreManager.isEconomyEnabled(w))
									{
										price = pmi.DenyPlayerPrice;
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
											Util.Send(p, RED + Util.C("MsgNotEnoughDeny") + " " + Util.C("WordMissing") + " " + RESET + Util.moneyFormat(price - balance, false));
											return true;
										}
									}
									
									plot.addDenied(args[1]);
									
									if(denied.equals("*"))
									{
										List<Player> deniedplayers = PlotMeCoreManager.getPlayersInPlot(w, id);
										
										for(Player deniedplayer : deniedplayers)
										{
											if(!plot.isAllowed(deniedplayer.getName()))
												deniedplayer.teleport(PlotMeCoreManager.getPlotHome(w, plot.id));
										}
									}
									else
									{
										Player deniedplayer = Bukkit.getServer().getPlayer(denied);
										
										if(deniedplayer != null)
										{
											if(deniedplayer.getWorld().equals(w))
											{
												String deniedid = PlotMeCoreManager.getPlotId(deniedplayer.getLocation());
												
												if(deniedid.equalsIgnoreCase(id))
												{
													deniedplayer.teleport(PlotMeCoreManager.getPlotHome(w, plot.id));
												}
											}
										}
									}
									
									Util.Send(p, Util.C("WordPlayer") + " " + RED + denied + RESET + " " + Util.C("MsgNowDenied") + " " + Util.moneyFormat(-price));
									
									if(isAdv)
										PlotMe_Core.self.getLogger().info(LOG + playername + " " + Util.C("MsgDeniedPlayer") + " " + denied + " " + Util.C("MsgToPlot") + " " + id + ((price != 0) ? " " + Util.C("WordFor") + " " + price : ""));
								}
							}
							else
							{
								Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgNotYoursNotAllowedDeny"));
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
