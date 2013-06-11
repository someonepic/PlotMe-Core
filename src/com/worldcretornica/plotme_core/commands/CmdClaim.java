package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.World;
import org.bukkit.entity.Player;

import  com.worldcretornica.plotme_core.commands.PlotCommand;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdClaim extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.use.claim") || PlotMe_Core.cPerms(p, "PlotMe.admin.claim.other"))
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
					Util.Send(p, RED + Util.C("MsgCannotClaimRoad"));
				}
				else if(!PlotMeCoreManager.isPlotAvailable(id, p))
				{
					Util.Send(p, RED + Util.C("MsgThisPlotOwned"));
				}
				else
				{
					String playername = p.getName();
					
					if(args.length == 2)
					{
						if(PlotMe_Core.cPerms(p, "PlotMe.admin.claim.other"))
						{
							playername = args[1];
						}
					}
					
					int plotlimit = PlotMe_Core.getPlotLimit(p);
					
					if(playername == p.getName() && plotlimit != -1 && PlotMeCoreManager.getNbOwnedPlot(p) >= plotlimit)
					{
						Util.Send(p, RED + Util.C("MsgAlreadyReachedMaxPlots") + " (" + 
								PlotMeCoreManager.getNbOwnedPlot(p) + "/" + PlotMe_Core.getPlotLimit(p) + "). " + Util.C("WordUse") + " " + RED + "/plotme " + Util.C("CommandHome") + RESET + " " + Util.C("MsgToGetToIt"));
					}
					else
					{
						World w = p.getWorld();
						PlotMapInfo pmi = PlotMeCoreManager.getMap(w);
						
						double price = 0;
						
						if(PlotMeCoreManager.isEconomyEnabled(w))
						{
							price = pmi.ClaimPrice;
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
								Util.Send(p, RED + Util.C("MsgNotEnoughBuy") + " " + Util.C("WordMissing") + " " + RESET + (price - balance) + RED + " " + PlotMe_Core.economy.currencyNamePlural());
								return true;
							}
						}
						
						Plot plot = PlotMeCoreManager.createPlot(w, id, playername);
						
						//PlotMeCoreManager.adjustLinkedPlots(id, w);
		
						if(plot == null)
							Util.Send(p, RED + Util.C("ErrCreatingPlotAt") + " " + id);
						else
						{
							if(playername.equalsIgnoreCase(p.getName()))
							{
								Util.Send(p, Util.C("MsgThisPlotYours") + " " + Util.C("WordUse") + " " + RED + "/plotme " + Util.C("CommandHome") + RESET + " " + Util.C("MsgToGetToIt") + " " + Util.moneyFormat(-price));
							}else{
								Util.Send(p, Util.C("MsgThisPlotIsNow") + " " + playername + Util.C("WordPossessive") + ". " + Util.C("WordUse") + " " + RED + "/plotme " + Util.C("CommandHome") + RESET + " " + Util.C("MsgToGetToIt") + " " + Util.moneyFormat(-price));
							}
							
							if(isAdv)
								PlotMe_Core.self.getLogger().info(LOG + playername + " " + Util.C("MsgClaimedPlot") + " " + id + ((price != 0) ? " " + Util.C("WordFor") + " " + price : ""));
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
