/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.loader.ModLoaderEventUtil
 *  dev.tr7zw.transition.loader.ModLoaderUtil
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BannerBlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 */
package dev.tr7zw.entityculling;

import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.KeybindHolder;
import dev.tr7zw.entityculling.config.ConfigScreenProvider;
import dev.tr7zw.transition.loader.ModLoaderEventUtil;
import dev.tr7zw.transition.loader.ModLoaderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public class EntityCullingMod
extends EntityCullingModBase {
    public void onInitializeClient() {
        super.onInitialize();
    }

    @Override
    public void initModloader() {
        ModLoaderEventUtil.registerClientTickStartListener(this::clientTick);
        ModLoaderEventUtil.registerWorldTickStartListener(this::worldTick);
        KeybindHolder.INSTANCE.registerKeybinds();
        ModLoaderUtil.registerConfigScreen(ConfigScreenProvider::createConfigScreen);
        ModLoaderUtil.disableDisplayTest();
    }

    @Override
    public AABB setupAABB(BlockEntity entity, BlockPos pos) {
        if (entity instanceof BannerBlockEntity) {
            return new AABB(pos).inflate(0.0, 1.0, 0.0);
        }
        return new AABB(pos);
    }
}

