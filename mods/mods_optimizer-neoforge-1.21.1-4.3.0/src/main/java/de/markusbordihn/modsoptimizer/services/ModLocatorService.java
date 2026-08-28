/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.Environment
 *  cpw.mods.modlauncher.Launcher
 *  cpw.mods.modlauncher.api.IEnvironment$Keys
 *  cpw.mods.modlauncher.api.TypesafeMap$Key
 *  net.neoforged.fml.loading.FMLPaths
 *  net.neoforged.neoforgespi.locating.IDependencyLocator
 *  net.neoforged.neoforgespi.locating.IDiscoveryPipeline
 *  net.neoforged.neoforgespi.locating.IModFile
 */
package de.markusbordihn.modsoptimizer.services;

import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.TypesafeMap;
import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.data.GameEnvironment;
import de.markusbordihn.modsoptimizer.service.ModsOptimizerService;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.locating.IDependencyLocator;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFile;

public class ModLocatorService
implements IDependencyLocator {
    public ModLocatorService() {
        Environment environment = Launcher.INSTANCE.environment();
        Optional launchTarget = environment.getProperty((TypesafeMap.Key)IEnvironment.Keys.LAUNCHTARGET.get());
        ModsOptimizerService modsOptimizer = new ModsOptimizerService(FMLPaths.GAMEDIR.get().toFile(), FMLPaths.MODSDIR.get().toFile(), launchTarget.isPresent() && ((String)launchTarget.get()).contains("server") ? GameEnvironment.SERVER : GameEnvironment.CLIENT).init();
        modsOptimizer.enableClientSideMods();
        modsOptimizer.parseMods();
        modsOptimizer.optimizeDuplicatedMods();
        modsOptimizer.disableClientSideMods();
        Constants.LOG.info("\u23f1 Mod Optimizer needs {} ms in total.", (Object)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - modsOptimizer.getTotalStartTime()));
    }

    public void scanMods(List<IModFile> list, IDiscoveryPipeline iDiscoveryPipeline) {
        Constants.LOG.debug("scanMods");
    }
}

