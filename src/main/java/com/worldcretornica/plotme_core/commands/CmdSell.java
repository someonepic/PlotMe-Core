package com.worldcretornica.plotme_core.commands;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class CmdSell extends PlotCommand 
{
	public CmdSell(PlotMe_Core instance) {
		super(instance);
	}

	public boolean exec(Player p, String[] args) 
	{
		if(plugin.getPlotMeCoreManager().isEconomyEnabled(p))
		{
			PlotMapInfo pmi = plugin.getPlotMeCoreManager().getMap(p);
			
			if(pmi.CanSellToBank || pmi.CanPutOnSale)
			{
				if(plugin.cPerms(p, "PlotMe.use.sell") || plugin.cPerms(p, "PlotMe.admin.sell"))
				{
					Location l = p.getLocation();
					String id = plugin.getPlotMeCoreManager().getPlotId(l);
					
					if(id.equals(""))
					{
						p.sendMessage(RED + C("MsgNoPlotFound"));
					}
					else
					{
						if(!plugin.getPlotMeCoreManager().isPlotAvailable(id, p))
						{
							Plot plot = plugin.getPlotMeCoreManager().getPlotById(p,id);
							
							if(plot.owner.equalsIgnoreCase(p.getName()) || plugin.cPerms(p, "PlotMe.admin.sell"))
							{
								World w = p.getWorld();
								String name = p.getName();
								
								if(plot.forsale)
								{
									plot.customprice = 0;
									plot.forsale = false;
									
									plot.updateField("customprice", 0);
									plot.updateField("forsale", false);
									
									plugin.getPlotMeCoreManager().adjustWall(l);
									plugin.getPlotMeCoreManager().setSellSign(w, plot);
									
									p.sendMessage(C("MsgPlotNoLongerSale"));
									
									if(isAdv)
										plugin.getLogger().info(LOG + name + " " + C("MsgRemovedPlot") + " " + id + " " + C("MsgFromBeingSold"));
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
														p.sendMessage(C("WordUsage") + ": " + RED + " /plotme " + C("CommandSellBank") + "|<" + C("WordAmount") + ">");
														p.sendMessage("  " + C("WordExample") + ": " + RED + "/plotme " + C("CommandSellBank") + " " + RESET + " or " + RED + " /plotme " + C("CommandSell") + " 200");
													}
													else
													{
														p.sendMessage(C("WordUsage") + ": " + RED + 
																" /plotme " + C("CommandSell") + " <" + C("WordAmount") + ">" + RESET + 
																" " + C("WordExample") + ": " + RED + "/plotme " + C("CommandSell") + " 200");
													}
												}
											}
											else
											{
												p.sendMessage(RED + C("MsgCannotCustomPriceDefault") + " " + price);
												return true;
											}
										}
									}
									
									if(bank)
									{
										if(!pmi.CanSellToBank)
										{
											p.sendMessage(RED + C("MsgCannotSellToBank"));
										}
										else
										{
											
											String currentbidder = plot.currentbidder;
											
											if(!currentbidder.equals(""))
											{
												double bid = plot.currentbid;
												
												EconomyResponse er = plugin.getEconomy().depositPlayer(currentbidder, bid);
												
												if(!er.transactionSuccess())
												{
													p.sendMessage(RED + er.errorMessage);
													Util().warn(er.errorMessage);
												}
												else
												{
													for(Player player : Bukkit.getServer().getOnlinePlayers())
													{
														if(player.getName().equalsIgnoreCase(currentbidder))
														{
															player.sendMessage(C("WordPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgSoldToBank") + " " + Util().moneyFormat(bid));
															break;
														}
													}
												}
											}
											
											double sellprice = pmi.SellToBankPrice;
											
											EconomyResponse er = plugin.getEconomy().depositPlayer(name, sellprice);
											
											if(er.transactionSuccess())
											{
												plot.owner = "$Bank$";
												plot.forsale = true;
												plot.customprice = pmi.BuyFromBankPrice;
												plot.auctionned = false;
												plot.currentbidder = "";
												plot.currentbid = 0;
																								
												plot.removeAllAllowed();
												
												plugin.getPlotMeCoreManager().setOwnerSign(w, plot);
												plugin.getPlotMeCoreManager().setSellSign(w, plot);
												
												plot.updateField("owner", plot.owner);
												plot.updateField("forsale", true);
												plot.updateField("auctionned", true);
												plot.updateField("customprice", plot.customprice);
												plot.updateField("currentbidder", "");
												plot.updateField("currentbid", 0);
												
												p.sendMessage(C("MsgPlotSold") + " " + Util().moneyFormat(sellprice));
												
												if(isAdv)
													plugin.getLogger().info(LOG + name + " " + C("MsgSoldToBankPlot") + " " + id + " " + C("WordFor") + " " + sellprice);
											}
											else
											{
												p.sendMessage(" " + er.errorMessage);
												Util().warn(er.errorMessage);
											}
										}
									}
									else
									{
										if(price < 0)
										{
											p.sendMessage(RED + C("MsgInvalidAmount"));
										}
										else
										{
											plot.customprice = price;
											plot.forsale = true;
											
											plot.updateField("customprice", price);
											plot.updateField("forsale", true);
											
											plugin.getPlotMeCoreManager().adjustWall(l);
											plugin.getPlotMeCoreManager().setSellSign(w, plot);
											
											p.sendMessage(C("MsgPlotForSale"));
											
											if(isAdv)
												plugin.getLogger().info(LOG + name + " " + C("MsgPutOnSalePlot") + " " + id + " " + C("WordFor") + " " + price);
										}
									}
								}
							}
							else
							{
								p.sendMessage(RED + C("MsgDoNotOwnPlot"));
							}
						}
						else
						{
							p.sendMessage(RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
						}
					}
				}
				else
				{
					p.sendMessage(RED + C("MsgPermissionDenied"));
				}
			}
			else
			{
				p.sendMessage(RED + C("MsgSellingPlotsIsDisabledWorld"));
			}
		}
		else
		{
			p.sendMessage(RED + C("MsgEconomyDisabledWorld"));
		}
		return true;
	}
}
