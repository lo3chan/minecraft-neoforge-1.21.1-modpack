/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.loader.ModLoaderUtil
 */
package dev.tr7zw.waveycapes;

import dev.tr7zw.transition.loader.ModLoaderUtil;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.WaveyCapesConfigScreen;
import dev.tr7zw.waveycapes.support.EarsSupport;
import dev.tr7zw.waveycapes.support.MinecraftCapesSupport;
import dev.tr7zw.waveycapes.support.SupportManager;

public class WaveyCapesMod
extends WaveyCapesBase {
    @Override
    public void initSupportHooks() {
        super.initSupportHooks();
        if (WaveyCapesMod.doesClassExist("net.minecraftcapes.MinecraftCapes")) {
            SupportManager.mods.add(new MinecraftCapesSupport());
            LOGGER.info("Wavey Capes loaded MinecraftCapes support!");
        }
        if (WaveyCapesMod.doesClassExist("com.unascribed.ears.common.EarsVersion")) {
            SupportManager.mods.add(new EarsSupport());
            LOGGER.info("Wavey Capes loaded Ears support!");
        }
    }

    @Override
    public void init() {
        super.init();
        ModLoaderUtil.disableDisplayTest();
        ModLoaderUtil.registerConfigScreen(WaveyCapesConfigScreen::createConfigScreen);
    }
}

