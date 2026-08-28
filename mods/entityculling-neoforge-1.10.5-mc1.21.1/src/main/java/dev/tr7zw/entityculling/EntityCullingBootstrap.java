/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.loader.ModLoaderEventUtil
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.loading.FMLEnvironment
 */
package dev.tr7zw.entityculling;

import dev.tr7zw.entityculling.EntityCullingMod;
import dev.tr7zw.transition.loader.ModLoaderEventUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(value="entityculling")
public class EntityCullingBootstrap {
    public EntityCullingBootstrap() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoaderEventUtil.registerClientSetupListener(() -> new EntityCullingMod().onInitialize());
        }
    }
}

