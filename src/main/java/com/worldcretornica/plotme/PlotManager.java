package com.worldcretornica.plotme;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;

/**
 * @deprecated  As of release 0.14, replaced by {@link com.worldcretornica.plotme_core.PlotMeCoreManager#}
 * Some will require PlotMeCoreManager.getGenMan()
 */
@Deprecated
public class PlotManager
{
	public static String getPlotId(Location loc)
	{
		return PlotMeCoreManager.getPlotId(loc);
	}
	
	public static String getPlotId(Player player) 
	{
		return PlotMeCoreManager.getPlotId(player);
	}

	public static List<Player> getPlayersInPlot(World w, String id) 
	{
		return PlotMeCoreManager.getPlayersInPlot(w, id);
	}
	
	public static void adjustLinkedPlots(String id, World world)
	{
		PlotMeCoreManager.adjustLinkedPlots(id, world);
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
		return PlotMeCoreManager.isPlotAvailable(id, world);
	}
	
	public static Plot createPlot(World world, String id, String owner)
	{
		return PlotMeCoreManager.createPlot(world, id, owner);
	}
	
	public static void setOwnerSign(World world, Plot plot)
	{	
		PlotMeCoreManager.setOwnerSign(world, plot);
	}
	
	public static void setSellSign(World world, Plot plot)
	{
		PlotMeCoreManager.setSellSign(world, plot);
	}
	
	public static void removeOwnerSign(World world, String id)
	{
		PlotMeCoreManager.removeOwnerSign(world, id);
	}
	
	public static void removeSellSign(World world, String id)
	{
		PlotMeCoreManager.removeSellSign(world, id);
	}
	
	public static int getIdX(String id)
	{
		return PlotMeCoreManager.getIdX(id);
	}
	
	public static int getIdZ(String id)
	{
		return PlotMeCoreManager.getIdZ(id);
	}
	
	public static Location getPlotBottomLoc(World world, String id)
	{
		return PlotMeCoreManager.getPlotBottomLoc(world, id);
	}
	
	public static Location getPlotTopLoc(World world, String id)
	{
		return PlotMeCoreManager.getPlotTopLoc(world, id);
	}
	
	public static void setBiome(World w, String id, Plot plot, Biome b)
	{
		PlotMeCoreManager.setBiome(w, plot, b);
	}
	
	public static void refreshPlotChunks(World w, Plot plot)
	{
		PlotMeCoreManager.getGenMan(w).refreshPlotChunks(w, plot.id);
	}
	
	public static Location getTop(World w, Plot plot)
	{
		return PlotMeCoreManager.getGenMan(w).getTop(w, plot.id);
	}
	
	public static Location getBottom(World w, Plot plot)
	{
		return PlotMeCoreManager.getGenMan(w).getBottom(w, plot.id);
	}
	
	public static void clear(World w, Plot plot)
	{
		PlotMeCoreManager.clear(w, plot, null);
	}
	
	public static void clear(Location bottom, Location top)
	{
		PlotMeCoreManager.getGenMan(bottom).clear(bottom, top);
	}
		
	public static void adjustWall(Location l)
	{
		PlotMeCoreManager.adjustWall(l);
	}
		
	public static boolean isBlockInPlot(Plot plot, Location blocklocation)
	{
		return PlotMeCoreManager.getGenMan(blocklocation).isBlockInPlot(plot.id, blocklocation);
	}
	
	public static boolean movePlot(World w, String idFrom, String idTo)
	{
		return PlotMeCoreManager.movePlot(w, idFrom, idTo);
	}
	
	public static int getNbOwnedPlot(Player p)
	{
		return PlotMeCoreManager.getNbOwnedPlot(p);
	}
	
	public static int getNbOwnedPlot(Player p, World w)
	{
		return PlotMeCoreManager.getNbOwnedPlot(p, w);
	}

	public static int getNbOwnedPlot(String name, World w)
	{
		return PlotMeCoreManager.getNbOwnedPlot(name, w);
	}
		
	public static int bottomX(String id, World w)
	{
		return PlotMeCoreManager.bottomX(id, w);
	}
	
	public static int bottomZ(String id, World w)
	{
		return PlotMeCoreManager.bottomZ(id, w);
	}

