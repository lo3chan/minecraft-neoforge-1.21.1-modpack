/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.loader.ModLoaderEventUtil
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.loading.FMLEnvironment
 */
package dev.tr7zw.waveycapes;

import dev.tr7zw.transition.loader.ModLoaderEventUtil;
import dev.tr7zw.waveycapes.WaveyCapesMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(value="waveycapes")
public class WaveyCapesBootstrap {
    public WaveyCapesBootstrap() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoaderEventUtil.registerClientSetupListener(() -> new WaveyCapesMod().init());
        }
    }
}

