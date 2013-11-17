package com.worldcretornica.plotme_core.event;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlotTeleportHomeEvent extends PlotEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
    private boolean _canceled;
    private Player _player;
    private Location loc;
	
    public PlotTeleportHomeEvent(PlotMe_Core instance, World world, Plot plot, Player player)
    {
    	super(instance, plot, world);
    	_player = player;
    	loc = null;
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
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public void setHomeLocation(Location homelocation)
	{
	    loc = homelocation;
	}
	
	@Override
	public Location getHomeLocation()
	{
	    if(loc == null) {
	        return super.getHomeLocation();
	    } else {
	        return loc;
	    }
	}
}
