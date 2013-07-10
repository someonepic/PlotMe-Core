package com.worldcretornica.plotme_core.commands;

import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.event.PlotDoneChangeEvent;
import com.worldcretornica.plotme_core.event.PlotMeEventFactory;

public class CmdDone extends PlotCommand 
{
	public CmdDone(PlotMe_Core instance) {
		super(instance);
	}

	public boolean exec(Player p, String[] args)
	{
		if(plugin.cPerms(p, "PlotMe.use.done") || plugin.cPerms(p, "PlotMe.admin.done"))
		{
			if(!plugin.getPlotMeCoreManager().isPlotWorld(p))
			{
				p.sendMessage(RED + C("MsgNotPlotWorld"));
				return true;
			}
			else
			{
				String id = plugin.getPlotMeCoreManager().getPlotId(p.getLocation());
				
				if(id.equals(""))
				{
					p.sendMessage(RED + C("MsgNoPlotFound"));
				}
				else
				{
					if(!plugin.getPlotMeCoreManager().isPlotAvailable(id, p))
					{
						Plot plot = plugin.getPlotMeCoreManager().getPlotById(p,id);
						String name = p.getName();
						
						if(plot.owner.equalsIgnoreCase(name) || plugin.cPerms(p, "PlotMe.admin.done"))
						{
							PlotDoneChangeEvent event = PlotMeEventFactory.callPlotDoneEvent(plugin, p.getWorld(), plot, p, plot.finished); 
							
							if(!event.isCancelled())
							{
								if(plot.finished)
								{
									plot.setUnfinished();
									p.sendMessage(C("MsgUnmarkFinished"));
									
									if(isAdv)
										plugin.getLogger().info(LOG + name + " " + C("WordMarked") + " " + id + " " + C("WordFinished"));
								}
								else
								{
									plot.setFinished();
									p.sendMessage(C("MsgMarkFinished"));
									
									if(isAdv)
										plugin.getLogger().info(LOG + name + " " + C("WordMarked") + " " + id + " " + C("WordUnfinished"));
								}
							}
						}
					}
					else
					{
						p.sendMessage(RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		}
		else
		{
			p.sendMessage(RED + C("MsgPermissionDenied"));
		}
		return true;
	}
}
