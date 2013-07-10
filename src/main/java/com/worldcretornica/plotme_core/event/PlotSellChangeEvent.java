package com.worldcretornica.plotme_core.event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class PlotSellChangeEvent extends PlotEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
    private boolean _canceled;
    private Plot _plot;
    private World _world;
    private Player _seller;
    private double _price;
    private boolean _soldToBank;
    private boolean _isForSale;
	
    public PlotSellChangeEvent(PlotMe_Core instance, World world, Plot plot, Player seller, double price, boolean soldToBank, boolean isForSale)
    {
    	super(instance);
    	_plot = plot;
    	_world = world;
    	_seller = seller;
    	_price = price;
    	_isForSale = isForSale;
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
		return _seller;
	}
	
	public double getPrice()
	{
		return _price;
	}
	
	public boolean isSoldToBank()
	{
		return _soldToBank;
	}
	
	public boolean isForSale()
	{
		return _isForSale;
	}
	
	public String getPreviousOwner()
	{
		return _plot.owner;
	}
	
	public Location getUpperBound()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_world).getPlotTopLoc(_world, _plot.id);
	}
	
	public Location getLowerBound()
	{
		return plugin.getPlotMeCoreManager().getGenMan(_world).getPlotBottomLoc(_world, _plot.id);
	}
}
