package com.worldcretornica.plotme_core.event;

import org.bukkit.event.Event;

import com.worldcretornica.plotme_core.PlotMe_Core;

abstract class PlotEvent extends Event
{
	protected PlotMe_Core plugin;
	
	public PlotEvent(PlotMe_Core instance)
	{
		plugin = instance;
	}
}
