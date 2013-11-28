package com.worldcretornica.plotme_core;

import org.bukkit.command.CommandSender;

public class PlotToClear {

    private String world;
    private String plotid;
    private CommandSender commandsender;
    private ClearReason reason;

    public PlotToClear(String w, String id, CommandSender cs, ClearReason r) {
        setWorld(w);
        setPlotId(id);
        setCommandSender(cs);
        setReason(r);
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getPlotId() {
        return plotid;
    }

    public void setPlotId(String plotid) {
        this.plotid = plotid;
    }

    public CommandSender getCommandSender() {
        return commandsender;
    }

    public void setCommandSender(CommandSender commandsender) {
        this.commandsender = commandsender;
    }

    public ClearReason getReason() {
        return reason;
    }

    public void setReason(ClearReason reason) {
        this.reason = reason;
    }
}
