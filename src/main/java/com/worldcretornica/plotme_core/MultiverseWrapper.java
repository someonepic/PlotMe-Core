/*
 * Copyright (C) 2013 Fabrizio Lungo <fab@lungo.co.uk> - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fabrizio Lungo <fab@lungo.co.uk>, November 2013
 */
package com.worldcretornica.plotme_core;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Fabrizio Lungo <fab@lungo.co.uk>
 */
public class MultiverseWrapper implements Delegate<MultiverseCore> {

    private final MultiverseCore multiverseCore;

    public MultiverseWrapper(JavaPlugin multiverseCore) {
        if (!(multiverseCore instanceof MultiverseCore)) {
            throw new IllegalArgumentException("JavaPlugin must be castable to MultiverseCore");
        }
        this.multiverseCore = (MultiverseCore) multiverseCore;
    }

    @Override
    public MultiverseCore getDelegate() {
        return multiverseCore;
    }

    public final boolean isEnabled() {
        return multiverseCore.isEnabled();
    }

    public MVWorldManagerWrapper getMVWorldManager() {
        return new MVWorldManagerWrapper(multiverseCore.getMVWorldManager());
    }

    public static class MVWorldManagerWrapper implements Delegate<MVWorldManager> {

        private final MVWorldManager worldManager;

        public MVWorldManagerWrapper(MVWorldManager worldManager) {
            this.worldManager = worldManager;
        }

        @Override
        public MVWorldManager getDelegate() {
            return worldManager;
        }

        public boolean addWorld(String string, World.Environment e, String string1, WorldType wt, Boolean bln, String string2) {
            return worldManager.addWorld(string, e, string1, wt, bln, string2);
        }

    }

}
