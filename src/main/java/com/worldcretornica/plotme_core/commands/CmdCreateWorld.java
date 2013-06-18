package com.worldcretornica.plotme_core.commands;

import java.util.HashMap;
import java.util.Map;

import multiworld.MultiWorldPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.api.v0_14b.IPlotMe_ChunkGenerator;
import com.worldcretornica.plotme_core.utils.MinecraftFontWidthCalculator;
import com.worldcretornica.plotme_core.utils.Util;

public class CmdCreateWorld extends PlotCommand 
{
	public boolean exec(CommandSender cs, String[] args)
	{
		if (PlotMe_Core.cPerms(cs, "PlotMe.admin.createworld"))
		{
			if(PlotMe_Core.creationbuffer.containsKey(cs.getName()))
			{
				if(args.length == 1)
				{
					//try to create world
					Map<String, String> parameters = PlotMe_Core.creationbuffer.get(cs.getName());
					if(PlotMeCoreManager.CreatePlotWorld(cs, parameters.get("worldname"), parameters.get("generator"), parameters))
					{
						Util.Send(cs, Util.C("MsgWorldCreationSuccess"));
					}
				}
				else
				{
					if(args.length >= 2)
					{
						//cancel
						if(args[1].equalsIgnoreCase(Util.C("CommandCreateWorld-Cancel")))
						{
							PlotMe_Core.creationbuffer.remove(cs.getName());
							return true;
						}
						//settings
						else if(args[1].equalsIgnoreCase(Util.C("CommandCreateWorld-Setting")))
						{
							if(args.length == 4)
							{
								String key = args[2];
								String value = args[3];

								Map<String, String> parameters = PlotMe_Core.creationbuffer.get(cs.getName());
								
								if(parameters != null)
								{
									for(String ckey : parameters.keySet())
									{
										if(key.equalsIgnoreCase(ckey))
										{
											parameters.remove(ckey);
											parameters.put(ckey, value);
											
											Util.Send(cs, Util.C("MsgSettingChanged") + " " + GREEN + ckey + RESET + "=" + AQUA + value);
											
											return true;
										}
									}
									
									showCurrentSettings(cs, parameters);
									return true;
								}
							}
						}
						
						Util.Send(cs, Util.C("WordUsage") + ": ");
						Util.Send(cs, "/plotme " + Util.C("CommandCreateWorld") + " " + Util.C("CommandCreateWorld-Setting") + 
								"<" + Util.C("WordConfig") + ">" + " " + "<" + Util.C("WordValue") + "> " + 
								Util.C("MsgCreateWorldParameters4"));
						Util.Send(cs, "/plotme " + Util.C("CommandCreateWorld") + " " + Util.C("CommandCreateWorld-Cancel") + " " +
								Util.C("MsgCreateWorldParameters5"));
					}
				}
			}
			else
			{
				//Usage
				if(args.length == 1)
				{
					Util.Send(cs, Util.C("WordUsage") + ": " + RED + "/plotme " + Util.C("CommandCreateWorld") + " <" + Util.C("WordWorld") + "> [" + Util.C("WordGenerator") + "]");
					Util.Send(cs, "  " + Util.C("MsgCreateWorldHelp"));
				}
				else
				{
					if(PlotMe_Core.multiworld == null)
					{
						if(Bukkit.getPluginManager().isPluginEnabled("MultiWorld"))
							PlotMe_Core.multiworld = (MultiWorldPlugin)Bukkit.getPluginManager().getPlugin("MultiWorld");
					}
					if(PlotMe_Core.multiverse == null)
					{
						if(Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core"))
							PlotMe_Core.multiverse = ((MultiverseCore)Bukkit.getPluginManager().getPlugin("Multiverse-Core"));
					}
					
					
					if((PlotMe_Core.multiworld == null || !PlotMe_Core.multiworld.isEnabled()) &&
							(PlotMe_Core.multiverse == null || !PlotMe_Core.multiverse.isEnabled()))
					{
						Util.Send(cs, "[" + PlotMe_Core.NAME + "] " + Util.C("ErrWorldPluginNotFound"));
						return true;
					}
					
					Map<String, String> parameters = new HashMap<String, String>(); 
					Map<String, String> genparameters = new HashMap<String, String>();
					
					//Prepare creation
					if(args.length >= 2)
					{
						parameters.put("worldname", args[1]);
						
						if(PlotMe_Core.multiworld != null && PlotMe_Core.multiworld.isEnabled())
						{
							try 
							{
								multiworld.Utils.checkWorldName(args[1]);
							} 
							catch (Exception e) 
							{
								Util.Send(cs, "[" + PlotMe_Core.NAME + "] " + Util.C("ErrInvalidWorldName") + " '" + parameters.get("worldname") + "'");
								return true;
							}
						}
						else if(PlotMe_Core.multiverse != null && PlotMe_Core.multiverse.isEnabled())
						{
							
						}
					}
					
					if(args.length >= 3)
					{
						parameters.put("generator", args[2]);
					}
					else
					{
						parameters.put("generator", "PlotMe-DefaultGenerator");
					}
					
					//Check if world exists
					if(Bukkit.getWorlds().contains(parameters.get("worldname")))
					{
						Util.Send(cs, "[" + PlotMe_Core.NAME + "] " + Util.C("ErrWorldExists") + " '" + parameters.get("worldname") + "'");
						return false;
					}
					
					
					//Find generator
					Plugin plugin = Bukkit.getPluginManager().getPlugin(parameters.get("generator"));
					
					if(plugin == null)
					{
						Util.Send(cs, "[" + PlotMe_Core.NAME + "] " + Util.C("ErrCannotFindWorldGen") + " '" + parameters.get("generator") + "'");
						return false;
					}
					else
					{
						ChunkGenerator cg = plugin.getDefaultWorldGenerator(parameters.get("worldname"), "");
						if(cg != null && cg instanceof IPlotMe_ChunkGenerator)
						{
							//Get the generator configurations
							genparameters = ((IPlotMe_ChunkGenerator) cg).getManager().getDefaultGenerationConfig();
							
							if(genparameters == null)
							{
								cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrCannotCreateGen1") + " '" + parameters.get("generator") + "' " + Util.C("ErrCannotCreateGen2"));
								return false;
							}
						}
						else
						{
							cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrCannotCreateGen1") + " '" + parameters.get("generator") + "' " + Util.C("ErrCannotCreateGen3"));
							return false;
						}
					}
					
					parameters.put("PlotAutoLimit", "1000");
					parameters.put("DaysToExpiration", "7");
					parameters.put("ProtectedWallBlockId", "44:4");
					parameters.put("ForSaleWallBlockId", "44:1");
					parameters.put("AuctionWallBlockId", "44:1");
					parameters.put("AutoLinkPlots", "true");
					parameters.put("DisableExplosion", "true");
					parameters.put("DisableIgnition", "true");
					parameters.put("UseEconomy", "false");
					parameters.put("CanPutOnSale", "false");
					parameters.put("CanSellToBank", "false");
					parameters.put("RefundClaimPriceOnReset", "false");
					parameters.put("RefundClaimPriceOnSetOwner", "false");
					parameters.put("ClaimPrice", "0");
					parameters.put("ClearPrice", "0");
					parameters.put("AddPlayerPrice", "0");
					parameters.put("DenyPlayerPrice", "0");
					parameters.put("RemovePlayerPrice", "0");
					parameters.put("UndenyPlayerPrice", "0");
					parameters.put("PlotHomePrice", "0");
					parameters.put("CanCustomizeSellPrice", "false");
					parameters.put("SellToPlayerPrice", "0");
					parameters.put("SellToBankPrice", "0");
					parameters.put("BuyFromBankPrice", "0");
					parameters.put("AddCommentPrice", "0");
					parameters.put("BiomeChangePrice", "0");
					parameters.put("ProtectPrice", "0");
					parameters.put("DisposePrice", "0");
					
					
					Util.Send(cs, Util.C("MsgCreateWorldParameters1"));
					Util.Send(cs, Util.C("MsgCreateWorldParameters2"));
					
					//Show default configurations
					showCurrentSettings(cs, parameters);
					Util.Send(cs, Util.C("MsgCreateWorldParameters3"));
					
					showCurrentSettings(cs, genparameters);
					
					parameters.putAll(genparameters);
					
					Util.Send(cs, "/plotme " + Util.C("CommandCreateWorld") + " " + Util.C("CommandCreateWorld-Setting") + 
							"<" + Util.C("WordConfig") + ">" + " " + "<" + Util.C("WordValue") + "> " + 
							Util.C("MsgCreateWorldParameters4"));
					
					Util.Send(cs, "/plotme " + Util.C("CommandCreateWorld") + " " + Util.C("CommandCreateWorld-Cancel") + " " +
							Util.C("MsgCreateWorldParameters5"));
					
					PlotMe_Core.creationbuffer.put(cs.getName(), parameters);
				}
			}
		}
		else
		{
			Util.Send(cs, RED + Util.C("MsgPermissionDenied"));
		}
		return true;
	}
	
	
	private void showCurrentSettings(CommandSender cs, Map<String, String> parameters)
	{
		String buffer = " ";
		
		for(String key : parameters.keySet())
		{
			if(MinecraftFontWidthCalculator.getStringWidth(ChatColor.stripColor(buffer + key + "=" + parameters.get(key) + " ")) >= 1250)
			{
				Util.Send(cs, buffer);
				buffer = " ";
			}
			
			buffer += GREEN + key + RESET + "=" + AQUA + parameters.get(key) + "  ";
		}
		Util.Send(cs, buffer);
	}
} 
