package com.worldcretornica.plotme_core.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class PlotDenyListener implements Listener
{

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(final PlayerMoveEvent event)
	{
		Player p = event.getPlayer();
		
		if(PlotMeCoreManager.isPlotWorld(p) && !PlotMe_Core.cPerms(p, "plotme.admin.bypassdeny"))
		{
			Location to = event.getTo();
			
			String idTo = PlotMeCoreManager.getPlotId(to);
			
			if(!idTo.equals(""))
			{
				Plot plot = PlotMeCoreManager.getPlotById(p, idTo);
				
				if(plot != null && plot.isDenied(p.getName()))
				{
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerTeleport(final PlayerTeleportEvent event)
	{
		Player p = event.getPlayer();
		
		if(PlotMeCoreManager.isPlotWorld(p) && !PlotMe_Core.cPerms(p, "plotme.admin.bypassdeny"))
		{
			Location to = event.getTo();
			
			String idTo = PlotMeCoreManager.getPlotId(to);
			
			if(!idTo.equals(""))
			{
				Plot plot = PlotMeCoreManager.getPlotById(p, idTo);
				
				if(plot != null && plot.isDenied(p.getName()))
				{
					event.setTo(PlotMeCoreManager.getPlotHome(p.getWorld(), plot.id));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		
		if(PlotMeCoreManager.isPlotWorld(p) && !PlotMe_Core.cPerms(p, "plotme.admin.bypassdeny"))
		{
			String id = PlotMeCoreManager.getPlotId(p.getLocation());
			
			if(!id.equals(""))
			{
				Plot plot = PlotMeCoreManager.getPlotById(p, id);
				
				if(plot != null && plot.isDenied(p.getName()))
				{
					p.teleport(PlotMeCoreManager.getPlotHome(p.getWorld(), plot.id));
				}
			}
		}
	}
}
