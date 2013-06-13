package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.apache.commons.lang.StringUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.SqlManager;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdComment extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.use.comment"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
			}
			else
			{
				if(args.length < 2)
				{
					Util.Send(p, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandComment") + " <" + Util.C("WordText") + ">");
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
							World w = p.getWorld();
							PlotMapInfo pmi = PlotMeCoreManager.getMap(w);
							String playername = p.getName();
							
							double price = 0;
							
							if(PlotMeCoreManager.isEconomyEnabled(w))
							{
								price = pmi.AddCommentPrice;
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
									Util.Send(p, RED + Util.C("MsgNotEnoughComment") + " " + Util.C("WordMissing") + " " + RESET + Util.moneyFormat(price - balance, false));
									return true;
								}
							}
							
							Plot plot = PlotMeCoreManager.getPlotById(p, id);
							
							String text = StringUtils.join(args," ");
							text = text.substring(text.indexOf(" "));
							
							String[] comment = new String[2];
							comment[0] = playername;
							comment[1] = text;
							
							plot.comments.add(comment);
							SqlManager.addPlotComment(comment, plot.comments.size(), PlotMeCoreManager.getIdX(id), PlotMeCoreManager.getIdZ(id), plot.world);
							
							Util.Send(p, Util.C("MsgCommentAdded") + " " + Util.moneyFormat(-price));
							
							if(isAdv)
								PlotMe_Core.self.getLogger().info(LOG + playername + " " + Util.C("MsgCommentedPlot") + " " + id + ((price != 0) ? " " + Util.C("WordFor") + " " + price : ""));
						}
						else
						{
							Util.Send(p, RED + Util.C("MsgThisPlot") + "(" + id + ") " + Util.C("MsgHasNoOwner"));
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
