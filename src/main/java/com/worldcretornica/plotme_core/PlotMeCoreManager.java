package com.worldcretornica.plotme_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
	private PlotMe_Core plugin = null;
	private MultiWorldPlugin multiworld = null;
	private MultiverseCore multiverse = null;
	
	private HashSet<String> playersignoringwelimit = null;
	private Map<String, PlotMapInfo> plotmaps = null;
	
	public PlotMeCoreManager(PlotMe_Core instance)
	{
		plugin = instance;
		setPlayersIgnoringWELimit(new HashSet<String>());
		plotmaps = new HashMap<String, PlotMapInfo>();
	}
	
	public boolean CreatePlotWorld(CommandSender cs, String worldname, String generator, Map<String, String> args)
	{
		//Get a seed
		Long seed = (new java.util.Random()).nextLong();
				
		//Check if we have multiworld
		if(getMultiworld() == null)
		{
			if(Bukkit.getPluginManager().isPluginEnabled("MultiWorld"))
				setMultiworld((MultiWorldPlugin)Bukkit.getPluginManager().getPlugin("MultiWorld"));
		}
		//Check if we have multiverse
		if(getMultiverse() == null)
		{
			if(Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core"))
				setMultiverse(((MultiverseCore)Bukkit.getPluginManager().getPlugin("Multiverse-Core")));
		}
		
		//Do we have one of them
		if(getMultiworld() == null && getMultiverse() == null)
		{
			cs.sendMessage("[" + plugin.getName() + "] " + Util().C("ErrWorldPluginNotFound"));
			return false;
		}
		
		
		//Find generator
		Plugin bukkitplugin = Bukkit.getPluginManager().getPlugin(generator);
		
		//Make generator create settings
		if(bukkitplugin == null)
		{
			cs.sendMessage("[" + plugin.getName() + "] " + Util().C("ErrCannotFindWorldGen") + " '" + generator + "'");
			return false;
		}
		else
		{
			ChunkGenerator cg = bukkitplugin.getDefaultWorldGenerator(worldname, "");
			if(cg != null && cg instanceof IPlotMe_ChunkGenerator)
			{
				//Create the generator configurations
				if(!((IPlotMe_ChunkGenerator) cg).getManager().createConfig(worldname, args, cs))
				{
					cs.sendMessage("[" + plugin.getName() + "] " + Util().C("ErrCannotCreateGen1") + " '" + generator + "' " + Util().C("ErrCannotCreateGen2"));
					return false;
				}
			}
			else
			{
				cs.sendMessage("[" + plugin.getName() + "] " + Util().C("ErrCannotCreateGen1") + " '" + generator + "' " + Util().C("ErrCannotCreateGen3"));
				return false;
			}
		}
		
		//Create manager configurations		
		File configfile = new File(plugin.getConfigPath(), "core-config.yml");
		
		FileConfiguration config = new YamlConfiguration();
		try 
		{
			config.load(configfile);
		} 
		catch (FileNotFoundException e) {} 
		catch (IOException e) 
		{
			plugin.getLogger().severe("can't read configuration file");
			e.printStackTrace();
			return false;
		} 
		catch (InvalidConfigurationException e) 
		{
			plugin.getLogger().severe("invalid configuration format");
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
		
		PlotMapInfo tempPlotInfo = new PlotMapInfo(plugin, worldname);
		ConfigurationSection currworld = worlds.getConfigurationSection(worldname);
		
		if(currworld == null)
		{
			currworld = worlds.createSection(worldname);
		}
		
		tempPlotInfo.PlotAutoLimit = Integer.parseInt(args.get("PlotAutoLimit"));
		tempPlotInfo.DaysToExpiration = Integer.parseInt(args.get("DaysToExpiration"));
		tempPlotInfo.ProtectedBlocks = plugin.getDefaultProtectedBlocks();
		tempPlotInfo.PreventedItems = plugin.getDefaultPreventedItems();
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
		
		addPlotMap(worldname.toLowerCase(), tempPlotInfo);
		
		try
		{
			config.save(configfile);
		} 
		catch (IOException e) 
		{
			plugin.getLogger().severe("error writting configurations");
			e.printStackTrace();
			return false;
		}
		
		
		//Are we using multiworld?
		if(getMultiworld() != null)
		{
			boolean success = false;
			
			if(getMultiworld().isEnabled())
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
					success = getMultiworld().getDataManager().makeWorld(worldname, env, seed, generator);
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
						getMultiworld().getDataManager().loadWorld(worldname, true);
						getMultiworld().getDataManager().save();
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						return false;
					}
				}
				else
					cs.sendMessage("[" + bukkitplugin.getName() + "] " + Util().C("ErrCannotCreateMW"));
			}
			else
			{
				cs.sendMessage("[" + bukkitplugin.getName() + "] " + Util().C("ErrMWDisabled"));
			}
			return success;
		}
		
		//Are we using multiverse?
		if(getMultiverse() != null)
		{
			boolean success = false;
			
			if(getMultiverse().isEnabled())
			{
				success = getMultiverse().getMVWorldManager().addWorld(worldname, Environment.NORMAL, seed.toString(), WorldType.NORMAL, true, generator);
				
				if(!success)
					cs.sendMessage("[" + bukkitplugin.getName() + "] " + Util().C("ErrCannotCreateMV"));
			}
			else
			{
				cs.sendMessage("[" + bukkitplugin.getName() + "] " + Util().C("ErrMVDisabled"));
			}
			return success;
		}
		
		return false;
	}
	
	public int getIdX(String id)
	{
		return Integer.parseInt(id.substring(0, id.indexOf(";")));
	}
	
	public int getIdZ(String id)
	{
		return Integer.parseInt(id.substring(id.indexOf(";") + 1));
	}
	
	public int getNbOwnedPlot(Player p)
	{
		return getNbOwnedPlot(p.getName(), p.getWorld());
	}
	
	public int getNbOwnedPlot(Player p, World w)
	{
		return getNbOwnedPlot(p.getName(), w);
	}

	public int getNbOwnedPlot(String name, World w)
	{ 
		return plugin.getSqlManager().getPlotCount(w.getName(), name);
	}
	
	public boolean isEconomyEnabled(World w)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && plugin.getGlobalUseEconomy() && plugin.getEconomy() != null;
	}
	
	public boolean isEconomyEnabled(String name)
	{
		PlotMapInfo pmi = getMap(name);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && plugin.getGlobalUseEconomy();
	}
	
	public boolean isEconomyEnabled(Player p)
	{
		if(plugin.getEconomy() == null) return false;
		
		PlotMapInfo pmi = getMap(p);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && plugin.getGlobalUseEconomy();
	}
	
	public boolean isEconomyEnabled(Block b)
	{
		PlotMapInfo pmi = getMap(b);
		
		if(pmi == null)
			return false;
		else
			return pmi.UseEconomy && plugin.getGlobalUseEconomy();
	}
	
	public PlotMapInfo getMap(World w)
	{
		if(w == null)
			return null;
		else
		{			
			String worldname = w.getName().toLowerCase();
			
			if(plotmaps.containsKey(worldname))
				return plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public PlotMapInfo getMap(String name)
	{
		String worldname = name.toLowerCase();
		
		if(plotmaps.containsKey(worldname))
			return plotmaps.get(worldname);
		else
			return null;
	}
	
	public PlotMapInfo getMap(Location l)
	{
		if(l == null)
			return null;
		else
		{
			String worldname = l.getWorld().getName().toLowerCase();
			
			if(plotmaps.containsKey(worldname))
				return plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public PlotMapInfo getMap(Player p)
	{
		if(p == null)
			return null;
		else
		{
			String worldname = p.getWorld().getName().toLowerCase();
			
			if(plotmaps.containsKey(worldname))
				return plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public PlotMapInfo getMap(Block b)
	{
		if(b == null)
			return null;
		else
		{
			String worldname = b.getWorld().getName().toLowerCase();
			
			if(plotmaps.containsKey(worldname))
				return plotmaps.get(worldname);
			else
				return null;
		}
	}
	
	public Plot getPlotById(World w, String id)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return null;
				
		return  pmi.getPlot(id);
	}
	
	public Plot getPlotById(String name, String id)
	{
		PlotMapInfo pmi = getMap(name);
		
		if(pmi == null)
			return null;
				
		return  pmi.getPlot(id);
	}
	
	public Plot getPlotById(Player p, String id)
	{
		PlotMapInfo pmi = getMap(p);
		
		if(pmi == null)
			return null;
				
		return  pmi.getPlot(id);
	}
	
	public Plot getPlotById(Player p)
	{
		PlotMapInfo pmi = getMap(p);
		
		if(pmi == null)
			return null;
				
		return pmi.getPlot(getPlotId(p));
	}
	
	public Plot getPlotById(Location l)
	{
		PlotMapInfo pmi = getMap(l);
		
		if(pmi == null)
			return null;
				
		return pmi.getPlot(getPlotId(l));
	}
	
	public Plot getPlotById(Block b, String id)
	{
		PlotMapInfo pmi = getMap(b);
		
		if(pmi == null)
			return null;
				
		return pmi.getPlot(id);
	}
	
	public Plot getPlotById(Block b)
	{
		PlotMapInfo pmi = getMap(b);
		
		if(pmi == null)
			return null;
				
		return pmi.getPlot(getPlotId(b.getLocation()));
	}
	
	public void removePlot(World w, String id)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return;
				
		pmi.removePlot(id);
	}
	
	public void addPlot(World w, String id, Plot plot)
	{
		PlotMapInfo pmi = getMap(w);
		
		if(pmi == null)
			return;
				
		pmi.addPlot(id, plot);
	}

	public World getFirstWorld()
	{
		if(plotmaps != null)
		{
			if(plotmaps.keySet() != null)
			{
				if(plotmaps.keySet().toArray().length > 0)
				{
					return Bukkit.getWorld((String) plotmaps.keySet().toArray()[0]);
				}
			}
		}
		return null;
	}
	
	public World getFirstWorld(String player)
	{
		String world = plugin.getSqlManager().getFirstWorld(player);
		
		if(!world.equals(""))
			return Bukkit.getWorld(world);
		else
			return null;
	}
	
	public boolean isPlotWorld(World w)
	{
		if(w == null)
			return false;
		else
		{
			if(getGenMan(w) == null)
				return false;
			else
				return plotmaps.containsKey(w.getName().toLowerCase());
		}
	}
	
	public boolean isPlotWorld(String name)
	{
		if(getGenMan(name) == null)
			return false;
		else
			return plotmaps.containsKey(name.toLowerCase());
	}
	
	public boolean isPlotWorld(Location l)
	{
		if(l == null)
			return false;
		else
		{
			if(getGenMan(l) == null)
				return false;
			else
				return plotmaps.containsKey(l.getWorld().getName().toLowerCase());
		}
	}
	
	public boolean isPlotWorld(Player p)
	{
		if(p == null)
			return false;
		else
		{
			if(getGenMan(p.getWorld()) == null)
				return false;
			else
				return plotmaps.containsKey(p.getWorld().getName().toLowerCase());
		}
	}
	
	public boolean isPlotWorld(Block b)
	{
		if(b == null)
			return false;
		else
		{
			if(getGenMan(b.getWorld()) == null)
				return false;
			else
				return plotmaps.containsKey(b.getWorld().getName().toLowerCase());
		}
	}
	
	public boolean isPlotWorld(BlockState b)
	{
		if(b == null)
			return false;
		else
		{
			if(getGenMan(b.getWorld()) == null)
				return false;
			else
				return plotmaps.containsKey(b.getWorld().getName().toLowerCase());
		}
	}
	
	public Plot createPlot(World w, String id, String owner)
	{
		if(isPlotAvailable(id, w) && !id.equals(""))
		{
			Plot plot = new Plot(plugin, owner, w, id, getMap(w).DaysToExpiration);

			setOwnerSign(w, plot);
			
			addPlot(w, id, plot);
			
			plugin.getSqlManager().addPlot(plot, getIdX(id), getIdZ(id), w);
			return plot;
		}
		else
		{
			return null;
		}
	}
	
	public boolean movePlot(World w, String idFrom, String idTo)
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
				plugin.getSqlManager().deletePlot(idX, idZ, plot2.world);
				removePlot(w, idFrom);
				removePlot(w, idTo);
				idX = getIdX(idFrom);
				idZ = getIdZ(idFrom);
				plugin.getSqlManager().deletePlot(idX, idZ, plot1.world);
				
				plot2.id = "" + idX + ";" + idZ;
				plugin.getSqlManager().addPlot(plot2, idX, idZ, w);
				addPlot(w, idFrom, plot2);
				
				for(int i = 0 ; i < plot2.comments.size() ; i++)
				{
					plugin.getSqlManager().addPlotComment(plot2.comments.get(i), i, idX, idZ, plot2.world);
				}
				
				for(String player : plot2.allowed())
				{
					plugin.getSqlManager().addPlotAllowed(player, idX, idZ, plot2.world);
				}
				
				idX = getIdX(idTo);
				idZ = getIdZ(idTo);
				plot1.id = "" + idX + ";" + idZ;
				plugin.getSqlManager().addPlot(plot1, idX, idZ, w);
				addPlot(w, idTo, plot1);
				
				for(int i = 0 ; i < plot1.comments.size() ; i++)
				{
					plugin.getSqlManager().addPlotComment(plot1.comments.get(i), i, idX, idZ, plot1.world);
				}
				
				for(String player : plot1.allowed())
				{
					plugin.getSqlManager().addPlotAllowed(player, idX, idZ, plot1.world);
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
				plugin.getSqlManager().deletePlot(idX, idZ, plot1.world);
				removePlot(w, idFrom);
				idX = getIdX(idTo);
				idZ = getIdZ(idTo);
				plot1.id = "" + idX + ";" + idZ;
				plugin.getSqlManager().addPlot(plot1, idX, idZ, w);
				addPlot(w, idTo, plot1);
				
				for(int i = 0 ; i < plot1.comments.size() ; i++)
				{
					plugin.getSqlManager().addPlotComment(plot1.comments.get(i), i, idX, idZ, plot1.world);
				}
				
				for(String player : plot1.allowed())
				{
					plugin.getSqlManager().addPlotAllowed(player, idX, idZ, plot1.world);
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
				plugin.getSqlManager().deletePlot(idX, idZ, plot2.world);
				removePlot(w, idTo);
				
				idX = getIdX(idFrom);
				idZ = getIdZ(idFrom);
				plot2.id = "" + idX + ";" + idZ;
				plugin.getSqlManager().addPlot(plot2, idX, idZ, w);
				addPlot(w, idFrom, plot2);
				
				for(int i = 0 ; i < plot2.comments.size() ; i++)
				{
					plugin.getSqlManager().addPlotComment(plot2.comments.get(i), i, idX, idZ, plot2.world);
				}
				
				for(String player : plot2.allowed())
				{
					plugin.getSqlManager().addPlotAllowed(player, idX, idZ, plot2.world);
				}
				
				setOwnerSign(w, plot2);
				setSellSign(w, plot2);
				getGenMan(w).removeOwnerDisplay(w, idTo);
				getGenMan(w).removeSellerDisplay(w, idTo);
			}
		}
		
		return true;
	}
	
	public void RemoveLWC(World w, String id)
	{
		if(plugin.getUsinglwc())
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
	    	
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() 
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
	
	public void setOwnerSign(World w, Plot plot)
	{			
		String line1 = "";
		String line2 = "";
		String line3 = "";
		String line4 = "";
		String id = plot.id;
				
		if((Util().C("SignId") + id).length() > 16)
		{
			line1 = (Util().C("SignId") + id).substring(0, 16);
			if((Util().C("SignId") + id).length() > 32)
			{
				line2 = (Util().C("SignId") + id).substring(16, 32);
			}
			else
			{
				line2 = (Util().C("SignId") + id).substring(16);
			}
		}
		else
		{
			line1 = Util().C("SignId") + id;
		}
		if((Util().C("SignOwner") + plot.owner).length() > 16)
		{
			line3 = (Util().C("SignOwner") + plot.owner).substring(0, 16);
			if((Util().C("SignOwner") + plot.owner).length() > 32)
			{
				line4 = (Util().C("SignOwner") + plot.owner).substring(16, 32);
			}
			else
			{
				line4 = (Util().C("SignOwner") + plot.owner).substring(16);
			}
		}else{
			line3 = Util().C("SignOwner") + plot.owner;
			line4 = "";
		}
		
		getGenMan(w).setOwnerDisplay(w, plot.id, line1, line2, line3, line4);
	}
	
	public void setSellSign(World w, Plot plot)
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
				line1 = Util().C("SignForSale");
				line2 = Util().C("SignPrice");
				if(plot.customprice % 1 == 0)
					line3 = Util().C("SignPriceColor") + Math.round(plot.customprice);
				else
					line3 = Util().C("SignPriceColor") + plot.customprice;
				line4 = "/plotme " + Util().C("CommandBuy");
			}
			
			getGenMan(w).setSellerDisplay(w, plot.id, line1, line2, line3, line4);
			
			line1 = "";
			line2 = "";
			line3 = "";
			line4 = "";
			
			if(plot.auctionned)
			{				
				line1 = Util().C("SignOnAuction");
				if(plot.currentbidder.equals(""))
					line2 = Util().C("SignMinimumBid");
				else
					line2 = Util().C("SignCurrentBid");
				if(plot.currentbid % 1 == 0)
					line3 = Util().C("SignCurrentBidColor") + Math.round(plot.currentbid);
				else
					line3 = Util().C("SignCurrentBidColor") + plot.currentbid;
				line4 = "/plotme " + Util().C("CommandBid") + " <x>";
			}
			
			getGenMan(w).setAuctionDisplay(w, plot.id, line1, line2, line3, line4);
		}
	}
	
	public void adjustLinkedPlots(String id, World world)
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
	
	public void setBiome(World w, Plot plot, Biome b)
	{
		String id = plot.id;
		
		getGenMan(w).setBiome(w, id, b);
		
		plugin.getSqlManager().updatePlot(getIdX(id), getIdZ(id), plot.world, "biome", b.name());
	}
	
	public void clear(World w, Plot plot, CommandSender cs, ClearReason reason)
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
		
		SqlManager sm = plugin.getSqlManager();
		
		sm.updatePlot(idX, idZ, world, "forsale", false);
		sm.updatePlot(idX, idZ, world, "protected", false);
		sm.updatePlot(idX, idZ, world, "auctionned", false);
		sm.updatePlot(idX, idZ, world, "auctionneddate", "");
		sm.updatePlot(idX, idZ, world, "currentbid", 0);
		sm.updatePlot(idX, idZ, world, "currentbidder", "");
				
		plugin.addPlotToClear(new PlotToClear(world, id, cs, reason));
	}
	
	public boolean isPlotAvailable(String id, World world)
	{
		return isPlotAvailable(id, world.getName().toLowerCase());
	}
	
	public boolean isPlotAvailable(String id, Player p)
	{
		return isPlotAvailable(id, p.getWorld().getName().toLowerCase());
	}
	
	public boolean isPlotAvailable(String id, String world)
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

	public String getPlotId(Location l) 
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
	
	public String getPlotId(Player p) 
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
	
	public IPlotMe_GeneratorManager getGenMan(World w)
	{
		return plugin.getGenManager(w);
	}
	
	public IPlotMe_GeneratorManager getGenMan(Location l)
	{
		return plugin.getGenManager(l.getWorld());
	}
	
	public IPlotMe_GeneratorManager getGenMan(String s)
	{
		return plugin.getGenManager(s);
	}

	public Location getPlotBottomLoc(World w, String id) 
	{
		return getGenMan(w).getPlotBottomLoc(w, id);
	}

	public Location getPlotTopLoc(World w, String id) 
	{
		return getGenMan(w).getPlotTopLoc(w, id);
	}

	public void adjustWall(Location l) 
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
	
	public void adjustWall(World w, Plot plot) 
	{
		String id = plot.id;
		getGenMan(w).adjustPlotFor(w, id, true, plot.protect, plot.auctionned, plot.forsale);
	}

	public void removeOwnerSign(World w, String id) 
	{
		getGenMan(w).removeOwnerDisplay(w, id);
	}

	public void removeSellSign(World w, String id) 
	{
		getGenMan(w).removeSellerDisplay(w, id);
	}

	public void removeAuctionSign(World w, String id) 
	{
		getGenMan(w).removeAuctionDisplay(w, id);
	}

	public boolean isValidId(World w, String id) 
	{
		return getGenMan(w).isValidId(id);
	}

	public int bottomX(String id, World w) 
	{
		return getGenMan(w).bottomX(id, w);
	}

	public int topX(String id, World w) 
	{
		return getGenMan(w).topX(id, w);
	}

	public int bottomZ(String id, World w) 
	{
		return getGenMan(w).bottomZ(id, w);
	}
	
	public int topZ(String id, World w) 
	{
		return getGenMan(w).topZ(id, w);
	}

	public void setBiome(World w, String id, Biome biome) 
	{
		getGenMan(w).setBiome(w, id, biome);
		plugin.getSqlManager().updatePlot(getIdX(id), getIdZ(id), w.getName(), "biome", biome.name());
	}

	public Location getPlotHome(World w, String id) 
	{
		return getGenMan(w).getPlotHome(w, id);
	}

	public List<Player> getPlayersInPlot(World w, String id) 
	{
		return getGenMan(w).getPlayersInPlot(w, id);
	}

	public void regen(World w, Plot plot, CommandSender sender) 
	{
		getGenMan(w).regen(w, plot.id, sender);
	}
	
	public PlotToClear getPlotLockInfo(String w, String id)
	{
		if(plugin.spools != null)
		{
			for(PlotMeSpool spool : plugin.spools)
			{
				PlotToClear ptc = spool.plottoclear;
				if(ptc != null && ptc.world.equalsIgnoreCase(w) && ptc.plotid.equalsIgnoreCase(id))
				{
					return ptc;
				}
			}
		}
		
		return plugin.getPlotLocked(w, id);
	}
	
	public HashSet<String> getPlayersIgnoringWELimit() {
		return playersignoringwelimit;
	}

	public void setPlayersIgnoringWELimit(HashSet<String> playersignoringwelimit) 
	{
		this.playersignoringwelimit = playersignoringwelimit;
	}
	
	public void addPlayerIgnoringWELimit(String player)
	{
		if(!isPlayerIgnoringWELimit(player))
			this.playersignoringwelimit.add(player);
	}
	
	public void removePlayerIgnoringWELimit(String player)
	{
		if(isPlayerIgnoringWELimit(player))
			this.playersignoringwelimit.remove(player);
	}
	
	public boolean isPlayerIgnoringWELimit(String player)
	{
		return this.playersignoringwelimit.contains(player);
	}

	public Map<String, PlotMapInfo> getPlotMaps() {
		return plotmaps;
	}

	public void addPlotMap(String world, PlotMapInfo map)
	{
		this.plotmaps.put(world, map);
	}
	
	public void removePlotMap(String world)
	{
		this.plotmaps.remove(world);
	}
	
	private Util Util()
	{
		return plugin.getUtil();
	}

	public MultiWorldPlugin getMultiworld() {
		return multiworld;
	}

	public void setMultiworld(MultiWorldPlugin multiworld) {
		this.multiworld = multiworld;
	}

	public MultiverseCore getMultiverse() {
		return multiverse;
	}

	public void setMultiverse(MultiverseCore multiverse) {
		this.multiverse = multiverse;
	}
}
