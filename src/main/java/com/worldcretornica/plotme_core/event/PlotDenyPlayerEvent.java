package com.worldcretornica.plotme_core.event;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class PlotDenyPlayerEvent extends PlotEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
    private boolean _canceled;
    private Plot _plot;
    private World _world;
    private Player _player;
    private String _denied;
	
    public PlotDenyPlayerEvent(PlotMe_Core instance, World world, Plot plot, Player player, String denied)
    {
    	super(instance);
    	_plot = plot;
    	_world = world;
    	_player = player;
    	_denied = denied;
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
	
	public Plot getPlot()
	{
		return _plot;
	}
	
	public World getWorld()
	{
		return _world;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public String getAddedDenied()
	{
		return _denied;
	}

	public HashSet<String> getAllDenied()
	{
		return _plot.denied();
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
