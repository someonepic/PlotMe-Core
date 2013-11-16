package com.worldcretornica.plotme_core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PlotMapInfo {

    private PlotMe_Core plugin = null;

    private ConcurrentHashMap<String, Plot> _plots;
    private List<String> _freedplots;
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

    public boolean UseProgressiveClear;
    public String NextFreed;

    public PlotMapInfo(PlotMe_Core instance) {
        plugin = instance;
        _plots = new ConcurrentHashMap<String, Plot>();
        _freedplots = new ArrayList<String>();
    }

    public PlotMapInfo(PlotMe_Core instance, String world) {
        this(instance);
        _world = world;
        _freedplots = plugin.getSqlManager().getFreed(world);
    }
    
    public int getNbPlots()
    {
        return _plots.size();
    }

    public Plot getPlot(String id) {
        if (!_plots.containsKey(id)) {
            Plot plot = plugin.getSqlManager().getPlot(_world, id);
            if (plot == null) {
                return null;
            }

            _plots.put(id, plot);
        }

        return _plots.get(id);
    }

    public ConcurrentHashMap<String, Plot> getLoadedPlots() {
        return _plots;
    }

    public void addPlot(String id, Plot plot) {
        _plots.putIfAbsent(id, plot);
    }

    public void removePlot(String id) {
        if (_plots.containsKey(id)) {
            _plots.remove(id);
        }
    }

    public void addFreed(String id) {
        if (!_freedplots.contains(id)) {
            _freedplots.add(id);
            int x = plugin.getPlotMeCoreManager().getIdX(id);
            int z = plugin.getPlotMeCoreManager().getIdZ(id);
            plugin.getSqlManager().addFreed(x, z, _world);
        }
    }

    public void removeFreed(String id) {
        if (_freedplots.contains(id)) {
            _freedplots.remove(id);
            int x = plugin.getPlotMeCoreManager().getIdX(id);
            int z = plugin.getPlotMeCoreManager().getIdZ(id);
            plugin.getSqlManager().deleteFreed(x, z, _world);
        }
    }

    public String getNextFreed() {
        if (!_freedplots.isEmpty()) {
            return _freedplots.get(0);
        } else {
            return NextFreed;
        }
    }

    public void setNextFreed(String id) {
        NextFreed = id;
        plugin.saveWorldConfig(_world);
    }
}
