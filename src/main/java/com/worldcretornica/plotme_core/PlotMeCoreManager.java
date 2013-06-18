package com.worldcretornica.plotme_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.api.v0_14b.IPlotMe_GeneratorManager;
import com.worldcretornica.plotme_core.api.v0_14b.IPlotMe_ChunkGenerator;
import com.worldcretornica.plotme_core.utils.Util;

import multiworld.MultiWorldPlugin;
import multiworld.worldgen.WorldGenerator;

public class PlotMeCoreManager 
{
	public static boolean CreatePlotWorld(CommandSender cs, String worldname, String generator, Map<String, String> args)
	{
		//Get a seed
		Long seed = (new java.util.Random()).nextLong();
				
		//Check if we have multiworld
		if(PlotMe_Core.multiworld == null)
		{
			if(Bukkit.getPluginManager().isPluginEnabled("MultiWorld"))
				PlotMe_Core.multiworld = (MultiWorldPlugin)Bukkit.getPluginManager().getPlugin("MultiWorld");
		}
		//Check if we have multiverse
		if(PlotMe_Core.multiverse == null)
		{
			if(Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core"))
				PlotMe_Core.multiverse = ((MultiverseCore)Bukkit.getPluginManager().getPlugin("Multiverse-Core"));
		}
		
		//Do we have one of them
		if(PlotMe_Core.multiworld == null && PlotMe_Core.multiverse == null)
		{
			cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrWorldPluginNotFound"));
			return false;
		}
		
		
		//Find generator
		Plugin plugin = Bukkit.getPluginManager().getPlugin(generator);
		
		//Make generator create settings
		if(plugin == null)
		{
			cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrCannotFindWorldGen") + " '" + generator + "'");
			return false;
		}
		else
		{
			ChunkGenerator cg = plugin.getDefaultWorldGenerator(worldname, "");
			if(cg != null && cg instanceof IPlotMe_ChunkGenerator)
			{
				//Create the generator configurations
				if(!((IPlotMe_ChunkGenerator) cg).getManager().createConfig(worldname, args, cs))
				{
					cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrCannotCreateGen1") + " '" + generator + "' " + Util.C("ErrCannotCreateGen2"));
					return false;
				}
			}
			else
			{
				cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrCannotCreateGen1") + " '" + generator + "' " + Util.C("ErrCannotCreateGen3"));
				return false;
			}
		}
		
		//Create manager configurations		
		File configfile = new File(PlotMe_Core.configpath, "core-config.yml");
		
		FileConfiguration config = new YamlConfiguration();
		try 
		{
			config.load(configfile);
		} 
		catch (FileNotFoundException e) {} 
		catch (IOException e) 
		{
			PlotMe_Core.self.getLogger().severe("can't read configuration file");
			e.printStackTrace();
			return false;
		} 
		catch (InvalidConfigurationException e) 
		{
			PlotMe_Core.self.getLogger().severe("invalid configuration format");
			e.printStackTrace();
			return false;
		}
		
		ConfigurationSection worlds;
		
		if(!config.contains("worlds"))
		{
			worlds = config.createSection("worlds");
		}
		else
		{
			worlds = config.getConfigurationSection("worlds");
		}
		
		PlotMapInfo tempPlotInfo = new PlotMapInfo(worldname);
		ConfigurationSection currworld = worlds.getConfigurationSection(worldname);
		
		if(currworld == null)
		{
			currworld = worlds.createSection(worldname);
		}
		
		tempPlotInfo.PlotAutoLimit = Integer.parseInt(args.get("PlotAutoLimit"));
		tempPlotInfo.DaysToExpiration = Integer.parseInt(args.get("DaysToExpiration"));
		tempPlotInfo.ProtectedBlocks = PlotMe_Core.getDefaultProtectedBlocks();
		tempPlotInfo.PreventedItems = PlotMe_Core.getDefaultPreventedItems();
		tempPlotInfo.AutoLinkPlots = Boolean.parseBoolean(args.get("AutoLinkPlots"));
		tempPlotInfo.DisableExplosion = Boolean.parseBoolean(args.get("DisableExplosion"));
		tempPlotInfo.DisableIgnition = Boolean.parseBoolean(args.get("DisableIgnition"));
		tempPlotInfo.UseEconomy = Boolean.parseBoolean(args.get("UseEconomy"));
		tempPlotInfo.CanPutOnSale = Boolean.parseBoolean(args.get("CanPutOnSale"));
		tempPlotInfo.CanSellToBank = Boolean.parseBoolean(args.get("CanSellToBank"));
		tempPlotInfo.RefundClaimPriceOnReset = Boolean.parseBoolean(args.get("RefundClaimPriceOnReset"));
		tempPlotInfo.RefundClaimPriceOnSetOwner = Boolean.parseBoolean(args.get("RefundClaimPriceOnSetOwner"));
		tempPlotInfo.ClaimPrice = Double.parseDouble(args.get("ClaimPrice"));
		tempPlotInfo.ClearPrice = Double.parseDouble(args.get("ClearPrice"));
		tempPlotInfo.AddPlayerPrice = Double.parseDouble(args.get("AddPlayerPrice"));
		tempPlotInfo.DenyPlayerPrice = Double.parseDouble(args.get("DenyPlayerPrice"));
		tempPlotInfo.RemovePlayerPrice = Double.parseDouble(args.get("RemovePlayerPrice"));
		tempPlotInfo.UndenyPlayerPrice = Double.parseDouble(args.get("UndenyPlayerPrice"));
		tempPlotInfo.PlotHomePrice = Double.parseDouble(args.get("PlotHomePrice"));
		tempPlotInfo.CanCustomizeSellPrice = Boolean.parseBoolean(args.get("CanCustomizeSellPrice"));
		tempPlotInfo.SellToPlayerPrice = Double.parseDouble(args.get("SellToPlayerPrice"));
		tempPlotInfo.SellToBankPrice = Double.parseDouble(args.get("SellToBankPrice"));
		tempPlotInfo.BuyFromBankPrice = Double.parseDouble(args.get("BuyFromBankPrice"));
		tempPlotInfo.AddCommentPrice = Double.parseDouble(args.get("AddCommentPrice"));
		tempPlotInfo.BiomeChangePrice = Double.parseDouble(args.get("BiomeChangePrice"));
		tempPlotInfo.ProtectPrice = Double.parseDouble(args.get("ProtectPrice"));
		tempPlotInfo.DisposePrice = Double.parseDouble(args.get("DisposePrice"));
		
		currworld.set("PlotAutoLimit", tempPlotInfo.PlotAutoLimit);
		currworld.set("DaysToExpiration", tempPlotInfo.DaysToExpiration);
		currworld.set("ProtectedBlocks", tempPlotInfo.ProtectedBlocks);
		currworld.set("PreventedItems", tempPlotInfo.PreventedItems);
		currworld.set("AutoLinkPlots", tempPlotInfo.AutoLinkPlots);
		currworld.set("DisableExplosion", tempPlotInfo.DisableExplosion);
		currworld.set("DisableIgnition", tempPlotInfo.DisableIgnition);
		
		ConfigurationSection economysection = currworld.createSection("economy");
		
		economysection.set("UseEconomy", tempPlotInfo.UseEconomy);
		economysection.set("CanPutOnSale", tempPlotInfo.CanPutOnSale);
		economysection.set("CanSellToBank", tempPlotInfo.CanSellToBank);
		economysection.set("RefundClaimPriceOnReset", tempPlotInfo.RefundClaimPriceOnReset);
		economysection.set("RefundClaimPriceOnSetOwner", tempPlotInfo.RefundClaimPriceOnSetOwner);
		economysection.set("ClaimPrice", tempPlotInfo.ClaimPrice);
		economysection.set("ClearPrice", tempPlotInfo.ClearPrice);
		economysection.set("AddPlayerPrice", tempPlotInfo.AddPlayerPrice);
		economysection.set("DenyPlayerPrice", tempPlotInfo.DenyPlayerPrice);
		economysection.set("RemovePlayerPrice", tempPlotInfo.RemovePlayerPrice);
		economysection.set("UndenyPlayerPrice", tempPlotInfo.UndenyPlayerPrice);
		economysection.set("PlotHomePrice", tempPlotInfo.PlotHomePrice);
		economysection.set("CanCustomizeSellPrice", tempPlotInfo.CanCustomizeSellPrice);
		economysection.set("SellToPlayerPrice", tempPlotInfo.SellToPlayerPrice);
		economysection.set("SellToBankPrice", tempPlotInfo.SellToBankPrice);
		economysection.set("BuyFromBankPrice", tempPlotInfo.BuyFromBankPrice);
		economysection.set("AddCommentPrice", tempPlotInfo.AddCommentPrice);
		economysection.set("BiomeChangePrice", tempPlotInfo.BiomeChangePrice);
		economysection.set("ProtectPrice", tempPlotInfo.ProtectPrice);
		economysection.set("DisposePrice", tempPlotInfo.DisposePrice);
		
		currworld.set("economy", economysection);
		
		worlds.set(worldname, currworld);
		
		//tempPlotInfo.plots = new HashMap<String, Plot>(); //SqlManager.getPlots(worldname.toLowerCase());
		
		PlotMe_Core.plotmaps.put(worldname.toLowerCase(), tempPlotInfo);
		
		try
		{
			config.save(configfile);
		} 
		catch (IOException e) 
		{
			PlotMe_Core.self.getLogger().severe("error writting configurations");
			e.printStackTrace();
			return false;
		}
		
		
		
		//Are we using multiworld?
		if(PlotMe_Core.multiworld != null)
		{
			boolean success = false;
			
			if(PlotMe_Core.multiworld.isEnabled())
			{
				WorldGenerator env = WorldGenerator.NORMAL;
				
				try
				{
					env = WorldGenerator.getGenByName("plugin");
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					return false;
				}
				
				try 
				{
					success = PlotMe_Core.multiworld.getDataManager().makeWorld(worldname, env, seed, generator);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					return false;
				}
				
				if(success)
				{
					try 
					{
						PlotMe_Core.multiworld.getDataManager().loadWorld(worldname, true);
						PlotMe_Core.multiworld.getDataManager().save();
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						return false;
					}
				}
				else
					cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrCannotCreateMW"));
			}
			else
			{
				cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrMWDisabled"));
			}
			return success;
		}
		
		//Are we using multiverse?
		if(PlotMe_Core.multiverse != null)
		{
			boolean success = false;
			
			if(PlotMe_Core.multiverse.isEnabled())
			{
				success = PlotMe_Core.multiverse.getMVWorldManager().addWorld(worldname, Environment.NORMAL, seed.toString(), WorldType.NORMAL, true, generator);
				
				if(!success)
					cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrCannotCreateMV"));
			}
			else
			{
				cs.sendMessage("[" + PlotMe_Core.NAME + "] " + Util.C("ErrMVDisabled"));
			}
			return success;
		}
		
		return false;
	}
	
	public static int getIdX(String id)
	{
		return Integer.parseInt(id.substring(0, id.indexOf(";")));
	}
	
	public static int getIdZ(String id)
	{
		return Integer.parseInt(id.substring(id.indexOf(";") + 1));
	}
	
	public static int getNbOwnedPlot(Player p)
	{
		return getNbOwnedPlot(p.getName(), p.getWorld());
	}
	
	public static int getNbOwnedPlot(Player p, World w)
	{
		return getNbOwnedPlot(p.getName(), w);
	}

	public static int getNbOwnedPlot(String name, World w)
	{ 
		return SqlManager.getPlotCount(w.getName(), name);
	}
	
	public static boolean isEconomyEnabled(World w)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && PlotMe_Core.globalUseEconomy && PlotMe_Core.economy != null;
	}
	
