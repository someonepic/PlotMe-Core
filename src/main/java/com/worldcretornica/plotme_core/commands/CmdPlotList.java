package com.worldcretornica.plotme_core.commands;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;
import java.util.Calendar;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CmdPlotList extends PlotCommand {

    public CmdPlotList(PlotMe_Core instance) {
        super(instance);
    }

    public boolean exec(Player p, String[] args) {
        if (plugin.cPerms(p, "PlotMe.use.list")) {
            if (!plugin.getPlotMeCoreManager().isPlotWorld(p)) {
                p.sendMessage(RED + C("MsgNotPlotWorld"));
                return true;
            } else {
                String name;

                if (plugin.cPerms(p, "PlotMe.admin.list") && args.length == 2) {
                    name = args[1];
                    p.sendMessage(C("MsgListOfPlotsWhere") + " " + AQUA + name + RESET + " " + C("MsgCanBuild"));
                } else {
                    name = p.getName();
                    p.sendMessage(C("MsgListOfPlotsWhereYou"));
                }

                String oldworld = "";

                for (Plot plot : plugin.getSqlManager().getPlayerPlots(name)) {
                    if (!plot.world.equals("")) {
                        World world = Bukkit.getWorld(plot.world);
                        if (world != null) {
                            plugin.getPlotMeCoreManager().getMap(world).addPlot(plot.id, plot);
                        }
                    }

                    StringBuilder addition = new StringBuilder();

                    if (!oldworld.equalsIgnoreCase(plot.world)) {
                        oldworld = plot.world;
                        p.sendMessage("  World: " + plot.world);
                    }

                    if (plot.expireddate != null) {
                        java.util.Date tempdate = plot.expireddate;

                        if (tempdate.compareTo(Calendar.getInstance().getTime()) < 0) {
                            addition.append(RED + " @" + plot.expireddate.toString() + RESET);
                        } else {
                            addition.append(" @" + plot.expireddate.toString());
                        }
                    }

                    if (plot.auctionned) {
                        addition.append(" " + C("WordAuction") + ": " + GREEN + Util().round(plot.currentbid) + RESET + ((!plot.currentbidder.equals("")) ? " " + plot.currentbidder : ""));
                    }

                    if (plot.forsale) {
                        addition.append(" " + C("WordSell") + ": " + GREEN + Util().round(plot.customprice) + RESET);
                    }

                    if (plot.owner.equalsIgnoreCase(name)) {
                        if (plot.allowedcount() == 0) {
                            if (name.equalsIgnoreCase(p.getName())) {
                                p.sendMessage("  " + plot.id + " -> " + AQUA + ITALIC + C("WordYours") + RESET + addition);
                            } else {
                                p.sendMessage("  " + plot.id + " -> " + AQUA + ITALIC + plot.owner + RESET + addition);
                            }
                        } else {
                            StringBuilder helpers = new StringBuilder();
                            for (int i = 0; i < plot.allowedcount(); i++) {
                                helpers.append(AQUA).append(plot.allowed().toArray()[i]).append(RESET).append(", ");
                            }
                            if (helpers.length() > 2) {
                                helpers.delete(helpers.length() - 2, helpers.length());
                            }

                            if (name.equalsIgnoreCase(p.getName())) {
                                p.sendMessage("  " + plot.id + " -> " + AQUA + ITALIC + C("WordYours") + RESET + addition + ", " + C("WordHelpers") + ": " + helpers);
                            } else {
                                p.sendMessage("  " + plot.id + " -> " + AQUA + ITALIC + plot.owner + RESET + addition + ", " + C("WordHelpers") + ": " + helpers);
                            }
                        }
                    } else if (plot.isAllowed(name)) {
                        StringBuilder helpers = new StringBuilder();
                        for (int i = 0; i < plot.allowedcount(); i++) {
                            if (p.getName().equalsIgnoreCase((String) plot.allowed().toArray()[i])) {
                                if (name.equalsIgnoreCase(p.getName())) {
                                    helpers.append(AQUA).append(ITALIC).append("You").append(RESET).append(", ");
                                } else {
                                    helpers.append(AQUA).append(ITALIC).append(name).append(RESET).append(", ");
                                }
                            } else {
                                helpers.append(AQUA).append(plot.allowed().toArray()[i]).append(RESET).append(", ");
                            }
                        }
                        if (helpers.length() > 2) {
                            helpers.delete(helpers.length() - 2, helpers.length());
                        }

                        if (plot.owner.equalsIgnoreCase(p.getName())) {
                            p.sendMessage("  " + plot.id + " -> " + AQUA + C("WordYours") + RESET + addition + ", " + C("WordHelpers") + ": " + helpers);
                        } else {
                            p.sendMessage("  " + plot.id + " -> " + AQUA + plot.owner + C("WordPossessive") + RESET + addition + ", " + C("WordHelpers") + ": " + helpers);
                        }
                    }

                }
            }
        } else {
            p.sendMessage(RED + C("MsgPermissionDenied"));
        }
        return true;
    }
}
