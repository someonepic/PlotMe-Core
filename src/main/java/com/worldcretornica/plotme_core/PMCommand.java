package com.worldcretornica.plotme_core;

import com.worldcretornica.plotme_core.commands.CmdAdd;
import com.worldcretornica.plotme_core.commands.CmdAddTime;
import com.worldcretornica.plotme_core.commands.CmdAuction;
import com.worldcretornica.plotme_core.commands.CmdAuto;
import com.worldcretornica.plotme_core.commands.CmdBid;
import com.worldcretornica.plotme_core.commands.CmdBiome;
import com.worldcretornica.plotme_core.commands.CmdBiomeList;
import com.worldcretornica.plotme_core.commands.CmdBuy;
import com.worldcretornica.plotme_core.commands.CmdClaim;
import com.worldcretornica.plotme_core.commands.CmdClear;
import com.worldcretornica.plotme_core.commands.CmdComment;
import com.worldcretornica.plotme_core.commands.CmdComments;
import com.worldcretornica.plotme_core.commands.CmdCreateWorld;
import com.worldcretornica.plotme_core.commands.CmdDeny;
import com.worldcretornica.plotme_core.commands.CmdDispose;
import com.worldcretornica.plotme_core.commands.CmdDone;
import com.worldcretornica.plotme_core.commands.CmdDoneList;
import com.worldcretornica.plotme_core.commands.CmdExpired;
import com.worldcretornica.plotme_core.commands.CmdHome;
import com.worldcretornica.plotme_core.commands.CmdID;
import com.worldcretornica.plotme_core.commands.CmdInfo;
import com.worldcretornica.plotme_core.commands.CmdMove;
import com.worldcretornica.plotme_core.commands.CmdPlotList;
import com.worldcretornica.plotme_core.commands.CmdProtect;
import com.worldcretornica.plotme_core.commands.CmdReload;
import com.worldcretornica.plotme_core.commands.CmdRemove;
import com.worldcretornica.plotme_core.commands.CmdReset;
import com.worldcretornica.plotme_core.commands.CmdResetExpired;
import com.worldcretornica.plotme_core.commands.CmdSell;
import com.worldcretornica.plotme_core.commands.CmdSetOwner;
import com.worldcretornica.plotme_core.commands.CmdShowHelp;
import com.worldcretornica.plotme_core.commands.CmdTP;
import com.worldcretornica.plotme_core.commands.CmdUndeny;
import com.worldcretornica.plotme_core.commands.CmdWEAnywhere;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PMCommand implements CommandExecutor {

    private PlotMe_Core plugin = null;
    private CmdAdd add;
    private CmdAddTime addtime;
    private CmdAuction auction;
    private CmdAuto auto;
    private CmdBid bid;
    private CmdBiome biome;
    private CmdBiomeList biomelist;
    private CmdBuy buy;
    private CmdClaim claim;
    private CmdClear clear;
    private CmdComment comment;
    private CmdComments comments;
    private CmdDeny deny;
    private CmdDispose dispose;
    private CmdDone done;
    private CmdDoneList donelist;
    private CmdExpired expired;
    private CmdHome home;
    private CmdID id;
    private CmdInfo info;
    private CmdMove move;
    private CmdPlotList plotlist;
    private CmdProtect protect;
    private CmdReload reload;
    private CmdRemove remove;
    private CmdReset reset;
    private CmdResetExpired resetexpired;
    private CmdSell sell;
    private CmdSetOwner setowner;
    private CmdShowHelp showhelp;
    private CmdTP tp;
    private CmdUndeny undeny;
    private CmdWEAnywhere weanywhere;
    private CmdCreateWorld createworld;

    public PMCommand(PlotMe_Core instance) {
        plugin = instance;
        add = new CmdAdd(plugin);
        addtime = new CmdAddTime(plugin);
        auction = new CmdAuction(plugin);
        auto = new CmdAuto(plugin);
        bid = new CmdBid(plugin);
        biome = new CmdBiome(plugin);
        biomelist = new CmdBiomeList(plugin);
        buy = new CmdBuy(plugin);
        claim = new CmdClaim(plugin);
        clear = new CmdClear(plugin);
        comment = new CmdComment(plugin);
        comments = new CmdComments(plugin);
        deny = new CmdDeny(plugin);
        dispose = new CmdDispose(plugin);
        done = new CmdDone(plugin);
        donelist = new CmdDoneList(plugin);
        expired = new CmdExpired(plugin);
        home = new CmdHome(plugin);
        id = new CmdID(plugin);
        info = new CmdInfo(plugin);
        move = new CmdMove(plugin);
        plotlist = new CmdPlotList(plugin);
        protect = new CmdProtect(plugin);
        reload = new CmdReload(plugin);
        remove = new CmdRemove(plugin);
        reset = new CmdReset(plugin);
        resetexpired = new CmdResetExpired(plugin);
        sell = new CmdSell(plugin);
        setowner = new CmdSetOwner(plugin);
        showhelp = new CmdShowHelp(plugin);
        tp = new CmdTP(plugin);
        undeny = new CmdUndeny(plugin);
        weanywhere = new CmdWEAnywhere(plugin);
        createworld = new CmdCreateWorld(plugin);
    }

    private String C(String caption) {
        return plugin.getUtil().C(caption);
    }

    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (l.equalsIgnoreCase("plotme") || l.equalsIgnoreCase("plot") || l.equalsIgnoreCase("p")) {
            if (!(s instanceof Player)) {
                if (args.length == 0 || args[0].equalsIgnoreCase("1")) {
                    s.sendMessage(C("ConsoleHelpMain"));
                    s.sendMessage(" - /plotme reload");
                    s.sendMessage(C("ConsoleHelpReload"));
                    return true;
                } else {
                    String a0 = args[0].toString();
                    if (!(s instanceof Player)) {
                        if (a0.equalsIgnoreCase("reload")) {
                            return reload.exec(s, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandResetExpired"))) {
                            return resetexpired.exec(s, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandCreateWorld"))) {
                            return createworld.exec(s, args);
                        }
                    }
                }
            } else {
                Player p = (Player) s;

                if (args.length == 0) {
                    return showhelp.exec(p, 1);
                } else {
                    String a0 = args[0].toString();
                    int ipage = -1;

                    try {
                        ipage = Integer.parseInt(a0);
                    } catch (Exception e) {
                    }

                    if (ipage != -1) {
                        return showhelp.exec(p, ipage);
                    } else {
                        if (a0.equalsIgnoreCase(C("CommandHelp"))) {
                            ipage = -1;

                            if (args.length > 1) {
                                String a1 = args[1].toString();
                                ipage = -1;

                                try {
                                    ipage = Integer.parseInt(a1);
                                } catch (Exception e) {
                                }
                            }

                            if (ipage != -1) {
                                return showhelp.exec(p, ipage);
                            } else {
                                return showhelp.exec(p, 1);
                            }
                        }
                        if (a0.equalsIgnoreCase(C("CommandClaim"))) {
                            return claim.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandAuto"))) {
                            return auto.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandInfo")) || a0.equalsIgnoreCase("i")) {
                            return info.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandComment"))) {
                            return comment.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandComments")) || a0.equalsIgnoreCase("c")) {
                            return comments.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandBiome")) || a0.equalsIgnoreCase("b")) {
                            return biome.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandBiomelist"))) {
                            return biomelist.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandId"))) {
                            return id.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandTp"))) {
                            return tp.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandClear"))) {
                            return clear.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandReset"))) {
                            return reset.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandAdd")) || a0.equalsIgnoreCase("+")) {
                            return add.exec(p, args);
                        }
                        if (plugin.getAllowToDeny()) {
                            if (a0.equalsIgnoreCase(C("CommandDeny"))) {
                                return deny.exec(p, args);
                            }
                            if (a0.equalsIgnoreCase(C("CommandUndeny"))) {
                                return undeny.exec(p, args);
                            }
                        }
                        if (a0.equalsIgnoreCase(C("CommandRemove")) || a0.equalsIgnoreCase("-")) {
                            return remove.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandSetowner")) || a0.equalsIgnoreCase("o")) {
                            return setowner.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandMove")) || a0.equalsIgnoreCase("m")) {
                            return move.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase("reload")) {
                            return reload.exec(s, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandWEAnywhere"))) {
                            return weanywhere.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandList"))) {
                            return plotlist.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandExpired"))) {
                            return expired.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandAddtime"))) {
                            return addtime.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandDone"))) {
                            return done.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandDoneList"))) {
                            return donelist.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandProtect"))) {
                            return protect.exec(p, args);
                        }

                        if (a0.equalsIgnoreCase(C("CommandSell"))) {
                            return sell.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandDispose"))) {
                            return dispose.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandAuction"))) {
                            return auction.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandBuy"))) {
                            return buy.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandBid"))) {
                            return bid.exec(p, args);
                        }
                        if (a0.startsWith(C("CommandHome")) || a0.startsWith("h")) {
                            return home.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandResetExpired"))) {
                            return resetexpired.exec(p, args);
                        }
                        if (a0.equalsIgnoreCase(C("CommandCreateWorld"))) {
                            return createworld.exec(p, args);
                        }
                    }
                }
            }
        }
        return false;
    }
}
