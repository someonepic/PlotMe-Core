package com.worldcretornica.plotme_core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class PlotMeSpool implements Runnable {

    private PlotMe_Core plugin = null;
    private Long[] currentClear = null;
    private PlotToClear plottoclear = null;

    private int taskid = 0;

    private static String T;
    private static String G;
    private static String M;
    private static String k;

    public PlotMeSpool(PlotMe_Core instance, PlotToClear plotToClear) {
        plugin = instance;

        T = plugin.getUtil().C("Unit_1000000000000");
        G = plugin.getUtil().C("Unit_1000000000");
        M = plugin.getUtil().C("Unit_1000000");
        k = plugin.getUtil().C("Unit_1000");
        
        this.plottoclear = plotToClear;
    }

    @Override
    public void run() {
        if (this.plottoclear != null) {
            World w = Bukkit.getWorld(this.plottoclear.getWorld());

            if (w != null) {
                if (this.currentClear == null)
                    this.currentClear = this.plugin.getGenManager(w).clear(w, getPlotToClear().getPlotId(), plugin.getConfig().getInt("NbBlocksPerClearStep"), true, null);
                else {
                    this.currentClear = this.plugin.getGenManager(w).clear(w, getPlotToClear().getPlotId(), plugin.getConfig().getInt("NbBlocksPerClearStep"), false, this.currentClear);
                }

                ShowProgress();

                if (this.currentClear == null) {
                    this.plugin.getGenManager(getPlotToClear().getWorld()).adjustPlotFor(w, getPlotToClear().getPlotId(), true, false, false, false);
                    this.plugin.getPlotMeCoreManager().RemoveLWC(w, getPlotToClear().getPlotId());
                    this.plugin.getGenManager(getPlotToClear().getWorld()).refreshPlotChunks(w, getPlotToClear().getPlotId());

                    Msg(this.plugin.getUtil().C("WordPlot") + " " + getPlotToClear().getPlotId() + " " + this.plugin.getUtil().C("WordCleared"));

                    this.plugin.removePlotToClear(this.plottoclear, this.taskid);
                    this.plottoclear = null;
                }
            } else {
                this.plugin.removePlotToClear(this.plottoclear, this.taskid);
                this.plottoclear = null;
            }
        }
    }

    private void Msg(String text) {
        getPlotToClear().getCommandSender().sendMessage(text);
    }

    private void ShowProgress() {
        long done = getDoneBlocks();
        long total = getTotalPlotBlocks();
        double percent = ((double) done) / ((double) total) * 100;

        Msg(plugin.getUtil().C("WordPlot") + " " + ChatColor.GREEN + getPlotToClear().getPlotId() + ChatColor.RESET + " " + plugin.getUtil().C("WordIn") + " "
                + ChatColor.GREEN + getPlotToClear().getWorld() + ChatColor.RESET + " "
                + plugin.getUtil().C("WordIs") + " " + ChatColor.GREEN + ((double) Math.round(percent * 10) / 10) + "% " + ChatColor.RESET + plugin.getUtil().C("WordCleared")
                + " (" + ChatColor.GREEN + format(done) + ChatColor.RESET + "/" + ChatColor.GREEN + format(total) + ChatColor.RESET + " " + plugin.getUtil().C("WordBlocks") + ")");
    }

    private long getTotalPlotBlocks() {
        World w = Bukkit.getWorld(getPlotToClear().getWorld());
        Location bottom = plugin.getGenManager(w).getPlotBottomLoc(w, getPlotToClear().getPlotId());
        Location top = plugin.getGenManager(w).getPlotTopLoc(w, getPlotToClear().getPlotId());

        return (top.getBlockX() - bottom.getBlockX() + 1) * (top.getBlockY() - bottom.getBlockY() + 1) * (top.getBlockZ() - bottom.getBlockZ() + 1);
    }

    private long getDoneBlocks() {
        return currentClear[3];
    }

    private String format(Long count) {
        double buffer;

        if (count > 1000000000000L) {
            buffer = ((double) count / 1000000000000L);
            buffer = ((double) Math.round(buffer * 10) / 10);
            return buffer + T;
        }
        if (count > 1000000000) {
            buffer = ((double) count / 1000000000);
            buffer = ((double) Math.round(buffer * 10) / 10);
            return buffer + G;
        } else if (count > 1000000) {
            buffer = ((double) count / 1000000);
            buffer = ((double) Math.round(buffer * 10) / 10);
            return buffer + M;
        } else if (count > 1000) {
            buffer = ((double) count / 1000);
            buffer = ((double) Math.round(buffer * 10) / 10);
            return buffer + k;
        }
        return count.toString();
    }

    public PlotToClear getPlotToClear() {
        return plottoclear;
    }

    public void setTaskId(int taskid) {
        this.taskid = taskid;
    }
}
