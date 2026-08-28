/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public class MixinEntity {
    @Inject(at={@At(value="HEAD")}, method={"spawnSprintParticle"}, cancellable=true)
    protected void spawnSprintParticle(CallbackInfo info) {
        Camera camera;
        Entity entity = (Entity)this;
        if (ConfigClient.sprintingPhysicsParticles && entity.level() instanceof ClientLevel && (camera = Minecraft.getInstance().gameRenderer.getMainCamera()).isInitialized() && camera.getPosition().distanceToSqr(entity.getX(), entity.getY(), entity.getZ()) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
            BlockPos blockPos = new BlockPos(Mth.floor((double)entity.getX()), Mth.floor((double)(entity.getY() - (double)0.2f)), Mth.floor((double)entity.getZ()));
            BlockState blockState = entity.level().getBlockState(blockPos);
            if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
                try {
                    EntityDimensions dimensions = entity.getDimensions(entity.getPose());
                    ParticleSpawner.spawnSprintingPhysicsParticle(blockState, blockPos, entity.level(), entity.getX() + ((double)Math.random() - 0.5) * (double)dimensions.width(), entity.getY() + 0.1, entity.getZ() + ((double)Math.random() - 0.5) * (double)dimensions.width());
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            info.cancel();
        }
    }
}

