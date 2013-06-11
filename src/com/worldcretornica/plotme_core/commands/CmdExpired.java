package com.worldcretornica.plotme_core.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;
import com.worldcretornica.plotme_core.utils.MinecraftFontWidthCalculator;

public class CmdExpired extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.expired"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
				return true;
			}
			else
			{
				int pagesize = 8;
				int page = 1;
				int maxpage = 0;
				int nbexpiredplots = 0; 
				World w = p.getWorld();
				List<Plot> expiredplots = new ArrayList<Plot>();
				HashMap<String, Plot> plots = PlotMeCoreManager.getPlots(w);
				String date = PlotMe_Core.getDate();
				
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
					
					if(!plot.protect && plot.expireddate != null && PlotMe_Core.getDate(plot.expireddate).compareTo(date.toString()) < 0)
					{
						nbexpiredplots++;
						expiredplots.add(plot);
					}
				}
				
				Collections.sort(expiredplots);
								
				maxpage = (int) Math.ceil(((double)nbexpiredplots/(double)pagesize));
				
				if(expiredplots.size() == 0)
				{
					Util.Send(p, Util.C("MsgNoPlotExpired"));
				}
				else
				{
					Util.Send(p, Util.C("MsgExpiredPlotsPage") + " " + page + "/" + maxpage);
					
					for(int i = (page-1) * pagesize; i < expiredplots.size() && i < (page * pagesize); i++)
					{	
						Plot plot = expiredplots.get(i);
						
						String starttext = "  " + AQUA + plot.id + RESET + " -> " + plot.owner;
						
						int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);						
						
						String line = starttext + Util.whitespace(550 - textLength) + "@" + plot.expireddate.toString();

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
