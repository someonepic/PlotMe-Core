package com.worldcretornica.plotme_core.event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.HandlerList;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class PlotLoadEvent extends PlotEvent
{
	private static final HandlerList handlers = new HandlerList();
    private Plot _plot;
    private World _world;
	
    public PlotLoadEvent(PlotMe_Core instance, World world, Plot plot)
    {
    	super(instance);
    	_plot = plot;
    	_world = world;
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
	
	public Plot getPlot()
	{
		return _plot;
	}
	
	public World getWorld()
	{
		return _world;
	}
	
	public Location getUpperBound()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_world).getPlotTopLoc(_world, _plot.id);
	}
	
	public Location getLowerBound()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_world).getPlotBottomLoc(_world, _plot.id);
	}
	
	public String getOwner()
	{
		return _plot.owner;
	}
}
