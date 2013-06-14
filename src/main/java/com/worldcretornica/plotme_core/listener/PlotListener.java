package com.worldcretornica.plotme_core.listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class PlotListener implements Listener 
{
	
	@EventHandler(priority = EventPriority.HIGH) //, ignoreCancelled = true
	public void onBlockBreak(final BlockBreakEvent event) 
	{	
		Block b = event.getBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b))
		{
			Player p = event.getPlayer();
			boolean canbuild = PlotMe_Core.cPerms(event.getPlayer(), "plotme.admin.buildanywhere");
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}
			else
			{
				Plot plot = PlotMeCoreManager.getMap(p).getPlot(id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else if(!plot.isAllowed(p.getName()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					plot.resetExpire(PlotMeCoreManager.getMap(b).DaysToExpiration);
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGH) //, ignoreCancelled = true
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		Block b = event.getBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b))
		{
			Player p = event.getPlayer();
			boolean canbuild = PlotMe_Core.cPerms(p, "plotme.admin.buildanywhere");
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}
			else
			{
				Plot plot = PlotMeCoreManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else if(!plot.isAllowed(p.getName()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					plot.resetExpire(PlotMeCoreManager.getMap(b).DaysToExpiration);
				}
			}
		}
	}
	
	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event)
	{
		if(!PlotMe_Core.cPerms(event.getPlayer(), "plotme.admin.buildanywhere"))
		{
			BlockFace bf = event.getBlockFace();
			Block b = event.getBlockClicked().getLocation().add(bf.getModX(), bf.getModY(), bf.getModZ()).getBlock();
			if(PlotMeCoreManager.isPlotWorld(b))
			{
				String id = PlotMeCoreManager.getPlotId(b.getLocation());
				Player p = event.getPlayer();
				
				if(id.equalsIgnoreCase(""))
				{
					p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
				else
				{
					Plot plot = PlotMeCoreManager.getPlotById(p,id);
					
					if (plot == null)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
					else if(!plot.isAllowed(p.getName()))
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketFill(final PlayerBucketFillEvent event)
	{
		if(!PlotMe_Core.cPerms(event.getPlayer(), "plotme.admin.buildanywhere"))
		{
			Block b = event.getBlockClicked();
			if(PlotMeCoreManager.isPlotWorld(b))
			{
				String id = PlotMeCoreManager.getPlotId(b.getLocation());
				Player p = event.getPlayer();
				
				if(id.equalsIgnoreCase(""))
				{
					p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
				else
				{
					Plot plot = PlotMeCoreManager.getPlotById(p,id);
					
					if (plot == null)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
					else if(!plot.isAllowed(p.getName()))
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		Block b = event.getClickedBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b))
		{
			PlotMapInfo pmi = PlotMeCoreManager.getMap(b);
			boolean blocked = false;
			Player player = event.getPlayer();
			boolean canbuild = PlotMe_Core.cPerms(player, "plotme.admin.buildanywhere");
			
			if(event.isBlockInHand() && event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				BlockFace face = event.getBlockFace();
				Block builtblock = b.getWorld().getBlockAt(b.getX() + face.getModX(), b.getY() + face.getModY(), b.getZ() + face.getModZ());
				
				String id = PlotMeCoreManager.getPlotId(builtblock.getLocation());
				
				Player p = event.getPlayer();
				
				if(id.equalsIgnoreCase(""))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					Plot plot = PlotMeCoreManager.getPlotById(p,id);
					
					if (plot == null)
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}
					else
					{
						if(!plot.isAllowed(p.getName()))
						{
							if(!canbuild)
							{
								p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
								event.setCancelled(true);
							}
						}
						else
						{
							plot.resetExpire(PlotMeCoreManager.getMap(b).DaysToExpiration);
						}
					}
				}
			}
			else
			{
				/*for(int blockid : pmi.ProtectedBlocks)
				{
					if(blockid == b.getTypeId())
					{
						if(!PlotMe.cPerms(player, "plotme.unblock." + blockid))
							blocked = true;
						
						break;
					}
				}*/
				
				if(pmi.ProtectedBlocks.contains(b.getTypeId()))
				{
					if(!PlotMe_Core.cPerms(player, "plotme.unblock." + b.getTypeId()))
						blocked = true;
				}
						
				ItemStack is = event.getItem();
				
				if(is != null && event.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					int itemid = is.getType().getId();
					byte itemdata = is.getData().getData();
					
					if(pmi.PreventedItems.contains("" + itemid) 
							|| pmi.PreventedItems.contains("" + itemid + ":" + itemdata))
					{
						if(!PlotMe_Core.cPerms(player, "plotme.unblock." + itemid))
							blocked = true;
					}
				}
				
				if(blocked)
				{
					String id = PlotMeCoreManager.getPlotId(b.getLocation());
					
					Player p = event.getPlayer();
					
					if(id.equalsIgnoreCase(""))
					{
						if(!canbuild)
						{
							if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
							{
								p.sendMessage(PlotMe_Core.caption("ErrCannotUse"));
							}
							event.setCancelled(true);
						}
					}
					else
					{
						Plot plot = PlotMeCoreManager.getPlotById(p,id);
						
						if (plot == null)
						{
							if(!canbuild)
							{
								if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
								{
									p.sendMessage(PlotMe_Core.caption("ErrCannotUse"));
								}
								event.setCancelled(true);
							}
						}
						else if(!plot.isAllowed(p.getName()))
						{
							if(!canbuild)
							{
								if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
								{
									p.sendMessage(PlotMe_Core.caption("ErrCannotUse"));
								}
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(final BlockSpreadEvent event)
	{
		Block b = event.getBlock();

		if(PlotMeCoreManager.isPlotWorld(b))
		{
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(final BlockFormEvent event)
	{
		Block b = event.getBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b))
		{
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockDamage(final BlockDamageEvent event)
	{
		Block b = event.getBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b))
		{
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFade(final BlockFadeEvent event)
	{
		Block b = event.getBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b))
		{
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFromTo(final BlockFromToEvent event)
	{
		Block b = event.getToBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b))
		{
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockGrow(final BlockGrowEvent event)
	{
		Block b = event.getBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b))
		{
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPistonExtend(final BlockPistonExtendEvent event)
	{
		if(PlotMeCoreManager.isPlotWorld(event.getBlock()))
		{
			BlockFace face = event.getDirection();
			
			for(Block b : event.getBlocks())
			{
				String id = PlotMeCoreManager.getPlotId(b.getLocation().add(face.getModX(), face.getModY(), face.getModZ()));
										
				if(id.equalsIgnoreCase(""))
				{
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPistonRetract(final BlockPistonRetractEvent event)
	{
		Block b = event.getRetractLocation().getBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b) && event.getBlock().getType() == Material.PISTON_STICKY_BASE)
		{
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
									
			if(id.equalsIgnoreCase(""))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onStructureGrow(final StructureGrowEvent event)
	{
		List<BlockState> blocks = event.getBlocks();
		boolean found = false;

		for(int i = 0 ; i < blocks.size(); i++)
		{
			if(found || PlotMeCoreManager.isPlotWorld(blocks.get(i)))
			{
				found = true;
				String id = PlotMeCoreManager.getPlotId(blocks.get(i).getLocation());
										
				if(id.equalsIgnoreCase(""))
				{
					event.getBlocks().remove(i);
					i--;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(final EntityExplodeEvent event)
	{	
		Location l = event.getLocation();
		
		if(l != null)
		{
			PlotMapInfo pmi = PlotMeCoreManager.getMap(l);
			
			if(pmi != null && pmi.DisableExplosion)
				event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockIgnite(final BlockIgniteEvent event)
	{
		Block b = event.getBlock();
		
		if(b != null)
		{
			PlotMapInfo pmi = PlotMeCoreManager.getMap(b);
			
			if(pmi != null)
			{
				if(pmi.DisableIgnition)
				{
					event.setCancelled(true);
				}
				else
				{
					String id = PlotMeCoreManager.getPlotId(b.getLocation());
					Player p = event.getPlayer();
					
					if(id.equalsIgnoreCase("") || p == null)
					{
						event.setCancelled(true);
					}
					else
					{
						Plot plot = PlotMeCoreManager.getPlotById(b,id);
						
						if (plot == null)
						{
							event.setCancelled(true);
						}
						else if(!plot.isAllowed(p.getName()))
						{
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingPlace(final HangingPlaceEvent event)
	{
		Block b = event.getBlock();
		
		if(PlotMeCoreManager.isPlotWorld(b))
		{
			String id = PlotMeCoreManager.getPlotId(b.getLocation());
			Player p = event.getPlayer();
			boolean canbuild = PlotMe_Core.cPerms(event.getPlayer(), "plotme.admin.buildanywhere");
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}
			else
			{
				Plot plot = PlotMeCoreManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else if(!plot.isAllowed(p.getName()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					plot.resetExpire(PlotMeCoreManager.getMap(b).DaysToExpiration);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingBreakByEntity(final HangingBreakByEntityEvent event)
	{
		Entity entity = event.getRemover();
		
		if(entity instanceof Player)
		{
			Player p = (Player)entity;
			
			boolean canbuild = PlotMe_Core.cPerms(p, "plotme.admin.buildanywhere");
	
			Location l = event.getEntity().getLocation();
			
			if(PlotMeCoreManager.isPlotWorld(l))
			{
				String id = PlotMeCoreManager.getPlotId(l);
				
				if(id.equalsIgnoreCase(""))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					Plot plot = PlotMeCoreManager.getPlotById(p,id);
					
					if (plot == null)
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}
					else if(!plot.isAllowed(p.getName()))
					{
						if(!canbuild)
						{
							p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
							event.setCancelled(true);
						}
					}
					else
					{
						plot.resetExpire(PlotMeCoreManager.getMap(l).DaysToExpiration);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEntity(final PlayerInteractEntityEvent event)
	{
		Location l = event.getRightClicked().getLocation();
		
		if(PlotMeCoreManager.isPlotWorld(l))
		{
			Player p = event.getPlayer();
			boolean canbuild = PlotMe_Core.cPerms(p, "plotme.admin.buildanywhere");
			String id = PlotMeCoreManager.getPlotId(l);
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
					event.setCancelled(true);
				}
			}
			else
			{
				Plot plot = PlotMeCoreManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else if(!plot.isAllowed(p.getName()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotBuild"));
						event.setCancelled(true);
					}
				}
				else
				{
					plot.resetExpire(PlotMeCoreManager.getMap(l).DaysToExpiration);
				}
			}
		}
	}	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerEggThrow(final PlayerEggThrowEvent event)
	{
		Location l = event.getEgg().getLocation();
		
		if(PlotMeCoreManager.isPlotWorld(l))
		{
			Player p = event.getPlayer();
			boolean canbuild = PlotMe_Core.cPerms(p, "plotme.admin.buildanywhere");
			String id = PlotMeCoreManager.getPlotId(l);
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe_Core.caption("ErrCannotUseEggs"));
					event.setHatching(false);
				}
			}
			else
			{
				Plot plot = PlotMeCoreManager.getPlotById(p,id);
				
				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotUseEggs"));
						event.setHatching(false);
					}
				}
				else if(!plot.isAllowed(p.getName()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe_Core.caption("ErrCannotUseEggs"));
						event.setHatching(false);
					}
				}
			}
		}
	}
	
/*
	@EventHandler
	public void onWorldInit(WorldInitEvent event) 
	{
		World w = event.getWorld();
		
		if (w.getName().equalsIgnoreCase("TestWorld"))
		{
			for (BlockPopulator pop : w.getPopulators()) 
			{
				if ((pop instanceof PlotRoadPopulator)) 
				{
					return;
				}
			}
			
			PlotMapInfo pmi = PlotManager.getMap(w);
			
			if(pmi == null)
			{
				w.getPopulators().add(new PlotRoadPopulator());
			}else{
				w.getPopulators().add(new PlotRoadPopulator(pmi));
			}
		}
	}*/
}
