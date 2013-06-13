package com.worldcretornica.plotme_core.commands;

import org.bukkit.ChatColor;
import com.worldcretornica.plotme_core.PlotMe_Core;

public abstract class PlotCommand
{
	protected final ChatColor RED = ChatColor.RED;
	protected final ChatColor RESET = ChatColor.RESET;
	protected final ChatColor AQUA = ChatColor.AQUA;
	protected final ChatColor GREEN = ChatColor.GREEN;
	protected final ChatColor ITALIC = ChatColor.ITALIC;
	protected final String LOG = "[Event] ";
	protected final boolean isAdv = PlotMe_Core.advancedlogging;
}
