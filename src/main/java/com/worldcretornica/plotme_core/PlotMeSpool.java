package com.worldcretornica.plotme_core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import com.worldcretornica.plotme_core.utils.Util;

public class PlotMeSpool implements Runnable
{
	private Long[] currentClear = null;
	public PlotToClear plottoclear = null;

	private long timer = 0;
	private boolean mustStop = false;
	
	@Override
	public void run() 
	{
		while(!mustStop && PlotMe_Core.self != null && PlotMe_Core.self.isEnabled())
		{
			//Plots to clear
			if(plottoclear == null)
			{
				plottoclear = PlotMe_Core.plotsToClear.poll();
				
				if(plottoclear != null)
				{
					World w = Bukkit.getWorld(plottoclear.world);
					
					currentClear = PlotMeCoreManager.getGenMan(w).clear(w, plottoclear.plotid, 50000, true, null);
					timer = System.currentTimeMillis();
					ShowProgress();
				}
			}
			else
			{
				World w = Bukkit.getWorld(plottoclear.world);
				currentClear = PlotMeCoreManager.getGenMan(w).clear(w, plottoclear.plotid, 50000, false, currentClear);
			}
			
			if(plottoclear != null)
			{
				if(currentClear != null)
				{
					if((timer + 20000 < System.currentTimeMillis()))
					{
						timer = System.currentTimeMillis();
						
						ShowProgress();
					}
				}
				//Clear finished, adjust walls and remove LWC
				else
				{
					World w = Bukkit.getWorld(plottoclear.world);
					if(w != null)
					{
						PlotMeCoreManager.getGenMan(plottoclear.world).adjustPlotFor(w, plottoclear.plotid, true, false, false, false);
						PlotMeCoreManager.RemoveLWC(w, plottoclear.plotid);
						PlotMeCoreManager.getGenMan(plottoclear.world).refreshPlotChunks(w, plottoclear.plotid);
					}
					
					Msg("Plot " + plottoclear.plotid + " cleared");
					plottoclear = null;
				}
			}
			
			//Sleep
			if(!doSleep())
			{
				return;
			}
		}
	}

	public void Stop()
	{
		mustStop = true;
	}
	
	private boolean doSleep()
	{
		try 
		{
			Thread.sleep(200);
			return true;
		}
		catch (InterruptedException e) 
		{
			PlotMe_Core.self.getLogger().severe(Util.C("ErrSpoolInterrupted"));
			e.printStackTrace();
			return false;
		}
	}
	
	private void Msg(String text)
	{
		Util.Send(plottoclear.commandsender, text);
	}
	
	private void ShowProgress()
	{
		long done = getDoneBlocks();
		long total = getTotalPlotBlocks();
		double percent = ((double) done) / ((double) total) * 100;
		
		Msg(Util.C("WordPlot") + " " + ChatColor.GREEN + plottoclear.plotid + ChatColor.RESET + " " + Util.C("WordIn") + " " +
				ChatColor.GREEN + plottoclear.world + ChatColor.RESET + " " +
				Util.C("WordIs") + " " + ChatColor.GREEN + ((double)Math.round(percent*10)/10) + "% " + ChatColor.RESET + Util.C("WordCleared") + 
				" (" + ChatColor.GREEN + format(done) + ChatColor.RESET + "/" + ChatColor.GREEN + format(total) + ChatColor.RESET + " " + Util.C("WordBlocks") + ")");
	}
	
	private long getTotalPlotBlocks()
	{
		World w = Bukkit.getWorld(plottoclear.world);
		Location bottom = PlotMeCoreManager.getGenMan(w).getPlotBottomLoc(w, plottoclear.plotid);
		Location top = PlotMeCoreManager.getGenMan(w).getPlotTopLoc(w, plottoclear.plotid);
		//PlotMe_Core.self.getLogger().info("(" + top.getBlockX() + "-" + bottom.getBlockX() + ")*(" + top.getBlockY() + "-" + bottom.getBlockY() + ")*(" + top.getBlockZ() + "-" + bottom.getBlockZ() + ")");
		
		return (top.getBlockX() - bottom.getBlockX() + 1) * (top.getBlockY() - bottom.getBlockY() + 1) * (top.getBlockZ() - bottom.getBlockZ() + 1);
	}
	
	private long getDoneBlocks()
	{		
		return currentClear[3];
	}
	
	private String format(Long count)
	{
		double buffer;
		
		if(count > 1000000000000L)
		{
			buffer = ((double)count / 1000000000000L);
			buffer = ((double)Math.round(buffer*10)/10);
			return buffer + "T";
		}
		if(count > 1000000000)
		{
			buffer = ((double)count / 1000000000);
			buffer = ((double)Math.round(buffer*10)/10);
			return buffer + "G";
		}
		else if(count > 1000000)
		{
			buffer = ((double)count / 1000000);
			buffer = ((double)Math.round(buffer*10)/10);
			return buffer + "M";
		}
		else if(count > 1000)
		{
			buffer = ((double)count / 1000);
			buffer = ((double)Math.round(buffer*10)/10);
			return buffer + "k";
		}
		else
		{
			return count.toString();
		}
	}
}
