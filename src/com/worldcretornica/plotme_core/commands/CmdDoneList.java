package com.worldcretornica.plotme_core.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotFinishedComparator;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;
import com.worldcretornica.plotme_core.utils.MinecraftFontWidthCalculator;

public class CmdDoneList extends PlotCommand 
{
	public boolean exec(Player p, String[] args) 
	{
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.done"))
		{
			PlotMapInfo pmi = PlotMeCoreManager.getMap(p);
			
			if(pmi == null)
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
				return true;
			}
			else
			{
				
				HashMap<String, Plot> plots = pmi.plots;
				List<Plot> finishedplots = new ArrayList<Plot>();
				int nbfinished = 0;
				int maxpage = 0;
				int pagesize = 8;
				int page = 1;
				
				if(args.length == 2)
				{
					try
					{
						page = Integer.parseInt(args[1]);
					}catch(NumberFormatException ex){}
				}
				
				for(String id : plots.keySet())
				{
					Plot plot = plots.get(id);
					
					if(plot.finished)
					{
						finishedplots.add(plot);
						nbfinished++;
					}
				}
				
				Collections.sort(finishedplots, new PlotFinishedComparator());
				
				maxpage = (int) Math.ceil(((double)nbfinished/(double)pagesize));
				
				if(finishedplots.size() == 0)
				{
					Util.Send(p, Util.C("MsgNoPlotsFinished"));
				}
				else
				{
					Util.Send(p, Util.C("MsgFinishedPlotsPage") + " " + page + "/" + maxpage);
					
					for(int i = (page-1) * pagesize; i < finishedplots.size() && i < (page * pagesize); i++)
					{	
						Plot plot = finishedplots.get(i);
						
						String starttext = "  " + AQUA + plot.id + RESET + " -> " + plot.owner;
						
						int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);						
						
						String line = starttext + Util.whitespace(550 - textLength) + "@" + plot.finisheddate;

						p.sendMessage(line);
					}
				}
			}
		}
		else
		{
			Util.Send(p, RED + Util.C("MsgPermissionDenied"));
		}
		return true;
	}
}
