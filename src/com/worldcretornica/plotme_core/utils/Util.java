package com.worldcretornica.plotme_core.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.worldcretornica.plotme_core.PlotMe_Core;

public class Util 
{
	private static final String LOG = "[" + PlotMe_Core.NAME + " Event] ";
	private static final ChatColor GREEN = ChatColor.GREEN;
	
	public static String C(String caption)
	{
		return PlotMe_Core.caption(caption);
	}
	
	public static StringBuilder whitespace(int length) 
	{
		int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");
		
		StringBuilder ret = new StringBuilder();
				
		for(int i = 0; (i+spaceWidth) < length; i+=spaceWidth) {
			ret.append(" ");
		}
		
		return ret;
	}
	
	public static String round(double money)
	{
		return (money % 1 == 0) ? "" + Math.round(money) : "" + money;
	}
	
	public static void warn(String msg)
	{
		PlotMe_Core.self.getLogger().warning(LOG + msg);
	}
	
	public static String moneyFormat(double price)
	{
		return moneyFormat(price, true);
	}
	
	public static String moneyFormat(double price, boolean showsign)
	{
		if(price == 0) return "";
		
		String format = round(Math.abs(price));
		
		if(PlotMe_Core.economy != null)
		{
			format = (price <= 1 && price >= -1) ? format + " " + PlotMe_Core.economy.currencyNameSingular() : format + " " + PlotMe_Core.economy.currencyNamePlural();
		}
		
		if(showsign)	
			return GREEN + ((price > 0) ? "+" + format : "-" + format);
		else
			return GREEN + format;
	}
	
	public static void Send(CommandSender cs, String text)
	{
		cs.sendMessage(text);
	}
	
	public static String FormatBiome(String biome)
	{
		biome = biome.toLowerCase();
		
		String[] tokens = biome.split("_");
		
		biome = "";
		
		for(String token : tokens)
		{
			token = token.substring(0, 1).toUpperCase() + token.substring(1);
			
			if(biome.equalsIgnoreCase(""))
			{
				biome = token;
			}
			else
			{
				biome = biome + "_" + token;
			}
		}

		return biome;
	}
}
