/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.loader.ModLoaderEventUtil
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.loading.FMLEnvironment
 */
package dev.tr7zw.notenoughanimations;

import dev.tr7zw.notenoughanimations.NEAnimationsMod;
import dev.tr7zw.transition.loader.ModLoaderEventUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(value="notenoughanimations")
public class NEABootstrap {
    public NEABootstrap() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoaderEventUtil.registerClientSetupListener(() -> new NEAnimationsMod().onInitializeClient());
        }
    }
}

