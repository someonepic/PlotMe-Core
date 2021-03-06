package com.worldcretornica.plotme_core.event;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;

public class PlotResetEvent extends PlotEvent implements Cancellable {

    private boolean _canceled;
    private CommandSender _reseter;

    public PlotResetEvent(PlotMe_Core instance, World world, Plot plot, CommandSender reseter) {
        super(instance, plot, world);
        _reseter = reseter;
    }

    @Override
    public boolean isCancelled() {
        return _canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        _canceled = cancel;
    }

    public CommandSender getReseter() {
        return _reseter;
    }
}
