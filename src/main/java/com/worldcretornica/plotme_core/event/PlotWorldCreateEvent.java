package com.worldcretornica.plotme_core.event;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.worldcretornica.plotme_core.PlotMe_Core;

public class PlotWorldCreateEvent extends PlotEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
    private boolean _canceled;
    private String _worldname;
    private CommandSender _creator;
    private Map<String, String> _parameters;
    
    public PlotWorldCreateEvent(PlotMe_Core instance, String worldname, CommandSender cs, Map<String, String> parameters)
    {
    	super(instance);
    	_worldname = worldname;
    	_creator = cs;
    	_parameters = parameters;
    }

	@Override
	public boolean isCancelled() 
	{
		return _canceled;
	}

	@Override
	public void setCancelled(boolean canceled) 
	{
		_canceled = canceled;
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
	
	public String getWorldName()
	{
		return _worldname;
	}
	
	public CommandSender getCreator()
	{
		return _creator;
	}
	
	public Map<String, String> getParameters()
	{
		return _parameters;
	}

}
