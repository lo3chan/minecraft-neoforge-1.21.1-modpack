/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.loading.FMLConfig
 *  net.neoforged.fml.loading.FMLConfig$ConfigValue
 *  net.neoforged.neoforgespi.earlywindow.GraphicsBootstrapper
 */
package net.caffeinemc.mods.sodium.service;

import net.caffeinemc.mods.sodium.client.compatibility.checks.PreLaunchChecks;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterProbe;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.Workarounds;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.amd.AmdWorkarounds;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.nvidia.NvidiaWorkarounds;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.neoforgespi.earlywindow.GraphicsBootstrapper;

public class SodiumWorkarounds
implements GraphicsBootstrapper {
    public String name() {
        return "sodium";
    }

    public void bootstrap(String[] arguments) {
        PreLaunchChecks.checkEnvironment();
        GraphicsAdapterProbe.findAdapters();
        Workarounds.init();
        if (FMLConfig.getBoolConfigValue((FMLConfig.ConfigValue)FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL)) {
            NvidiaWorkarounds.applyEnvironmentChanges();
            AmdWorkarounds.applyEnvironmentChanges();
        }
    }
}