	public static boolean isEconomyEnabled(String name)
	{
		PlotMapInfo pmi = getMap(name);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && PlotMe_Core.globalUseEconomy;
	}
	
	public static boolean isEconomyEnabled(Player p)
	{
		if(PlotMe_Core.economy == null) return false;
		
		PlotMapInfo pmi = getMap(p);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && PlotMe_Core.globalUseEconomy;
	}
	
	public static boolean isEconomyEnabled(Block b)
	{
		PlotMapInfo pmi = getMap(b);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && PlotMe_Core.globalUseEconomy;
	}
	
	public static PlotMapInfo getMap(World w)
	{
		if(w == null)
			return null;
		else
		{			
			String worldname = w.getName().toLowerCase();
			
			if(PlotMe_Core.plotmaps.containsKey(worldname))
				return PlotMe_Core.plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public static PlotMapInfo getMap(String name)
	{
		String worldname = name.toLowerCase();
		
		if(PlotMe_Core.plotmaps.containsKey(worldname))
			return PlotMe_Core.plotmaps.get(worldname);
		else
			return null;
	}
	
	public static PlotMapInfo getMap(Location l)
	{
		if(l == null)
			return null;
		else
		{
			String worldname = l.getWorld().getName().toLowerCase();
			
			if(PlotMe_Core.plotmaps.containsKey(worldname))
				return PlotMe_Core.plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public static PlotMapInfo getMap(Player p)
	{
		if(p == null)
			return null;
		else
		{
			String worldname = p.getWorld().getName().toLowerCase();
			
			if(PlotMe_Core.plotmaps.containsKey(worldname))
				return PlotMe_Core.plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public static PlotMapInfo getMap(Block b)
	{
		if(b == null)
			return null;
		else
		{
			String worldname = b.getWorld().getName().toLowerCase();
			
			if(PlotMe_Core.plotmaps.containsKey(worldname))
				return PlotMe_Core.plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public static Plot getPlotById(World w, String id)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return null;
				
		return  pmi.getPlot(id);
	}
	
	public static Plot getPlotById(String name, String id)
	{
		PlotMapInfo pmi = getMap(name);
		
		if(pmi == null)
			return null;
				
		return  pmi.getPlot(id);
	}
	
	public static Plot getPlotById(Player p, String id)
	{
		PlotMapInfo pmi = getMap(p);
		
		if(pmi == null)
			return null;
				
		return  pmi.getPlot(id);
	}
	
	public static Plot getPlotById(Player p)
	{
		PlotMapInfo pmi = getMap(p);
		
		if(pmi == null)
			return null;
				
		return pmi.getPlot(getPlotId(p));
	}
	
	public static Plot getPlotById(Location l)
	{
		PlotMapInfo pmi = getMap(l);
		
		if(pmi == null)
			return null;
				
		return pmi.getPlot(getPlotId(l));
	}
	
	public static Plot getPlotById(Block b, String id)
	{
		PlotMapInfo pmi = getMap(b);
		
		if(pmi == null)
			return null;
				
		return pmi.getPlot(id);
	}
	
	public static Plot getPlotById(Block b)
	{
		PlotMapInfo pmi = getMap(b);
		
		if(pmi == null)
			return null;
				
		return pmi.getPlot(getPlotId(b.getLocation()));
	}
	
	public static void removePlot(World w, String id)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return;
				
		pmi.removePlot(id);
	}
	
	public static void addPlot(World w, String id, Plot plot)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return;
				
		pmi.addPlot(id, plot);
	}
	
	public static void deleteNextExpired(World w, CommandSender sender)
	{
		Plot expiredplot = SqlManager.getExpiredPlot(w.getName());
		
		clear(w, expiredplot, sender);
		
		String id = expiredplot.id;
		
		removePlot(w, id);
			
		getGenMan(w).removeOwnerDisplay(w, id);
		getGenMan(w).removeSellerDisplay(w, id);
		
		SqlManager.deletePlot(getIdX(id), getIdZ(id), w.getName().toLowerCase());
	}

	public static World getFirstWorld()
	{
		if(PlotMe_Core.plotmaps != null)
		{
			if(PlotMe_Core.plotmaps.keySet() != null)
			{
				if(PlotMe_Core.plotmaps.keySet().toArray().length > 0)
				{
					return Bukkit.getWorld((String) PlotMe_Core.plotmaps.keySet().toArray()[0]);
				}
			}
		}
		return null;
	}
	
	public static World getFirstWorld(String player)
	{
		String world = SqlManager.getFirstWorld(player);
		
		if(!world.equals(""))
			return Bukkit.getWorld(world);
		else
			return null;
	}
	
	public static boolean isPlotWorld(World w)
	{
		if(w == null)
			return false;
		else
		{
			if(getGenMan(w) == null)
				return false;
			else
				return PlotMe_Core.plotmaps.containsKey(w.getName().toLowerCase());
		}
	}
	
	public static boolean isPlotWorld(String name)
	{
		if(getGenMan(name) == null)
			return false;
		else
			return PlotMe_Core.plotmaps.containsKey(name.toLowerCase());
	}
	
	public static boolean isPlotWorld(Location l)
	{
		if(l == null)
			return false;
		else
		{
			if(getGenMan(l) == null)
				return false;
			else
				return PlotMe_Core.plotmaps.containsKey(l.getWorld().getName().toLowerCase());
		}
	}
	
	public static boolean isPlotWorld(Player p)
	{
		if(p == null)
			return false;
		else
		{
			if(getGenMan(p.getWorld()) == null)
				return false;
			else
				return PlotMe_Core.plotmaps.containsKey(p.getWorld().getName().toLowerCase());
		}
	}
	
	public static boolean isPlotWorld(Block b)
	{
		if(b == null)
			return false;
		else
		{
			if(getGenMan(b.getWorld()) == null)
				return false;
			else
				return PlotMe_Core.plotmaps.containsKey(b.getWorld().getName().toLowerCase());
		}
	}
	
	public static boolean isPlotWorld(BlockState b)
	{
		if(b == null)
			return false;
		else
		{
			if(getGenMan(b.getWorld()) == null)
				return false;
			else
				return PlotMe_Core.plotmaps.containsKey(b.getWorld().getName().toLowerCase());
		}
	}
	
	public static Plot createPlot(World w, String id, String owner)
	{
		if(isPlotAvailable(id, w) && !id.equals(""))
		{
			Plot plot = new Plot(owner, w, id, getMap(w).DaysToExpiration);

			setOwnerSign(w, plot);
			
			addPlot(w, id, plot);
			
			SqlManager.addPlot(plot, getIdX(id), getIdZ(id), w);
			return plot;
		}
		else
		{
			return null;
		}
	}
	
	public static boolean movePlot(World w, String idFrom, String idTo)
	{
		/*Location plot1Bottom = getPlotBottomLoc(w, idFrom);
		Location plot2Bottom = getPlotBottomLoc(w, idTo);
		Location plot1Top = getPlotTopLoc(w, idFrom);
		
		int distanceX = plot1Bottom.getBlockX() - plot2Bottom.getBlockX();
		int distanceZ = plot1Bottom.getBlockZ() - plot2Bottom.getBlockZ();
		
		for(int x = plot1Bottom.getBlockX(); x <= plot1Top.getBlockX(); x++)
		{
			for(int z = plot1Bottom.getBlockZ(); z <= plot1Top.getBlockZ(); z++)
			{
				Block plot1Block = w.getBlockAt(x, 0, z);
				Block plot2Block = w.getBlockAt(x - distanceX, 0, z - distanceZ);
				
				String plot1Biome = plot1Block.getBiome().name();
				String plot2Biome = plot2Block.getBiome().name();
				
				plot1Block.setBiome(Biome.valueOf(plot2Biome));
				plot2Block.setBiome(Biome.valueOf(plot1Biome));
				
				for(int y = 0; y < w.getMaxHeight() ; y++)
				{
					plot1Block = w.getBlockAt(new Location(w, x, y, z));
					int plot1Type = plot1Block.getTypeId();
					byte plot1Data = plot1Block.getData();
					
					plot2Block = w.getBlockAt(new Location(w, x - distanceX, y, z - distanceZ));
					int plot2Type = plot2Block.getTypeId();
					byte plot2Data = plot2Block.getData();
										
					plot1Block.setTypeIdAndData(plot2Type, plot2Data, false);
					plot1Block.setData(plot2Data, false);
										
					plot2Block.setTypeIdAndData(plot1Type, plot1Data, false);
					plot2Block.setData(plot1Data, false);
				}
			}
		}*/
		
		if(!getGenMan(w).movePlot(w, w, idFrom, idTo))
		{
			return false;
		}
		
		
		//HashMap<String, Plot> plots = getPlots(w);
		
		Plot plot1 = getPlotById(w, idFrom);
		Plot plot2 = getPlotById(w, idTo);
		
		if(plot1 != null)
		{
			if(plot2 != null)
			{		
				int idX = getIdX(idTo);
				int idZ = getIdZ(idTo);
				SqlManager.deletePlot(idX, idZ, plot2.world);
				removePlot(w, idFrom);
				removePlot(w, idTo);
				idX = getIdX(idFrom);
				idZ = getIdZ(idFrom);
				SqlManager.deletePlot(idX, idZ, plot1.world);
				
				plot2.id = "" + idX + ";" + idZ;
				SqlManager.addPlot(plot2, idX, idZ, w);
				addPlot(w, idFrom, plot2);
				
				for(int i = 0 ; i < plot2.comments.size() ; i++)
				{
					SqlManager.addPlotComment(plot2.comments.get(i), i, idX, idZ, plot2.world);
				}
				
				for(String player : plot2.allowed())
				{
					SqlManager.addPlotAllowed(player, idX, idZ, plot2.world);
				}
				
				idX = getIdX(idTo);
				idZ = getIdZ(idTo);
				plot1.id = "" + idX + ";" + idZ;
				SqlManager.addPlot(plot1, idX, idZ, w);
				addPlot(w, idTo, plot1);
				
				for(int i = 0 ; i < plot1.comments.size() ; i++)
				{
					SqlManager.addPlotComment(plot1.comments.get(i), i, idX, idZ, plot1.world);
				}
				
				for(String player : plot1.allowed())
				{
					SqlManager.addPlotAllowed(player, idX, idZ, plot1.world);
				}
				
				setOwnerSign(w, plot1);
				setSellSign(w, plot1);
				setOwnerSign(w, plot2);
				setSellSign(w, plot2);
				
			}
			else
			{				
				int idX = getIdX(idFrom);
				int idZ = getIdZ(idFrom);
				SqlManager.deletePlot(idX, idZ, plot1.world);
				removePlot(w, idFrom);
				idX = getIdX(idTo);
				idZ = getIdZ(idTo);
				plot1.id = "" + idX + ";" + idZ;
				SqlManager.addPlot(plot1, idX, idZ, w);
				addPlot(w, idTo, plot1);
				
				for(int i = 0 ; i < plot1.comments.size() ; i++)
				{
					SqlManager.addPlotComment(plot1.comments.get(i), i, idX, idZ, plot1.world);
				}
				
				for(String player : plot1.allowed())
				{
					SqlManager.addPlotAllowed(player, idX, idZ, plot1.world);
				}
				
				setOwnerSign(w, plot1);
				setSellSign(w, plot1);
				getGenMan(w).removeOwnerDisplay(w, idFrom);
				getGenMan(w).removeSellerDisplay(w, idFrom);
				
			}
		}
		else
		{
			if(plot2 != null)
			{
				int idX = getIdX(idTo);
				int idZ = getIdZ(idTo);
				SqlManager.deletePlot(idX, idZ, plot2.world);
				removePlot(w, idTo);
				
				idX = getIdX(idFrom);
				idZ = getIdZ(idFrom);
				plot2.id = "" + idX + ";" + idZ;
				SqlManager.addPlot(plot2, idX, idZ, w);
				addPlot(w, idFrom, plot2);
				
				for(int i = 0 ; i < plot2.comments.size() ; i++)
				{
					SqlManager.addPlotComment(plot2.comments.get(i), i, idX, idZ, plot2.world);
				}
				
				for(String player : plot2.allowed())
				{
					SqlManager.addPlotAllowed(player, idX, idZ, plot2.world);
				}
				
				setOwnerSign(w, plot2);
				setSellSign(w, plot2);
				getGenMan(w).removeOwnerDisplay(w, idTo);
				getGenMan(w).removeSellerDisplay(w, idTo);
			}
		}
		
		return true;
	}
	
	public static void RemoveLWC(World w, String id)
	{
		if(PlotMe_Core.usinglwc)
		{
			Location bottom = getGenMan(w).getBottom(w, id);
			Location top = getGenMan(w).getTop(w, id);
			final int x1 = bottom.getBlockX();
			final int y1 = bottom.getBlockY();
	    	final int z1 = bottom.getBlockZ();
	    	final int x2 = top.getBlockX();
	    	final int y2 = top.getBlockY();
	    	final int z2 = top.getBlockZ();
			final String wname = w.getName();
	    	
			Bukkit.getScheduler().runTaskAsynchronously(PlotMe_Core.self, new Runnable() 
			{	
				public void run() 
				{
					LWC lwc = com.griefcraft.lwc.LWC.getInstance();
					List<Protection> protections = lwc.getPhysicalDatabase().loadProtections(wname, x1, x2, y1, y2, z1, z2);

					for (Protection protection : protections) {
					    protection.remove();
					}
				}
			});
	    }
	}
	
	public static void setOwnerSign(World w, Plot plot)
	{			
		String line1 = "";
		String line2 = "";
		String line3 = "";
		String line4 = "";
		String id = plot.id;
				
		if((PlotMe_Core.caption("SignId") + id).length() > 16)
		{
			line1 = (PlotMe_Core.caption("SignId") + id).substring(0, 16);
			if((PlotMe_Core.caption("SignId") + id).length() > 32)
			{
				line2 = (PlotMe_Core.caption("SignId") + id).substring(16, 32);
			}
			else
			{
				line2 = (PlotMe_Core.caption("SignId") + id).substring(16);
			}
		}
		else
		{
			line1 = PlotMe_Core.caption("SignId") + id;
		}
		if((PlotMe_Core.caption("SignOwner") + plot.owner).length() > 16)
		{
			line3 = (PlotMe_Core.caption("SignOwner") + plot.owner).substring(0, 16);
			if((PlotMe_Core.caption("SignOwner") + plot.owner).length() > 32)
			{
				line4 = (PlotMe_Core.caption("SignOwner") + plot.owner).substring(16, 32);
			}
			else
			{
				line4 = (PlotMe_Core.caption("SignOwner") + plot.owner).substring(16);
			}
		}else{
			line3 = PlotMe_Core.caption("SignOwner") + plot.owner;
			line4 = "";
		}
		
		getGenMan(w).setOwnerDisplay(w, plot.id, line1, line2, line3, line4);
	}
	
	public static void setSellSign(World w, Plot plot)
	{
		String line1 = "";
		String line2 = "";
		String line3 = "";
		String line4 = "";
		String id = plot.id;
		
		getGenMan(w).removeSellerDisplay(w, id);
		
		if(plot.forsale || plot.auctionned)
		{
			if(plot.forsale)
			{
				line1 = PlotMe_Core.caption("SignForSale");
				line2 = PlotMe_Core.caption("SignPrice");
				if(plot.customprice % 1 == 0)
					line3 = PlotMe_Core.caption("SignPriceColor") + Math.round(plot.customprice);
				else
					line3 = PlotMe_Core.caption("SignPriceColor") + plot.customprice;
				line4 = "/plotme " + PlotMe_Core.caption("CommandBuy");
			}
			
			getGenMan(w).setSellerDisplay(w, plot.id, line1, line2, line3, line4);
			
			line1 = "";
			line2 = "";
			line3 = "";
			line4 = "";
			
			if(plot.auctionned)
			{				
				line1 = PlotMe_Core.caption("SignOnAuction");
				if(plot.currentbidder.equals(""))
					line2 = PlotMe_Core.caption("SignMinimumBid");
				else
					line2 = PlotMe_Core.caption("SignCurrentBid");
				if(plot.currentbid % 1 == 0)
					line3 = PlotMe_Core.caption("SignCurrentBidColor") + Math.round(plot.currentbid);
				else
					line3 = PlotMe_Core.caption("SignCurrentBidColor") + plot.currentbid;
				line4 = "/plotme " + PlotMe_Core.caption("CommandBid") + " <x>";
			}
			
			getGenMan(w).setAuctionDisplay(w, plot.id, line1, line2, line3, line4);
		}
	}
	
	public static void adjustLinkedPlots(String id, World world)
	{		
		//TODO
		Map<String, Plot> plots = new HashMap<String, Plot>(); //getPlots(world);
		
		IPlotMe_GeneratorManager gm = getGenMan(world);
		
		int x = getIdX(id);
		int z = getIdZ(id);
		
		Plot p11 = plots.get(id);
		
		if(p11 != null)
		{
			Plot p01 = plots.get((x - 1) + ";" + z);
			Plot p10 = plots.get(x + ";" + (z - 1));
			Plot p12 = plots.get(x + ";" + (z + 1));
			Plot p21 = plots.get((x + 1) + ";" + z);
			Plot p00 = plots.get((x - 1) + ";" + (z - 1));
			Plot p02 = plots.get((x - 1) + ";" + (z + 1));
			Plot p20 = plots.get((x + 1) + ";" + (z - 1));
			Plot p22 = plots.get((x + 1) + ";" + (z + 1));
			
			if(p01 != null && p01.owner.equalsIgnoreCase(p11.owner))
			{
				gm.fillroad(p01.id, p11.id, world);
			}
			
			if(p10 != null && p10.owner.equalsIgnoreCase(p11.owner))
			{
				gm.fillroad(p10.id, p11.id, world);
			}

			if(p12 != null && p12.owner.equalsIgnoreCase(p11.owner))
			{
				gm.fillroad(p12.id, p11.id, world);
			}

			if(p21 != null && p21.owner.equalsIgnoreCase(p11.owner))
			{
				gm.fillroad(p21.id, p11.id, world);
			}
			
			if(p00 != null && p10 != null && p01 != null &&
					p00.owner.equalsIgnoreCase(p11.owner) &&
					p11.owner.equalsIgnoreCase(p10.owner) &&
					p10.owner.equalsIgnoreCase(p01.owner))
			{
				gm.fillmiddleroad(p00.id, p11.id, world);
			}
			
			if(p10 != null && p20 != null && p21 != null &&
					p10.owner.equalsIgnoreCase(p11.owner) &&
					p11.owner.equalsIgnoreCase(p20.owner) &&
					p20.owner.equalsIgnoreCase(p21.owner))
			{
				gm.fillmiddleroad(p20.id, p11.id, world);
			}
			
			if(p01 != null && p02 != null && p12 != null &&
					p01.owner.equalsIgnoreCase(p11.owner) &&
					p11.owner.equalsIgnoreCase(p02.owner) &&
					p02.owner.equalsIgnoreCase(p12.owner))
			{
				gm.fillmiddleroad(p02.id, p11.id, world);
			}
			
			if(p12 != null && p21 != null && p22 != null &&
					p12.owner.equalsIgnoreCase(p11.owner) &&
					p11.owner.equalsIgnoreCase(p21.owner) &&
					p21.owner.equalsIgnoreCase(p22.owner))
			{
				gm.fillmiddleroad(p22.id, p11.id, world);
			}
			
		}
	}
	
	public static void setBiome(World w, Plot plot, Biome b)
	{
		String id = plot.id;
		
		getGenMan(w).setBiome(w, id, b);
		
		SqlManager.updatePlot(getIdX(id), getIdZ(id), plot.world, "biome", b.name());
	}
	
	public static void clear(World w, Plot plot, CommandSender cs)
	{
		String id = plot.id;
		
		/*getGenMan(w).clear(w, id);
		adjustWall(w, plot);
		RemoveLWC(w, plot);*/
		plot.forsale = false;
		plot.protect = false;
		plot.auctionned = false;
		plot.auctionneddate = null;
		plot.currentbid = 0;
		plot.currentbidder = "";
		
		String world = w.getName();
		int idX = getIdX(id);
		int idZ = getIdZ(id);
		
		SqlManager.updatePlot(idX, idZ, world, "forsale", false);
		SqlManager.updatePlot(idX, idZ, world, "protected", false);
		SqlManager.updatePlot(idX, idZ, world, "auctionned", false);
		SqlManager.updatePlot(idX, idZ, world, "auctionneddate", "");
		SqlManager.updatePlot(idX, idZ, world, "currentbid", 0);
		SqlManager.updatePlot(idX, idZ, world, "currentbidder", "");
				
		PlotMe_Core.plotsToClear.add(new String[]{world, id, cs.getName()});
	}
	
	public static boolean isPlotAvailable(String id, World world)
	{
		return isPlotAvailable(id, world.getName().toLowerCase());
	}
	
	public static boolean isPlotAvailable(String id, Player p)
	{
		return isPlotAvailable(id, p.getWorld().getName().toLowerCase());
	}
	
	public static boolean isPlotAvailable(String id, String world)
	{
		PlotMapInfo pmi = getMap(world);
		
		if(pmi != null)
		{
			if(pmi.getPlot(id) == null)
				return true;
			else
				return false;
			
			// !getPlots(world).containsKey(id);
		}
		else
		{
			return false;
		}
	}

	public static String getPlotId(Location l) 
	{
		if(getGenMan(l) == null)
			return "";
		
		IPlotMe_GeneratorManager gen = getGenMan(l);

		if(gen == null)
		{
			return "";
		}
		else
		{
			return gen.getPlotId(l);
		}
	}
	
	public static String getPlotId(Player p) 
	{
		if(getGenMan(p.getLocation()) == null)
			return "";
		
		IPlotMe_GeneratorManager gen = getGenMan(p.getLocation());

		if(gen == null)
			return "";
		else
		{
			return gen.getPlotId(p.getLocation());
		}
	}
	
	public static IPlotMe_GeneratorManager getGenMan(World w)
	{
		return PlotMe_Core.getGenManager(w);
	}
	
	public static IPlotMe_GeneratorManager getGenMan(Location l)
	{
		return PlotMe_Core.getGenManager(l.getWorld());
	}
	
	public static IPlotMe_GeneratorManager getGenMan(String s)
	{
		return PlotMe_Core.getGenManager(s);
	}

	public static Location getPlotBottomLoc(World w, String id) 
	{
		return getGenMan(w).getPlotBottomLoc(w, id);
	}

	public static Location getPlotTopLoc(World w, String id) 
	{
		return getGenMan(w).getPlotTopLoc(w, id);
	}

	public static void adjustWall(Location l) 
	{
		Plot plot = getPlotById(l);
		String id = getPlotId(l);
		World w = l.getWorld();
		
		if(plot == null)
		{
			getGenMan(w).adjustPlotFor(w, id, false, false, false, false);
		}
		else
		{
			getGenMan(w).adjustPlotFor(w, id, true, plot.protect, plot.auctionned, plot.forsale);
		}
	}
	
	public static void adjustWall(World w, Plot plot) 
	{
		String id = plot.id;
		getGenMan(w).adjustPlotFor(w, id, true, plot.protect, plot.auctionned, plot.forsale);
	}

	public static void removeOwnerSign(World w, String id) 
	{
		getGenMan(w).removeOwnerDisplay(w, id);
	}

	public static void removeSellSign(World w, String id) 
	{
		getGenMan(w).removeSellerDisplay(w, id);
	}

	public static void removeAuctionSign(World w, String id) 
	{
		getGenMan(w).removeAuctionDisplay(w, id);
	}

	public static boolean isValidId(World w, String id) 
	{
		return getGenMan(w).isValidId(id);
	}

	public static int bottomX(String id, World w) 
	{
		return getGenMan(w).bottomX(id, w);
	}

	public static int topX(String id, World w) 
	{
		return getGenMan(w).topX(id, w);
	}

	public static int bottomZ(String id, World w) 
	{
		return getGenMan(w).bottomZ(id, w);
	}
	
	public static int topZ(String id, World w) 
	{
		return getGenMan(w).topZ(id, w);
	}

	public static void setBiome(World w, String id, Biome biome) 
	{
		getGenMan(w).setBiome(w, id, biome);
		SqlManager.updatePlot(getIdX(id), getIdZ(id), w.getName(), "biome", biome.name());
	}

	public static Location getPlotHome(World w, String id) 
	{
		return getGenMan(w).getPlotHome(w, id);
	}

	public static List<Player> getPlayersInPlot(World w, String id) 
	{
		return getGenMan(w).getPlayersInPlot(w, id);
	}

	public static void regen(World w, Plot plot, CommandSender sender) 
	{
		getGenMan(w).regen(w, plot.id, sender);
	}
}
