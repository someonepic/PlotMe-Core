package com.worldcretornica.plotme_core.event;

import org.bukkit.World;
import org.bukkit.event.HandlerList;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class PlotLoadEvent extends PlotEvent
{
	private static final HandlerList handlers = new HandlerList();
	
    public PlotLoadEvent(PlotMe_Core instance, World world, Plot plot)
    {
    	super(instance, plot, world);
    }
    
	@Override
	public HandlerList getHandlers() 
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList() 
	{
        return handlers;
    }
}
