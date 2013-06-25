package com.worldcretornica.plotme_core.utils;

import java.util.Map;

import org.bukkit.ChatColor;
import com.worldcretornica.plotme_core.PlotMe_Core;

public class Util 
{
	private PlotMe_Core plugin;
	
	private final String LOG;
	private final ChatColor GREEN = ChatColor.GREEN;
	private Map<String, String> captions = null;
	
	public Util(PlotMe_Core instance)
	{
		plugin = instance;
		LOG = "[" + plugin.getName() + " Event] ";
	}
	
	public void setCaptions(Map<String, String> cap)
	{
		captions = cap;
	}
	
	public void Dispose()
	{
		captions.clear();
		captions = null;
	}
	
	public String C(String s)
	{
		if(captions.containsKey(s))
		{
			return addColor(captions.get(s));
		}
		else
		{
			plugin.getLogger().warning("Missing caption: " + s);
			return "ERROR:Missing caption '" + s + "'";
		}
	}
	
	public String addColor(String string) 
	{
		return ChatColor.translateAlternateColorCodes('&', string);
    }
	
	public StringBuilder whitespace(int length) 
	{
		int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");
		
		StringBuilder ret = new StringBuilder();
				
		for(int i = 0; (i+spaceWidth) < length; i+=spaceWidth) {
			ret.append(" ");
		}
		
		return ret;
	}
	
	public String round(double money)
	{
		return (money % 1 == 0) ? "" + Math.round(money) : "" + money;
	}
	
	public void warn(String msg)
	{
		plugin.getLogger().warning(LOG + msg);
	}
	
	public String moneyFormat(double price)
	{
		return moneyFormat(price, true);
	}
	
	public String moneyFormat(double price, boolean showsign)
	{
		if(price == 0) return "";
		
		String format = round(Math.abs(price));
		
		if(plugin.getEconomy() != null)
		{
			format = (price <= 1 && price >= -1) ? format + " " + plugin.getEconomy().currencyNameSingular() : format + " " + plugin.getEconomy().currencyNamePlural();
		}
		
		if(showsign)	
			return GREEN + ((price > 0) ? "+" + format : "-" + format);
		else
			return GREEN + format;
	}
	
	public String FormatBiome(String biome)
	{
		biome = biome.toLowerCase();
		
		String[] tokens = biome.split("_");
		
		biome = "";
		
		for(String token : tokens)
		{
			token = token.substring(0, 1).toUpperCase() + token.substring(1);
			
			if(biome.equals(""))
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
