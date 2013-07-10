package com.worldcretornica.plotme_core.event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.worldcretornica.plotme_core.PlotMe_Core;

public class PlotCreateEvent extends PlotEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
    private boolean _canceled;
    private String _plotId;
    private World _world;
    private Player _creator;
    
    public PlotCreateEvent(PlotMe_Core instance, World world, String plotId, Player creator)
    {
    	super(instance);
    	_plotId = plotId;
    	_world = world;
    	_creator = creator;
    }
    
	@Override
	public boolean isCancelled() 
	{
		return _canceled;
	}

	@Override
	public void setCancelled(boolean cancel) 
	{
		_canceled = cancel;
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
	
	public String getPlotId()
	{
		return _plotId;
	}
	
	public World getWorld()
	{
		return _world;
	}
	
	public Player getPlayer()
	{
		return _creator;
	}
	
	public Location getUpperBound()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_world).getPlotTopLoc(_world, _plotId);
	}
	
	public Location getLowerBound()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_world).getPlotBottomLoc(_world, _plotId);
	}
}
