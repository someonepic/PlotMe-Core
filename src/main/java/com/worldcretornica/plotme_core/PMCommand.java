package com.worldcretornica.plotme_core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.commands.*;
import com.worldcretornica.plotme_core.utils.Util;

public class PMCommand implements CommandExecutor
{
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
	
	public PMCommand()
	{
		add = new CmdAdd();
		addtime = new CmdAddTime();
		auction = new CmdAuction();
		auto = new CmdAuto();
		bid = new CmdBid();
		biome = new CmdBiome();
		biomelist = new CmdBiomeList();
		buy = new CmdBuy();
		claim = new CmdClaim();
		clear = new CmdClear();
		comment = new CmdComment();
		comments = new CmdComments();
		deny = new CmdDeny();
		dispose = new CmdDispose();
		done = new CmdDone();
		donelist = new CmdDoneList();
		expired = new CmdExpired();
		home = new CmdHome();
		id = new CmdID();
		info = new CmdInfo();
		move = new CmdMove();
		plotlist = new CmdPlotList();
		protect = new CmdProtect();
		reload = new CmdReload();
		remove = new CmdRemove();
		reset = new CmdReset();
		resetexpired = new CmdResetExpired();
		sell = new CmdSell();
		setowner = new CmdSetOwner();
		showhelp = new CmdShowHelp();
		tp = new CmdTP();
		undeny = new CmdUndeny();
		weanywhere = new CmdWEAnywhere();
		createworld = new CmdCreateWorld();
	}
	
	private String C(String caption)
	{
		return Util.C(caption);
	}
	
	public boolean onCommand(CommandSender s, Command c, String l, String[] args)
	{
		if(l.equalsIgnoreCase("plotme") || l.equalsIgnoreCase("plot") || l.equalsIgnoreCase("p"))
		{
			if(!(s instanceof Player))
			{
				if(args.length == 0 || args[0].equalsIgnoreCase("1"))
				{
					s.sendMessage(C("ConsoleHelpMain")); 
					s.sendMessage(" - /plotme reload");
					s.sendMessage(C("ConsoleHelpReload"));
					return true;
				}
				else
				{
					String a0 = args[0].toString();
					if(!(s instanceof Player))
					{
						if (a0.equalsIgnoreCase("reload")) { return reload.exec(s, args);}
						if (a0.equalsIgnoreCase(C("CommandResetExpired"))) { return resetexpired.exec(s, args); }
						if (a0.equalsIgnoreCase(C("CommandCreateWorld"))) { return createworld.exec(s, args); }
					}
				}
			}
			else
			{
				Player p = (Player)s;
				
				if(args.length == 0)
				{
					return showhelp.exec(p, 1);
				}
				else
				{
					String a0 = args[0].toString();
					int ipage = -1;
					
					try  
					{  
						ipage = Integer.parseInt( a0 );  
					}  
					catch( Exception e) {}
									
					if(ipage != -1)
					{
						return showhelp.exec(p, ipage);
					}
					else
					{
						if (a0.equalsIgnoreCase(C("CommandHelp")))
						{
							ipage = -1;
							
							if(args.length > 1)
							{
								String a1 = args[1].toString();
								ipage = -1;
								
								try  
								{  
									ipage = Integer.parseInt( a1 );  
								}  
								catch( Exception e) {}
							}
							
							if(ipage != -1)
							{
								return showhelp.exec(p, ipage);
							}
							else
							{
								return showhelp.exec(p, 1);
							}
						}
						if (a0.equalsIgnoreCase(C("CommandClaim"))) { return claim.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandAuto"))) { return auto.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandInfo")) || a0.equalsIgnoreCase("i")) { return info.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandComment"))) { return comment.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandComments")) || a0.equalsIgnoreCase("c")) { return comments.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandBiome")) || a0.equalsIgnoreCase("b")) { return biome.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandBiomelist"))) { return biomelist.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandId"))) { return id.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandTp"))) { return tp.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandClear"))) { return clear.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandReset"))) { return reset.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandAdd")) || a0.equalsIgnoreCase("+")) { return add.exec(p, args);}
						if(PlotMe_Core.allowToDeny)
						{
							if (a0.equalsIgnoreCase(C("CommandDeny"))) { return deny.exec(p, args);}
							if (a0.equalsIgnoreCase(C("CommandUndeny"))) { return undeny.exec(p, args);}
						}
						if (a0.equalsIgnoreCase(C("CommandRemove")) || a0.equalsIgnoreCase("-")) { return remove.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandSetowner")) || a0.equalsIgnoreCase("o")) { return setowner.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandMove")) || a0.equalsIgnoreCase("m")) { return move.exec(p, args);}
						if (a0.equalsIgnoreCase("reload")) { return reload.exec(s, args);}
						if (a0.equalsIgnoreCase(C("CommandWEAnywhere"))) { return weanywhere.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandList"))) { return plotlist.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandExpired"))) { return expired.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandAddtime"))) { return addtime.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandDone"))) { return done.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandDoneList"))) { return donelist.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandProtect"))) { return protect.exec(p, args);}
						
						if (a0.equalsIgnoreCase(C("CommandSell"))) { return sell.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandDispose"))) { return dispose.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandAuction"))) { return auction.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandBuy"))) { return buy.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandBid"))) { return bid.exec(p, args);}
						if (a0.startsWith(C("CommandHome")) || a0.startsWith("h")) { return home.exec(p, args);}
						if (a0.equalsIgnoreCase(C("CommandResetExpired"))) { return resetexpired.exec(p, args); }
						if (a0.equalsIgnoreCase(C("CommandCreateWorld"))) { return createworld.exec(p, args); }
					}
				}
			}
		}
		return false;
	}
}
