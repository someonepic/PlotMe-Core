package com.worldcretornica.plotme_core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class PlotMeSpool implements Runnable {

    private PlotMe_Core plugin = null;
    private Long[] currentClear = null;
    public PlotToClear plottoclear = null;

    private long timer = 0;
    private boolean mustStop = false;

    private static String T;
    private static String G;
    private static String M;
    private static String k;

    public PlotMeSpool(PlotMe_Core instance) {
        plugin = instance;

        T = plugin.getUtil().C("Unit_1000000000000");
        G = plugin.getUtil().C("Unit_1000000000");
        M = plugin.getUtil().C("Unit_1000000");
        k = plugin.getUtil().C("Unit_1000");
    }

    @Override
    public void run() {
        while (!mustStop && plugin != null && plugin.isEnabled()) {
            //Plots to clear
            if (plottoclear == null) {
                plottoclear = plugin.pollPlotsToClear();

                if (plottoclear != null) {
                    World w = Bukkit.getWorld(plottoclear.world);

                    currentClear = plugin.getGenManager(w).clear(w, plottoclear.plotid, plugin.getConfig().getInt("NbBlocksPerClearStep"), true, null);
                    timer = System.currentTimeMillis();
                    ShowProgress();
                }
            } else {
                World w = Bukkit.getWorld(plottoclear.world);
                currentClear = plugin.getGenManager(w).clear(w, plottoclear.plotid, plugin.getConfig().getInt("NbBlocksPerClearStep"), false, currentClear);
            }

            if (plottoclear != null) {
                if (currentClear != null) {
                    if ((timer + 20000 < System.currentTimeMillis())) {
                        timer = System.currentTimeMillis();

                        ShowProgress();
                    }
                } //Clear finished, adjust walls and remove LWC
                else {
                    World w = Bukkit.getWorld(plottoclear.world);
                    if (w != null) {
                        plugin.getGenManager(plottoclear.world).adjustPlotFor(w, plottoclear.plotid, true, false, false, false);
                        plugin.getPlotMeCoreManager().RemoveLWC(w, plottoclear.plotid);
                        plugin.getGenManager(plottoclear.world).refreshPlotChunks(w, plottoclear.plotid);
                    }

                    Msg(plugin.getUtil().C("WordPlot") + " " + plottoclear.plotid + " " + plugin.getUtil().C("WordCleared"));
                    plottoclear = null;
                }
            }

            //Sleep
            if (!doSleep()) {
                return;
            }
        }
    }

    public void Stop() {
        mustStop = true;
    }

    private boolean doSleep() {
        try {
            Thread.sleep(200);
            return true;
        } catch (InterruptedException e) {
            plugin.getLogger().severe(plugin.getUtil().C("ErrSpoolInterrupted"));
            e.printStackTrace();
            return false;
        }
    }

    private void Msg(String text) {
        plottoclear.commandsender.sendMessage(text);
    }

    private void ShowProgress() {
        long done = getDoneBlocks();
        long total = getTotalPlotBlocks();
        double percent = ((double) done) / ((double) total) * 100;

        Msg(plugin.getUtil().C("WordPlot") + " " + ChatColor.GREEN + plottoclear.plotid + ChatColor.RESET + " " + plugin.getUtil().C("WordIn") + " "
                + ChatColor.GREEN + plottoclear.world + ChatColor.RESET + " "
                + plugin.getUtil().C("WordIs") + " " + ChatColor.GREEN + ((double) Math.round(percent * 10) / 10) + "% " + ChatColor.RESET + plugin.getUtil().C("WordCleared")
                + " (" + ChatColor.GREEN + format(done) + ChatColor.RESET + "/" + ChatColor.GREEN + format(total) + ChatColor.RESET + " " + plugin.getUtil().C("WordBlocks") + ")");
    }

    private long getTotalPlotBlocks() {
        World w = Bukkit.getWorld(plottoclear.world);
        Location bottom = plugin.getGenManager(w).getPlotBottomLoc(w, plottoclear.plotid);
        Location top = plugin.getGenManager(w).getPlotTopLoc(w, plottoclear.plotid);

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
        } else {
            return count.toString();
        }
    }
}
