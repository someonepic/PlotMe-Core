package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdSell extends PlotCommand 
{
	public boolean exec(Player p, String[] args) 
	{
		if(PlotMeCoreManager.isEconomyEnabled(p))
		{
			PlotMapInfo pmi = PlotMeCoreManager.getMap(p);
			
			if(pmi.CanSellToBank || pmi.CanPutOnSale)
			{
				if(PlotMe_Core.cPerms(p, "PlotMe.use.sell") || PlotMe_Core.cPerms(p, "PlotMe.admin.sell"))
				{
					Location l = p.getLocation();
					String id = PlotMeCoreManager.getPlotId(l);
					
					if(id.equals(""))
					{
						Util.Send(p, RED + Util.C("MsgNoPlotFound"));
					}
					else
					{
						if(!PlotMeCoreManager.isPlotAvailable(id, p))
						{
							Plot plot = PlotMeCoreManager.getPlotById(p,id);
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || PlotMe_Core.cPerms(p, "PlotMe.admin.sell"))
							{
								World w = p.getWorld();
								String name = p.getName();
								
								if(plot.forsale)
								{
									plot.customprice = 0;
									plot.forsale = false;
									
									plot.updateField("customprice", 0);
									plot.updateField("forsale", false);
									
									PlotMeCoreManager.adjustWall(l);
									PlotMeCoreManager.setSellSign(w, plot);
									
									Util.Send(p, Util.C("MsgPlotNoLongerSale"));
									
									if(isAdv)
										PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgRemovedPlot") + " " + id + " " + Util.C("MsgFromBeingSold"));
								}
								else
								{
									double price = pmi.SellToPlayerPrice;
									boolean bank = false;
									
									if(args.length == 2)
									{
										if(args[1].equalsIgnoreCase("bank"))
										{
											bank = true;
										}
										else
										{
											if(pmi.CanCustomizeSellPrice)
											{
												try  
												{  
													price = Double.parseDouble(args[1]);  
												}  
												catch(Exception e)
												{
													if(pmi.CanSellToBank)
													{
														Util.Send(p, Util.C("WordUsage") + ": " + RED + " /plotme " + Util.C("CommandSellBank") + "|<" + Util.C("WordAmount") + ">");
														p.sendMessage("  " + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandSellBank") + " " + RESET + " or " + RED + " /plotme " + Util.C("CommandSell") + " 200");
													}
													else
													{
														Util.Send(p, Util.C("WordUsage") + ": " + RED + 
																" /plotme " + Util.C("CommandSell") + " <" + Util.C("WordAmount") + ">" + RESET + 
																" " + Util.C("WordExample") + ": " + RED + "/plotme " + Util.C("CommandSell") + " 200");
													}
												}
											}
											else
											{
												Util.Send(p, RED + Util.C("MsgCannotCustomPriceDefault") + " " + price);
												return true;
											}
										}
									}
									
									if(bank)
									{
										if(!pmi.CanSellToBank)
										{
											Util.Send(p, RED + Util.C("MsgCannotSellToBank"));
										}
										else
										{
											
											String currentbidder = plot.currentbidder;
											
											if(!currentbidder.equals(""))
											{
												double bid = plot.currentbid;
												
												EconomyResponse er = PlotMe_Core.economy.depositPlayer(currentbidder, bid);
												
												if(!er.transactionSuccess())
												{
													Util.Send(p, RED + er.errorMessage);
													Util.warn(er.errorMessage);
												}
												else
												{
													for(Player player : Bukkit.getServer().getOnlinePlayers())
													{
														if(player.getName().equalsIgnoreCase(currentbidder))
														{
															Util.Send(player, Util.C("WordPlot") + " " + id + " " + Util.C("MsgOwnedBy") + " " + plot.owner + " " + Util.C("MsgSoldToBank") + " " + Util.moneyFormat(bid));
															break;
														}
													}
												}
											}
											
											double sellprice = pmi.SellToBankPrice;
											
											EconomyResponse er = PlotMe_Core.economy.depositPlayer(name, sellprice);
											
											if(er.transactionSuccess())
											{
												plot.owner = "$Bank$";
												plot.forsale = true;
												plot.customprice = pmi.BuyFromBankPrice;
												plot.auctionned = false;
												plot.currentbidder = "";
												plot.currentbid = 0;
																								
												plot.removeAllAllowed();
												
												PlotMeCoreManager.setOwnerSign(w, plot);
												PlotMeCoreManager.setSellSign(w, plot);
												
												plot.updateField("owner", plot.owner);
												plot.updateField("forsale", true);
												plot.updateField("auctionned", true);
												plot.updateField("customprice", plot.customprice);
												plot.updateField("currentbidder", "");
												plot.updateField("currentbid", 0);
												
												Util.Send(p, Util.C("MsgPlotSold") + " " + Util.moneyFormat(sellprice));
												
												if(isAdv)
													PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgSoldToBankPlot") + " " + id + " " + Util.C("WordFor") + " " + sellprice);
											}
											else
											{
												Util.Send(p, " " + er.errorMessage);
												Util.warn(er.errorMessage);
											}
										}
									}
									else
									{
										if(price < 0)
										{
											Util.Send(p, RED + Util.C("MsgInvalidAmount"));
										}
										else
										{
											plot.customprice = price;
											plot.forsale = true;
											
											plot.updateField("customprice", price);
											plot.updateField("forsale", true);
											
											PlotMeCoreManager.adjustWall(l);
											PlotMeCoreManager.setSellSign(w, plot);
											
											Util.Send(p, Util.C("MsgPlotForSale"));
											
											if(isAdv)
												PlotMe_Core.self.getLogger().info(LOG + name + " " + Util.C("MsgPutOnSalePlot") + " " + id + " " + Util.C("WordFor") + " " + price);
										}
									}
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
				else
				{
					Util.Send(p, RED + Util.C("MsgPermissionDenied"));
				}
			}
			else
			{
				Util.Send(p, RED + Util.C("MsgSellingPlotsIsDisabledWorld"));
			}
		}
		else
		{
			Util.Send(p, RED + Util.C("MsgEconomyDisabledWorld"));
		}
		return true;
	}
}
