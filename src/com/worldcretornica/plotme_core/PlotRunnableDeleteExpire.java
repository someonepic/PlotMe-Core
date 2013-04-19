package com.worldcretornica.plotme_core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;

public class PlotRunnableDeleteExpire implements Runnable 
{
	public void run()
	{
		if(PlotMe_Core.worldcurrentlyprocessingexpired != null)
		{
			World w = PlotMe_Core.worldcurrentlyprocessingexpired;
			List<Plot> expiredplots = new ArrayList<Plot>();
			HashMap<String, Plot> plots = PlotMeCoreManager.getPlots(w);
			String date = PlotMe_Core.getDate();
			Plot expiredplot;
			
			for(String id : plots.keySet())
			{
				Plot plot = plots.get(id);
				
				if(!plot.protect && !plot.finished && plot.expireddate != null && PlotMe_Core.getDate(plot.expireddate).compareTo(date.toString()) < 0)
				{
					expiredplots.add(plot);
				}
				
				if(expiredplots.size() == PlotMe_Core.nbperdeletionprocessingexpired)
				{
					break;
				}
			}
			
			if(expiredplots.size() == 0)
			{
				PlotMe_Core.counterexpired = 0;
			}
			else
			{
				plots = null;
				
				Collections.sort(expiredplots);
				
				String ids = "";
				
				for(int ictr = 0; ictr < PlotMe_Core.nbperdeletionprocessingexpired && expiredplots.size() > 0; ictr++)
				{
					expiredplot = expiredplots.get(0);
					
					expiredplots.remove(0);
					
					PlotMeCoreManager.clear(w, expiredplot);
					
					String id = expiredplot.id;
					ids += ChatColor.RED + id + ChatColor.RESET + ", ";
					
					PlotMeCoreManager.getPlots(w).remove(id);
						
					PlotMeCoreManager.removeOwnerSign(w, id);
					PlotMeCoreManager.removeSellSign(w, id);
										
					SqlManager.deletePlot(PlotMeCoreManager.getIdX(id), PlotMeCoreManager.getIdZ(id), w.getName().toLowerCase());
					
					PlotMe_Core.counterexpired--;
				}
				
				if(ids.substring(ids.length() - 2).equals(", "))
				{
					ids = ids.substring(0, ids.length() - 2);
				}
				
				PlotMe_Core.cscurrentlyprocessingexpired.sendMessage("" + PlotMe_Core.PREFIX + PlotMe_Core.caption("MsgDeletedExpiredPlots") + " " + ids);
			}
			
			if(PlotMe_Core.counterexpired == 0)
			{
				PlotMe_Core.cscurrentlyprocessingexpired.sendMessage("" + PlotMe_Core.PREFIX + PlotMe_Core.caption("MsgDeleteSessionFinished"));
				PlotMe_Core.worldcurrentlyprocessingexpired = null;
				PlotMe_Core.cscurrentlyprocessingexpired = null;
			}
		}
	}
}