	public static int topX(String id, World w)
	{
		return PlotMeCoreManager.topX(id, w);
	}
	
	public static int topZ(String id, World w)
	{
		return PlotMeCoreManager.topZ(id, w);
	}
	
	public static boolean isPlotWorld(World w)
	{
		return PlotMeCoreManager.isPlotWorld(w);
	}
	
	public static boolean isPlotWorld(String name)
	{
		return PlotMeCoreManager.isPlotWorld(name);
	}
	
	public static boolean isPlotWorld(Location l)
	{
		return PlotMeCoreManager.isPlotWorld(l);
	}
	
	public static boolean isPlotWorld(Player p)
	{
		return PlotMeCoreManager.isPlotWorld(p);
	}
	
	public static boolean isPlotWorld(Block b)
	{
		return PlotMeCoreManager.isPlotWorld(b);
	}
	
	public static boolean isPlotWorld(BlockState b)
	{
		return PlotMeCoreManager.isPlotWorld(b);
	}
	
	public static boolean isEconomyEnabled(World w)
	{
		return PlotMeCoreManager.isEconomyEnabled(w);
	}
	
	public static boolean isEconomyEnabled(String name)
	{
		return PlotMeCoreManager.isEconomyEnabled(name);
	}
	
	public static boolean isEconomyEnabled(Player p)
	{
		return PlotMeCoreManager.isEconomyEnabled(p);
	}
	
	public static boolean isEconomyEnabled(Block b)
	{
		return PlotMeCoreManager.isEconomyEnabled(b);
	}
	
	public static PlotMapInfo getMap(World w)
	{
		return PlotMeCoreManager.getMap(w);
	}
	
	public static PlotMapInfo getMap(String name)
	{
		return PlotMeCoreManager.getMap(name);
	}
	
	public static PlotMapInfo getMap(Location l)
	{
		return PlotMeCoreManager.getMap(l);
	}
	
	public static PlotMapInfo getMap(Player p)
	{
		return PlotMeCoreManager.getMap(p);
	}
	
	public static PlotMapInfo getMap(Block b)
	{
		return PlotMeCoreManager.getMap(b);
	}
		
	public static Plot getPlotById(World w, String id)
	{
		return PlotMeCoreManager.getPlotById(w, id);
	}
	
	public static Plot getPlotById(String name, String id)
	{
		return PlotMeCoreManager.getPlotById(name, id);
	}
	
	public static Plot getPlotById(Player p, String id)
	{
		return PlotMeCoreManager.getPlotById(p, id);
	}
	
	public static Plot getPlotById(Player p)
	{
		return PlotMeCoreManager.getPlotById(p);
	}
	
	public static Plot getPlotById(Location l)
	{
		return PlotMeCoreManager.getPlotById(l);
	}
	
	public static Plot getPlotById(Block b, String id)
	{
		return PlotMeCoreManager.getPlotById(b, id);
	}
	
	public static Plot getPlotById(Block b)
	{
		return PlotMeCoreManager.getPlotById(b);
	}
	
	public static void deleteNextExpired(World w, CommandSender sender)
	{
		PlotMeCoreManager.deleteNextExpired(w, sender);
	}

	public static World getFirstWorld()
	{
		return PlotMeCoreManager.getFirstWorld();
	}
	
	public static World getFirstWorld(String player)
	{
		return PlotMeCoreManager.getFirstWorld(player);
	}
	
	public static boolean isValidId(String id)
	{
		String[] coords = id.split(";");
		
		if(coords.length != 2)
			return false;
		else
		{
			try
			{
				Integer.parseInt(coords[0]);
				Integer.parseInt(coords[1]);
				return true;
			}catch(Exception e)
			{
				return false;
			}
		}
	}
	
	public static void regen(World w, Plot plot, CommandSender sender)
	{
		PlotMeCoreManager.regen(w, plot, sender);
	}
	
	public static Location getPlotHome(World w, Plot plot)
	{
		return PlotMeCoreManager.getPlotHome(w, plot.id);
	}
	
	public static void RemoveLWC(World w, Plot plot)
	{
		PlotMeCoreManager.RemoveLWC(w, plot.id);
	}
}
