package com.worldcretornica.plotme_core;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;

import com.worldcretornica.plotme_core.utils.Util;

public class PlotRunnableDeleteExpire implements Runnable 
{
	public void run()
	{
		if(PlotMe_Core.worldcurrentlyprocessingexpired != null)
		{
			World w = PlotMe_Core.worldcurrentlyprocessingexpired;
			List<Plot> expiredplots = SqlManager.getExpiredPlots(w.getName(), 0, PlotMe_Core.nbperdeletionprocessingexpired);
		
			if(expiredplots.size() == 0)
			{
				PlotMe_Core.counterexpired = 0;
			}
			else
			{				
				String ids = "";
				
				for(Plot expiredplot : expiredplots)
				{										
					PlotMeCoreManager.clear(w, expiredplot, PlotMe_Core.cscurrentlyprocessingexpired, ClearReason.Expired);
					
					String id = expiredplot.id;
					ids += ChatColor.RED + id + ChatColor.RESET + ", ";
					
					PlotMeCoreManager.removePlot(w, id);
					PlotMeCoreManager.removeOwnerSign(w, id);
					PlotMeCoreManager.removeSellSign(w, id);
										
					SqlManager.deletePlot(PlotMeCoreManager.getIdX(id), PlotMeCoreManager.getIdZ(id), w.getName().toLowerCase());
					
					PlotMe_Core.counterexpired--;
				}
				
				if(ids.substring(ids.length() - 2).equals(", "))
				{
					ids = ids.substring(0, ids.length() - 2);
				}
				
				PlotMe_Core.cscurrentlyprocessingexpired.sendMessage(Util.C("MsgDeletedExpiredPlots") + " " + ids);
			}
			
			if(PlotMe_Core.counterexpired == 0)
			{
				PlotMe_Core.cscurrentlyprocessingexpired.sendMessage(Util.C("MsgDeleteSessionFinished"));
				PlotMe_Core.worldcurrentlyprocessingexpired = null;
				PlotMe_Core.cscurrentlyprocessingexpired = null;
			}
		}
	}
}
