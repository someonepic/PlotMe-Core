package com.worldcretornica.plotme_core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.utils.Util;

public class PlotMeSpool implements Runnable
{
	private String currentClearWorld = "";
	private String currentClearPlotId = "";
	private Long[] currentClear = null;
	private String[] plottoclear = null;

	private long timer = 0;
	
	@Override
	public void run() 
	{
		while(PlotMe_Core.self != null && PlotMe_Core.self.isEnabled())
		{
			//Plots to clear
			if(currentClearPlotId.equals(""))
			{
				if(!PlotMe_Core.plotsToClear.isEmpty())
				{
					plottoclear = (String[]) PlotMe_Core.plotsToClear.toArray()[0];
					
					World w = Bukkit.getWorld(plottoclear[0]);
					
					currentClearWorld = plottoclear[0];
					currentClearPlotId = plottoclear[1];
					currentClear = PlotMeCoreManager.getGenMan(w).clear(w, plottoclear[1], 50000, true, null);
					timer = System.currentTimeMillis();
					ShowProgress();
				}
			}
			else
			{
				World w = Bukkit.getWorld(currentClearWorld);
				currentClear = PlotMeCoreManager.getGenMan(w).clear(w, currentClearPlotId, 50000, false, currentClear);
			}
			
			if(!currentClearPlotId.equals("") && (timer + 20000 < System.currentTimeMillis()))
			{
				timer = System.currentTimeMillis();
				
				ShowProgress();
			}
			
			//Clear finished, adjust walls and remove LWC
			if(!currentClearPlotId.equals("") && currentClear == null)
			{
				World w = Bukkit.getWorld(currentClearWorld);
				if(w != null)
				{
					PlotMeCoreManager.getGenMan(currentClearWorld).adjustPlotFor(w, currentClearPlotId, true, false, false, false);
					PlotMeCoreManager.RemoveLWC(w, currentClearPlotId);
					PlotMeCoreManager.getGenMan(currentClearWorld).refreshPlotChunks(w, currentClearPlotId);
				}
				
				Msg("Plot " + currentClearPlotId + " cleared");
				
				currentClearWorld = "";
				currentClearPlotId = "";
				PlotMe_Core.plotsToClear.remove(plottoclear);
				
			}
			
			//Sleep
			if(!doSleep())
			{
				return;
			}
		}
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
			PlotMe_Core.self.getLogger().severe("The spool sleep was interrupted");
			e.printStackTrace();
			return false;
		}
	}
	
	private void Msg(String text)
	{
		Player p = Bukkit.getPlayer(plottoclear[2]);
		
		if(p != null)
		{
			Util.Send(p, text);
		}
		else
		{
			PlotMe_Core.self.getLogger().info(text);
		}
	}
	
	private void ShowProgress()
	{
		long done = getDoneBlocks();
		long total = getTotalPlotBlocks();
		double percent = ((double) done) / ((double) total) * 100;
		
		Msg("Plot " + currentClearPlotId + " cleared at " + Math.round(percent) + "% (" + done + "/" + total + " blocks)");
	}
	
	private long getTotalPlotBlocks()
	{
		World w = Bukkit.getWorld(plottoclear[0]);
		Location bottom = PlotMeCoreManager.getGenMan(w).getPlotBottomLoc(w, currentClearPlotId);
		Location top = PlotMeCoreManager.getGenMan(w).getPlotTopLoc(w, currentClearPlotId);
		
		return (top.getBlockX() - bottom.getBlockX()) * (top.getBlockY() - bottom.getBlockY()) * (top.getBlockZ() - bottom.getBlockZ());
	}
	
	private long getDoneBlocks()
	{		
		return currentClear[3];
	}
}
