/*
 * Copyright (C) 2013
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.worldcretornica.plotme_core.listener;

import com.worldcretornica.plotme_core.PlotMe_Core;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Fabrizio Lungo <fab@lungo.co.uk>
 */
public class PlayerListener implements Listener {

    private final PlotMe_Core plugin;

    public PlayerListener(PlotMe_Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (!p.isOp()) {
            return;
        }
        Set<String> badWorlds = plugin.getBadWorlds();
        for (String world : badWorlds) {
            if (plugin.getGenManager(world) == null) {
                // TODO: Add as multilingual caption
                plugin.sendMessage(p, "The world " + ChatColor.GOLD + world + ChatColor.RESET + " is defined as a plotworld but does not exist or is not using a PlotMe generator.");
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerTeleportEvent event) {
        World toWorld = event.getTo().getWorld();
        if (toWorld.equals(event.getFrom().getWorld())) {
            // Did not change world
            return;
        }
        Player p = event.getPlayer();
        if (p.isOp() && plugin.getBadWorlds().contains(toWorld.getName())) {
            if (plugin.getGenManager(toWorld) == null) {
                // TODO: Add as multilingual caption
                plugin.sendMessage(p, "This world is defined as a plotworld but is not using a PlotMe generator.");
            }
        }
    }
}
