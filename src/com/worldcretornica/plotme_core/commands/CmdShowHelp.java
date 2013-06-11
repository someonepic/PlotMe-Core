package com.worldcretornica.plotme_core.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.PlotMapInfo;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.Util;

public class CmdShowHelp extends PlotCommand 
{
	public boolean exec(Player p, int page)
	{
		int max = 4;
		int maxpage = 0;
		boolean ecoon = PlotMeCoreManager.isEconomyEnabled(p);
		
		List<String> allowed_commands = new ArrayList<String>();
		
		allowed_commands.add("limit");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.claim")) allowed_commands.add("claim");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.claim.other")) allowed_commands.add("claim.other");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.auto")) allowed_commands.add("auto");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.home")) allowed_commands.add("home");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.home.other")) allowed_commands.add("home.other");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.info"))
		{
			allowed_commands.add("info");
			allowed_commands.add("biomeinfo");
		}
		if(PlotMe_Core.cPerms(p, "PlotMe.use.comment")) allowed_commands.add("comment");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.comments")) allowed_commands.add("comments");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.list")) allowed_commands.add("list");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.biome"))
		{
			allowed_commands.add("biome");
			allowed_commands.add("biomelist");
		}
		if(PlotMe_Core.cPerms(p, "PlotMe.use.done") || 
				PlotMe_Core.cPerms(p, "PlotMe.admin.done")) allowed_commands.add("done");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.done")) allowed_commands.add("donelist");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.tp")) allowed_commands.add("tp");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.id")) allowed_commands.add("id");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.clear") || 
				PlotMe_Core.cPerms(p, "PlotMe.admin.clear")) allowed_commands.add("clear");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.dispose") || 
				PlotMe_Core.cPerms(p, "PlotMe.use.dispose")) allowed_commands.add("dispose");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.reset")) allowed_commands.add("reset");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.add") || 
				PlotMe_Core.cPerms(p, "PlotMe.admin.add")) allowed_commands.add("add");
		if(PlotMe_Core.cPerms(p, "PlotMe.use.remove") || 
				PlotMe_Core.cPerms(p, "PlotMe.admin.remove")) allowed_commands.add("remove");
		if(PlotMe_Core.allowToDeny)
		{
			if(PlotMe_Core.cPerms(p, "PlotMe.use.deny") || 
					PlotMe_Core.cPerms(p, "PlotMe.admin.deny")) allowed_commands.add("deny");
			if(PlotMe_Core.cPerms(p, "PlotMe.use.undeny") || 
					PlotMe_Core.cPerms(p, "PlotMe.admin.undeny")) allowed_commands.add("undeny");
		}
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.setowner")) allowed_commands.add("setowner");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.move")) allowed_commands.add("move");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.weanywhere")) allowed_commands.add("weanywhere");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.reload")) allowed_commands.add("reload");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.list")) allowed_commands.add("listother");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.expired")) allowed_commands.add("expired");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.addtime")) allowed_commands.add("addtime");
		if(PlotMe_Core.cPerms(p, "PlotMe.admin.resetexpired")) allowed_commands.add("resetexpired");
		
		PlotMapInfo pmi = PlotMeCoreManager.getMap(p);
		
		if(PlotMeCoreManager.isPlotWorld(p) && ecoon)
		{
			if(PlotMe_Core.cPerms(p, "PlotMe.use.buy")) allowed_commands.add("buy");
			if(PlotMe_Core.cPerms(p, "PlotMe.use.sell")) 
			{
				allowed_commands.add("sell");
				if(pmi.CanSellToBank)
				{
					allowed_commands.add("sellbank");
				}
			}
			if(PlotMe_Core.cPerms(p, "PlotMe.use.auction")) allowed_commands.add("auction");
			if(PlotMe_Core.cPerms(p, "PlotMe.use.bid")) allowed_commands.add("bid");
		}
		
		maxpage = (int) Math.ceil((double) allowed_commands.size() / max);
		
		if (page > maxpage)
			page = 1;
		
		p.sendMessage(RED + " ---==" + AQUA + Util.C("HelpTitle") + " " + page + "/" + maxpage + RED + "==--- ");
		
		for(int ctr = (page - 1) * max; ctr < (page * max) && ctr < allowed_commands.size(); ctr++)
		{
			String allowedcmd = allowed_commands.get(ctr);
			
			if(allowedcmd.equalsIgnoreCase("limit"))
			{
				if(PlotMeCoreManager.isPlotWorld(p) || PlotMe_Core.allowWorldTeleport)
				{
					World w = null;
					
					if(PlotMeCoreManager.isPlotWorld(p))
					{
						w = p.getWorld();
					}
					else if(PlotMe_Core.allowWorldTeleport)
					{
						w = PlotMeCoreManager.getFirstWorld();
					}

					int maxplots = PlotMe_Core.getPlotLimit(p);
					int ownedplots = PlotMeCoreManager.getNbOwnedPlot(p, w);
					
					if(maxplots == -1)
						p.sendMessage(GREEN + Util.C("HelpYourPlotLimitWorld") + " : " + AQUA + ownedplots + 
								GREEN + " " + Util.C("HelpUsedOf") + " " + AQUA + Util.C("WordInfinite"));
					else
						p.sendMessage(GREEN + Util.C("HelpYourPlotLimitWorld") + " : " + AQUA + ownedplots + 
								GREEN + " " + Util.C("HelpUsedOf") + " " + AQUA + maxplots);
				}
				else
				{
					p.sendMessage(GREEN + Util.C("HelpYourPlotLimitWorld") + " : " + AQUA + Util.C("MsgNotPlotWorld"));
				}
			}
			else if(allowedcmd.equalsIgnoreCase("claim"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandClaim"));
				if(ecoon && pmi != null && pmi.ClaimPrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpClaim") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.ClaimPrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpClaim"));
			}
			else if(allowedcmd.equalsIgnoreCase("claim.other"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandClaim") + " <" + Util.C("WordPlayer") + ">");
				if(ecoon && pmi != null && pmi.ClaimPrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpClaimOther") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.ClaimPrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpClaimOther"));
			}
			else if(allowedcmd.equalsIgnoreCase("auto"))
			{
				if(PlotMe_Core.allowWorldTeleport)
					p.sendMessage(GREEN + " /plotme " + Util.C("CommandAuto") + " [" + Util.C("WordWorld") + "]");
				else
					p.sendMessage(GREEN + " /plotme " + Util.C("CommandAuto"));
				
				if(ecoon && pmi != null && pmi.ClaimPrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpAuto") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.ClaimPrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpAuto"));
			}
			else if(allowedcmd.equalsIgnoreCase("home"))
			{
				if(PlotMe_Core.allowWorldTeleport)
					p.sendMessage(GREEN + " /plotme " + Util.C("CommandHome") + "[:#] [" + Util.C("WordWorld") + "]");
				else
					p.sendMessage(GREEN + " /plotme " + Util.C("CommandHome") + "[:#]");
				
				if(ecoon && pmi != null && pmi.PlotHomePrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpHome") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.PlotHomePrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpHome"));
			}
			else if(allowedcmd.equalsIgnoreCase("home.other"))
			{
				if(PlotMe_Core.allowWorldTeleport)
					p.sendMessage(GREEN + " /plotme " + Util.C("CommandHome") + "[:#] <" + Util.C("WordPlayer") + "> [" + Util.C("WordWorld") + "]");
				else
					p.sendMessage(GREEN + " /plotme " + Util.C("CommandHome") + "[:#] <" + Util.C("WordPlayer") + ">");
				
				if(ecoon && pmi != null && pmi.PlotHomePrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpHomeOther") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.PlotHomePrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpHomeOther"));
			}
			else if(allowedcmd.equalsIgnoreCase("info"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandInfo"));
				p.sendMessage(AQUA + " " + Util.C("HelpInfo"));
			}
			else if(allowedcmd.equalsIgnoreCase("comment"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandComment") + " <" + Util.C("WordComment") + ">");
				if(ecoon && pmi != null && pmi.AddCommentPrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpComment") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.AddCommentPrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpComment"));
			}
			else if(allowedcmd.equalsIgnoreCase("comments"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandComments"));
				p.sendMessage(AQUA + " " + Util.C("HelpComments"));
			}
			else if(allowedcmd.equalsIgnoreCase("list"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandList"));
				p.sendMessage(AQUA + " " + Util.C("HelpList"));
			}
			else if(allowedcmd.equalsIgnoreCase("listother"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandList") + " <" + Util.C("WordPlayer") + ">");
				p.sendMessage(AQUA + " " + Util.C("HelpListOther"));
			}
			else if(allowedcmd.equalsIgnoreCase("biomeinfo"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandBiome"));
				p.sendMessage(AQUA + " " + Util.C("HelpBiomeInfo"));
			}
			else if(allowedcmd.equalsIgnoreCase("biome"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandBiome") + " <" + Util.C("WordBiome") + ">");
				if(ecoon && pmi != null && pmi.BiomeChangePrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpBiome") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.BiomeChangePrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpBiome"));
			}
			else if(allowedcmd.equalsIgnoreCase("biomelist"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandBiomelist"));
				p.sendMessage(AQUA + " " + Util.C("HelpBiomeList"));
			}
			else if(allowedcmd.equalsIgnoreCase("done"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandDone"));
				p.sendMessage(AQUA + " " + Util.C("HelpDone"));
			}
			else if(allowedcmd.equalsIgnoreCase("tp"))
			{
				if(PlotMe_Core.allowWorldTeleport)
					p.sendMessage(GREEN + " /plotme " + Util.C("CommandTp") + " <" + Util.C("WordId") + "> [" + Util.C("WordWorld") + "]");
				else
					p.sendMessage(GREEN + " /plotme " + Util.C("CommandTp") + " <" + Util.C("WordId") + ">");
				
				p.sendMessage(AQUA + " " + Util.C("HelpTp"));
			}
			else if(allowedcmd.equalsIgnoreCase("id"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandId"));
				p.sendMessage(AQUA + " " + Util.C("HelpId"));
			}
			else if(allowedcmd.equalsIgnoreCase("clear"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandClear"));
				if(ecoon && pmi != null && pmi.ClearPrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpId") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.ClearPrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpClear"));
			}
			else if(allowedcmd.equalsIgnoreCase("reset"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandReset"));
				p.sendMessage(AQUA + " " + Util.C("HelpReset"));
			}
			else if(allowedcmd.equalsIgnoreCase("add"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandAdd") + " <" + Util.C("WordPlayer") + ">");
				if(ecoon && pmi != null && pmi.AddPlayerPrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpAdd") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.AddPlayerPrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpAdd"));
			}
			else if(allowedcmd.equalsIgnoreCase("deny"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandDeny") + " <" + Util.C("WordPlayer") + ">");
				if(ecoon && pmi != null && pmi.DenyPlayerPrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpDeny") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.DenyPlayerPrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpDeny"));
			}
			else if(allowedcmd.equalsIgnoreCase("remove")){
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandRemove") + " <" + Util.C("WordPlayer") + ">");
				if(ecoon && pmi != null && pmi.RemovePlayerPrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpRemove") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.RemovePlayerPrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpRemove"));
			}
			else if(allowedcmd.equalsIgnoreCase("undeny")){
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandUndeny") + " <" + Util.C("WordPlayer") + ">");
				if(ecoon && pmi != null && pmi.UndenyPlayerPrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpUndeny") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.UndenyPlayerPrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpUndeny"));
			}
			else if(allowedcmd.equalsIgnoreCase("setowner"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandSetowner") + " <" + Util.C("WordPlayer") + ">");
				p.sendMessage(AQUA + " " + Util.C("HelpSetowner"));
			}
			else if(allowedcmd.equalsIgnoreCase("move"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandMove") + " <" + Util.C("WordIdFrom") + "> <" + Util.C("WordIdTo") + ">");
				p.sendMessage(AQUA + " " + Util.C("HelpMove"));
			}
			else if(allowedcmd.equalsIgnoreCase("weanywhere"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandWEAnywhere"));
				p.sendMessage(AQUA + " " + Util.C("HelpWEAnywhere"));
			}
			else if(allowedcmd.equalsIgnoreCase("expired"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandExpired") + " [page]");
				p.sendMessage(AQUA + " " + Util.C("HelpExpired"));
			}
			else if(allowedcmd.equalsIgnoreCase("donelist"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandDoneList") + " [page]");
				p.sendMessage(AQUA + " " + Util.C("HelpDoneList"));
			}
			else if(allowedcmd.equalsIgnoreCase("addtime"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandAddtime"));
				int days = (pmi == null) ? 0 : pmi.DaysToExpiration;
				if(days == 0)
					p.sendMessage(AQUA + " " + Util.C("HelpAddTime1") + " " + RESET + Util.C("WordNever"));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpAddTime1") + " " + RESET + days + AQUA + " " + Util.C("HelpAddTime2"));
			}
			else if(allowedcmd.equalsIgnoreCase("reload"))
			{
				p.sendMessage(GREEN + " /plotme reload");
				p.sendMessage(AQUA + " " + Util.C("HelpReload"));
			}
			else if(allowedcmd.equalsIgnoreCase("dispose"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandDispose"));
				if(ecoon && pmi != null && pmi.DisposePrice != 0)
					p.sendMessage(AQUA + " " + Util.C("HelpDispose") + " " + Util.C("WordPrice") + " : " + RESET + Util.round(pmi.DisposePrice));
				else
					p.sendMessage(AQUA + " " + Util.C("HelpDispose"));
			}
			else if(allowedcmd.equalsIgnoreCase("buy"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandBuy"));
				p.sendMessage(AQUA + " " + Util.C("HelpBuy"));
			}
			else if(allowedcmd.equalsIgnoreCase("sell"))
			{				
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandSell") + " [" + Util.C("WordAmount") + "]");
				p.sendMessage(AQUA + " " + Util.C("HelpSell") + " " + Util.C("WordDefault") + " : " + RESET + Util.round(pmi.SellToPlayerPrice));
			}
			else if(allowedcmd.equalsIgnoreCase("sellbank"))
			{				
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandSellBank"));
				p.sendMessage(AQUA + " " + Util.C("HelpSellBank") + " " + RESET + Util.round(pmi.SellToBankPrice));
			}
			else if(allowedcmd.equalsIgnoreCase("auction"))
			{				
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandAuction") + " [" + Util.C("WordAmount") + "]");
				p.sendMessage(AQUA + " " + Util.C("HelpAuction") + " " + Util.C("WordDefault") + " : " + RESET + "1");
			}
			else if(allowedcmd.equalsIgnoreCase("resetexpired"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandResetExpired") + " <" + Util.C("WordWorld") + ">");
				p.sendMessage(AQUA + " " + Util.C("HelpResetExpired"));
			}
			else if(allowedcmd.equalsIgnoreCase("bid"))
			{
				p.sendMessage(GREEN + " /plotme " + Util.C("CommandBid") + " <" + Util.C("WordAmount") + ">");
				p.sendMessage(AQUA + " " + Util.C("HelpBid"));
			}
		}
		
		return true;
	}
}
