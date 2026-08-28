/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.ocean;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ocean.OceanWorld;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
    @Shadow
    private Level level;

    @Inject(at={@At(value="HEAD")}, method={"render"})
    public <E extends Entity> void renderStart(E entity, double x, double y, double z, float yRot, float renderPercent, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo info) {
        if (ConfigClient.areOceanPhysicsEnabled() && this.level instanceof ClientLevel) {
            poseStack.pushPose();
            OceanWorld oceanWorld = PhysicsMod.getInstance(this.level).getPhysicsWorld().getOceanWorld();
            oceanWorld.computeEntityOffset(poseStack.last().pose(), poseStack.last().normal(), this.level, entity, x, y, z, 0.0, 0.0, 0.0, yRot, renderPercent);
            if (ConfigClient.oceanRipples) {
                // empty if block
            }
        }
    }

    @Inject(at={@At(value="RETURN")}, method={"render"})
    public <E extends Entity> void renderEnd(E entity, double x, double y, double z, float yRot, float renderPercent, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo info) {
        if (ConfigClient.areOceanPhysicsEnabled() && this.level instanceof ClientLevel) {
            poseStack.popPose();
        }
    }
}

