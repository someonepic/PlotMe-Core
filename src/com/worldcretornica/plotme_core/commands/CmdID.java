package com.worldcretornica.plotme_core.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdID extends PlotCommand 
{
	public boolean exec(Player p, String[] args)
	{
		if (PlotMe_Core.cPerms(p, "PlotMe.admin.id"))
		{
			if(!PlotMeCoreManager.isPlotWorld(p))
			{
				Util.Send(p, RED + Util.C("MsgNotPlotWorld"));
			}
			else
			{
				String plotid = PlotMeCoreManager.getPlotId(p.getLocation());
				
				if(plotid.equals(""))
				{
					Util.Send(p, RED + Util.C("MsgNoPlotFound"));
				}
				else
				{
					p.sendMessage(AQUA + Util.C("WordPlot") + " " + Util.C("WordId") + ": " + RESET + plotid);
					
					Location bottom = PlotMeCoreManager.getPlotBottomLoc(p.getWorld(), plotid);
					Location top = PlotMeCoreManager.getPlotTopLoc(p.getWorld(), plotid);
					
					p.sendMessage(AQUA + Util.C("WordBottom") + ": " + RESET + bottom.getBlockX() + ChatColor.BLUE + "," + RESET + bottom.getBlockZ());
					p.sendMessage(AQUA + Util.C("WordTop") + ": " + RESET + top.getBlockX() + ChatColor.BLUE + "," + RESET + top.getBlockZ());
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
