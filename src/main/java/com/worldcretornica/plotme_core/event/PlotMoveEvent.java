package com.worldcretornica.plotme_core.event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class PlotMoveEvent extends PlotEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
    private boolean _canceled;
    private String _fromId;
    private String _toId;
    private World _fromworld;
    private World _toworld;
    private Player _mover;
	
    public PlotMoveEvent(PlotMe_Core instance, World fromworld, World toworld, String fromId, String toId, Player mover)
    {
    	super(instance);
    	_fromId = fromId;
    	_toId = toId;
    	_fromworld = fromworld;
    	_toworld = toworld;
    	_mover = mover;
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
	
	public Plot getPlotFrom()
	{
		return plugin.getPlotMeCoreManager().getPlotById(_fromworld, _fromId);
	}
	
	public Plot getPlotTo()
	{
		return plugin.getPlotMeCoreManager().getPlotById(_toworld, _toId);
	}
	
	public World getWorldFrom()
	{
		return _fromworld;
	}
	
	public World getWorldTo()
	{
		return _toworld;
	}
	
	public Player getPlayer()
	{
		return _mover;
	}
	
	public String getIdFrom()
	{
		return _fromId;
	}
	
	public String getIdTo()
	{
		return _toId;
	}
	
	public Location getUpperBoundFrom()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_fromworld).getPlotTopLoc(_fromworld, _fromId);
	}
	
	public Location getLowerBoundFrom()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_fromworld).getPlotBottomLoc(_fromworld, _fromId);
	}
	
	public Location getUpperBoundTo()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_toworld).getPlotTopLoc(_toworld, _toId);
	}
	
	public Location getLowerBoundTo()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_toworld).getPlotBottomLoc(_toworld, _toId);
	}
	
	public String getOwnerFrom()
	{
		Plot plot = getPlotFrom();
		if(plot != null)
			return plot.owner;
		else
			return "";
	}
	
	public String getOwnerTo()
	{
		Plot plot = getPlotTo();
		if(plot != null)
			return plot.owner;
		else
			return "";
	}
}
