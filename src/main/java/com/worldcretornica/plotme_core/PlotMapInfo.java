package com.worldcretornica.plotme_core;

import java.util.HashMap;
import java.util.List;

public class PlotMapInfo 
{
	private HashMap<String, Plot> _plots;
	private String _world;
	
	public int PlotAutoLimit;
	public int DaysToExpiration;
	public List<Integer> ProtectedBlocks;
	public List<String> PreventedItems;
	public boolean UseEconomy;
	public boolean CanPutOnSale;
	public boolean CanSellToBank;
	public boolean RefundClaimPriceOnReset;
	public boolean RefundClaimPriceOnSetOwner;
	public double ClaimPrice;
	public double ClearPrice;
	public double AddPlayerPrice;
	public double DenyPlayerPrice;
	public double RemovePlayerPrice;
	public double UndenyPlayerPrice;
	public double PlotHomePrice;
	public boolean CanCustomizeSellPrice;
	public double SellToPlayerPrice;
	public double SellToBankPrice;
	public double BuyFromBankPrice;
	public double AddCommentPrice;
	public double BiomeChangePrice;
	public double ProtectPrice;
	public double DisposePrice;
	
	public boolean AutoLinkPlots;
	public boolean DisableExplosion;
	public boolean DisableIgnition;
	
	public PlotMapInfo()
	{
		_plots = new HashMap<String, Plot>();
	}
	
	public PlotMapInfo(String world)
	{
		_world = world;
		_plots = new HashMap<String, Plot>();
	}
	
	public Plot getPlot(String id)
	{
		if(!_plots.containsKey(id))
		{
			Plot plot = SqlManager.getPlot(_world, id);
			if (plot == null)
				return null;
			
			_plots.put(id, plot);
		}
		
		return _plots.get(id);
	}
	
	public void addPlot(String id, Plot plot)
	{
		if(!_plots.containsKey(id))
		{
			_plots.put(id, plot);
		}
	}
	
	public void removePlot(String id)
	{
		if(_plots.containsKey(id))
		{
			_plots.remove(id);
		}
	}
}
