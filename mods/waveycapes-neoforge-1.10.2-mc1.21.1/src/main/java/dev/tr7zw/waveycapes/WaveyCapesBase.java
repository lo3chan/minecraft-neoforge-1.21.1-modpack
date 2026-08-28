/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.world.entity.LivingEntity
 */
package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.delegate.PlayerDelegate;
import dev.tr7zw.waveycapes.render.CapeNodeCollector;
import dev.tr7zw.waveycapes.support.AnimationSupport;
import dev.tr7zw.waveycapes.support.ShoulderSurfingSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.nms.MinecraftPlayer;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import lombok.Generated;
import net.minecraft.world.entity.LivingEntity;

public abstract class WaveyCapesBase
extends ModBase {
    public static WaveyCapesBase INSTANCE;
    private final CapeNodeCollector capeNodeCollector = new CapeNodeCollector();

    @Override
    public void init() {
        INSTANCE = this;
        super.init();
        this.initSupportHooks();
    }

    @Override
    public Vector3 applyModAnimations(MinecraftPlayer player, Vector3 pos) {
        for (AnimationSupport sup : SupportManager.animationSupport) {
            pos = sup.applyAnimationChanges((LivingEntity)((PlayerDelegate)player).getPlayer(), 0.0f, pos);
        }
        return pos;
    }

    @Override
    public void initSupportHooks() {
        if (WaveyCapesBase.doesClassExist("com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer")) {
            ShoulderSurfingSupport.init();
            LOGGER.info("Wavey Capes loaded Shoulder Surfing support!");
        }
    }

    @Generated
    public static WaveyCapesBase getINSTANCE() {
        return INSTANCE;
    }

    @Generated
    public CapeNodeCollector getCapeNodeCollector() {
        return this.capeNodeCollector;
    }
}

