package com.worldcretornica.plotme_core.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.PlotWorldEdit;

public class PlotWorldEditListener implements Listener 
{

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerMove(final PlayerMoveEvent event)
	{	
		Location from = event.getFrom();
		Location to = event.getTo();
		
		boolean changemask = false;
		Player p = event.getPlayer();
		
		if(to == null)
		{
			PlotWorldEdit.removeMask(p);
		}
		else
		{
			String idTo = "";
			
			if(from != null)
			{
				if(!from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName()))
				{
					changemask = true;
				}
				else if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ())
				{
					String idFrom = PlotMeCoreManager.getPlotId(from);
					idTo = PlotMeCoreManager.getPlotId(to);
					
					if(!idFrom.equalsIgnoreCase(idTo))
					{
						changemask = true;
					}
				}
			}
			
			if(changemask)
			{
				if(PlotMeCoreManager.isPlotWorld(to.getWorld()))
				{
					if(!PlotMe_Core.isIgnoringWELimit(p))
					{
						PlotWorldEdit.setMask(p, idTo);
					}
					else
					{
						PlotWorldEdit.removeMask(p);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		if(PlotMeCoreManager.isPlotWorld(p))
		{
			if(!PlotMe_Core.isIgnoringWELimit(p))
				PlotWorldEdit.setMask(p);
			else
				PlotWorldEdit.removeMask(p);
		}
		else
		{
			PlotWorldEdit.removeMask(p);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerTeleport(final PlayerTeleportEvent event)
	{
		Player p = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if(to == null)
		{
			PlotWorldEdit.removeMask(p);
		}
		else
		{
			if(from != null && PlotMeCoreManager.isPlotWorld(from) && !PlotMeCoreManager.isPlotWorld(to))
			{
				PlotWorldEdit.removeMask(p);
			}
			else if(PlotMeCoreManager.isPlotWorld(to))
			{
				PlotWorldEdit.setMask(p);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerPortal(final PlayerPortalEvent event)
	{
		Player p = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if(to == null)
		{
			PlotWorldEdit.removeMask(p);
		}
		else
		{
			if(from != null && PlotMeCoreManager.isPlotWorld(from) && !PlotMeCoreManager.isPlotWorld(to))
			{
				PlotWorldEdit.removeMask(p);
			}
			else if(PlotMeCoreManager.isPlotWorld(to))
			{
				PlotWorldEdit.setMask(p);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event)
	{		
		Player p = event.getPlayer();
		
		if(PlotMeCoreManager.isPlotWorld(p) && !PlotMe_Core.isIgnoringWELimit(p))
		{
			if(event.getMessage().startsWith("//gmask"))
			{
				event.setCancelled(true);
			}
			else if(event.getMessage().startsWith("//up"))
			{
				Plot plot = PlotMeCoreManager.getPlotById(p);
				
				if(plot == null || !plot.isAllowed(p.getName()))
				{
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		Player p = event.getPlayer();

		if(!PlotMe_Core.cPerms(p, "plotme.admin.buildanywhere") && PlotMeCoreManager.isPlotWorld(p) && !PlotMe_Core.isIgnoringWELimit(p))
		{
			if((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) 
					&& p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR)
			{
				Block b = event.getClickedBlock();
				Plot plot = PlotMeCoreManager.getPlotById(b);
				
				if(plot != null && plot.isAllowed(p.getName()))
					PlotWorldEdit.setMask(p, b.getLocation());
				else
					event.setCancelled(true);
			}
		}
	}
}