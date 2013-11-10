/*
 * Copyright (C) 2013 Fabrizio Lungo <fab@lungo.co.uk> - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fabrizio Lungo <fab@lungo.co.uk>, November 2013
 */
package com.worldcretornica.plotme_core;

import multiworld.ConfigException;
import multiworld.InvalidWorldGenException;
import multiworld.MultiWorldPlugin;
import multiworld.WorldGenException;
import multiworld.data.DataHandler;
import multiworld.worldgen.WorldGenerator;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Fabrizio Lungo <fab@lungo.co.uk>
 */
public class MultiWorldWrapper implements Delegate<MultiWorldPlugin> {

    private final MultiWorldPlugin multiworld;

    public MultiWorldWrapper(JavaPlugin multiworld) {
        if (!(multiworld instanceof MultiWorldPlugin)) {
            throw new IllegalArgumentException("JavaPlugin must be castable to MultiWorldPlugin");
        }
        this.multiworld = (MultiWorldPlugin) multiworld;
    }

    @Override
    public MultiWorldPlugin getDelegate() {
        return multiworld;
    }

    public final boolean isEnabled() {
        return multiworld.isEnabled();
    }

    public DataHandlerWrapper getDataManager() {
        return new DataHandlerWrapper(multiworld.getDataManager());
    }

    public static class DataHandlerWrapper implements Delegate<DataHandler> {

        private final DataHandler dataHandler;

        private DataHandlerWrapper(DataHandler dataHandler) {
            this.dataHandler = dataHandler;
        }

        @Override
        public DataHandler getDelegate() {
            return dataHandler;
        }

        public boolean makeWorld(String name, WorldGeneratorWrapper env, long seed, String options) throws DelegateClassException {
            try {
                return dataHandler.makeWorld(name, env.getDelegate(), seed, options);
            } catch (ConfigException ex) {
                throw new DelegateClassException(ex);
            } catch (WorldGenException ex) {
                throw new DelegateClassException(ex);
            }
        }

        public void save() throws DelegateClassException {
            try {
                dataHandler.save();
            } catch (ConfigException ex) {
                throw new DelegateClassException(ex);
            }
        }

        public World loadWorld(String name, boolean mustSave) throws DelegateClassException {
            try {
                return dataHandler.loadWorld(name, mustSave);
            } catch (ConfigException ex) {
                throw new DelegateClassException(ex);
            }
        }

    }

    public static class WorldGeneratorWrapper implements Delegate<WorldGenerator> {

        private final WorldGenerator worldGenerator;

        public WorldGeneratorWrapper(WorldGenerator worldGenerator) {
            this.worldGenerator = worldGenerator;
        }

        @Override
        public WorldGenerator getDelegate() {
            return worldGenerator;
        }

        public static WorldGeneratorWrapper getGenByName(String gen) throws DelegateClassException {
            try {
                return new WorldGeneratorWrapper(WorldGenerator.getGenByName(gen));
            } catch (InvalidWorldGenException ex) {
                throw new DelegateClassException(ex);
            }
        }

    }

}
