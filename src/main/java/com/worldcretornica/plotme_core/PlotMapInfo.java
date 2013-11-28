package com.worldcretornica.plotme_core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PlotMapInfo {

    private PlotMe_Core plugin = null;

    private ConcurrentHashMap<String, Plot> _plots;
    private List<String> _freedplots;
    private String _world;

    private int PlotAutoLimit;
    private int DaysToExpiration;
    private List<Integer> ProtectedBlocks;
    private List<String> PreventedItems;
    private boolean UseEconomy;
    private boolean CanPutOnSale;
    private boolean CanSellToBank;
    private boolean RefundClaimPriceOnReset;
    private boolean RefundClaimPriceOnSetOwner;
    private double ClaimPrice;
    private double ClearPrice;
    private double AddPlayerPrice;
    private double DenyPlayerPrice;
    private double RemovePlayerPrice;
    private double UndenyPlayerPrice;
    private double PlotHomePrice;
    private boolean CanCustomizeSellPrice;
    private double SellToPlayerPrice;
    private double SellToBankPrice;
    private double BuyFromBankPrice;
    private double AddCommentPrice;
    private double BiomeChangePrice;
    private double ProtectPrice;
    private double DisposePrice;

    private boolean AutoLinkPlots;
    private boolean DisableExplosion;
    private boolean DisableIgnition;

    private boolean UseProgressiveClear;
    private String NextFreed;

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
        if (id.isEmpty()) {
            return null;
        }
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
    
    public void addProtectedBlock(Integer blockId) {
        if(!isProtectedBlock(blockId)) {
            this.ProtectedBlocks.add(blockId);
        }
    }
    
    public void removeProtectedBlock(Integer blockId) {
        if(isProtectedBlock(blockId)) {
            this.ProtectedBlocks.remove(blockId);
        }
    }
    
    public boolean isProtectedBlock(Integer blockId) {
        return this.ProtectedBlocks.contains(blockId);
    }
    
    public List<Integer> getProtectedBlocks() {
        return this.ProtectedBlocks;
    }
    
    public void setProtectedBlocks(List<Integer> blockId) {
        this.ProtectedBlocks = blockId;
    }
    
    public void addPreventedItem(String itemId) {
        if(!isPreventedItem(itemId)) {
            this.PreventedItems.add(itemId);
        }
    }
    
    public void removePreventedItems(String itemId) {
        if(isPreventedItem(itemId)) {
            this.ProtectedBlocks.remove(itemId);
        }
    }
    
    public boolean isPreventedItem(String itemId) {
        return this.PreventedItems.contains(itemId);
    }
    
    public List<String> getPreventedItems() {
        return this.PreventedItems;
    }
    
    public void setPreventedItems(List<String> itemIds) {
        this.PreventedItems = itemIds;
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

    public int getPlotAutoLimit() {
        return PlotAutoLimit;
    }

    public void setPlotAutoLimit(int plotAutoLimit) {
        PlotAutoLimit = plotAutoLimit;
    }

    public int getDaysToExpiration() {
        return DaysToExpiration;
    }

    public void setDaysToExpiration(int daysToExpiration) {
        DaysToExpiration = daysToExpiration;
    }

    public boolean isUseEconomy() {
        return UseEconomy;
    }

    public void setUseEconomy(boolean useEconomy) {
        UseEconomy = useEconomy;
    }

    public boolean isCanPutOnSale() {
        return CanPutOnSale;
    }

    public void setCanPutOnSale(boolean canPutOnSale) {
        CanPutOnSale = canPutOnSale;
    }

    public boolean isCanSellToBank() {
        return CanSellToBank;
    }

    public void setCanSellToBank(boolean canSellToBank) {
        CanSellToBank = canSellToBank;
    }

    public boolean isRefundClaimPriceOnReset() {
        return RefundClaimPriceOnReset;
    }

    public void setRefundClaimPriceOnReset(boolean refundClaimPriceOnReset) {
        RefundClaimPriceOnReset = refundClaimPriceOnReset;
    }

    public boolean isRefundClaimPriceOnSetOwner() {
        return RefundClaimPriceOnSetOwner;
    }

    public void setRefundClaimPriceOnSetOwner(boolean refundClaimPriceOnSetOwner) {
        RefundClaimPriceOnSetOwner = refundClaimPriceOnSetOwner;
    }

    public double getClaimPrice() {
        return ClaimPrice;
    }

    public void setClaimPrice(double claimPrice) {
        ClaimPrice = claimPrice;
    }

    public double getClearPrice() {
        return ClearPrice;
    }

    public void setClearPrice(double clearPrice) {
        ClearPrice = clearPrice;
    }

    public double getAddPlayerPrice() {
        return AddPlayerPrice;
    }

    public void setAddPlayerPrice(double addPlayerPrice) {
        AddPlayerPrice = addPlayerPrice;
    }

    public double getDenyPlayerPrice() {
        return DenyPlayerPrice;
    }

    public void setDenyPlayerPrice(double denyPlayerPrice) {
        DenyPlayerPrice = denyPlayerPrice;
    }

    public double getRemovePlayerPrice() {
        return RemovePlayerPrice;
    }

    public void setRemovePlayerPrice(double removePlayerPrice) {
        RemovePlayerPrice = removePlayerPrice;
    }

    public double getUndenyPlayerPrice() {
        return UndenyPlayerPrice;
    }

    public void setUndenyPlayerPrice(double undenyPlayerPrice) {
        UndenyPlayerPrice = undenyPlayerPrice;
    }

    public double getPlotHomePrice() {
        return PlotHomePrice;
    }

    public void setPlotHomePrice(double plotHomePrice) {
        PlotHomePrice = plotHomePrice;
    }

    public boolean isCanCustomizeSellPrice() {
        return CanCustomizeSellPrice;
    }

    public void setCanCustomizeSellPrice(boolean canCustomizeSellPrice) {
        CanCustomizeSellPrice = canCustomizeSellPrice;
    }

    public double getSellToPlayerPrice() {
        return SellToPlayerPrice;
    }

    public void setSellToPlayerPrice(double sellToPlayerPrice) {
        SellToPlayerPrice = sellToPlayerPrice;
    }

    public double getSellToBankPrice() {
        return SellToBankPrice;
    }

    public void setSellToBankPrice(double sellToBankPrice) {
        SellToBankPrice = sellToBankPrice;
    }

    public double getBuyFromBankPrice() {
        return BuyFromBankPrice;
    }

    public void setBuyFromBankPrice(double buyFromBankPrice) {
        BuyFromBankPrice = buyFromBankPrice;
    }

    public double getAddCommentPrice() {
        return AddCommentPrice;
    }

    public void setAddCommentPrice(double addCommentPrice) {
        AddCommentPrice = addCommentPrice;
    }

    public double getBiomeChangePrice() {
        return BiomeChangePrice;
    }

    public void setBiomeChangePrice(double biomeChangePrice) {
        BiomeChangePrice = biomeChangePrice;
    }

    public double getProtectPrice() {
        return ProtectPrice;
    }

    public void setProtectPrice(double protectPrice) {
        ProtectPrice = protectPrice;
    }

    public double getDisposePrice() {
        return DisposePrice;
    }

    public void setDisposePrice(double disposePrice) {
        DisposePrice = disposePrice;
    }

    public boolean isAutoLinkPlots() {
        return AutoLinkPlots;
    }

    public void setAutoLinkPlots(boolean autoLinkPlots) {
        AutoLinkPlots = autoLinkPlots;
    }

    public boolean isDisableExplosion() {
        return DisableExplosion;
    }

    public void setDisableExplosion(boolean disableExplosion) {
        DisableExplosion = disableExplosion;
    }

    public boolean isDisableIgnition() {
        return DisableIgnition;
    }

    public void setDisableIgnition(boolean disableIgnition) {
        DisableIgnition = disableIgnition;
    }

    public boolean isUseProgressiveClear() {
        return UseProgressiveClear;
    }

    public void setUseProgressiveClear(boolean useProgressiveClear) {
        UseProgressiveClear = useProgressiveClear;
    }
}
