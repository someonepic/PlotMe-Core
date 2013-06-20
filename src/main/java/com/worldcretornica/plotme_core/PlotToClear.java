package com.worldcretornica.plotme_core;

import org.bukkit.command.CommandSender;

public class PlotToClear 
{
	public String world;
	public String plotid;
	public CommandSender commandsender;
	public ClearReason reason;
	
	public PlotToClear(String w, String id, CommandSender cs, ClearReason r)
	{
		world = w;
		plotid = id;
		commandsender = cs;
		reason = r;
	}
}
