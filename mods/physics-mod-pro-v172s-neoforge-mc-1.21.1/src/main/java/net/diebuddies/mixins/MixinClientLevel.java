/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LeavesBlock
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
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientLevel.class})
public class MixinClientLevel {
    @Inject(at={@At(value="HEAD")}, method={"addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"}, cancellable=true)
    public void addParticle(ParticleOptions particleOptions, double x, double y, double z, double vx, double vy, double vz, CallbackInfo info) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (ConfigClient.serverBlockPhysicsParticles && particleOptions instanceof BlockParticleOption) {
            BlockParticleOption blockParticle = (BlockParticleOption)particleOptions;
            if (camera.isInitialized() && camera.getPosition().distanceToSqr(x, y, z) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
                boolean isLeaf;
                BlockState block = blockParticle.getState();
                boolean isBarrier = block.getBlock() == Blocks.BARRIER;
                boolean bl = isLeaf = block.getBlock() instanceof LeavesBlock || block.is(BlockTags.LEAVES);
                if (isBarrier || isLeaf) {
                    return;
                }
                ParticleSpawner.spawnServerBlockPhysicsParticle(blockParticle.getState(), (Level)((ClientLevel)this), x + (double)(Math.random() * 0.1f) - (double)0.05f, y + (double)(Math.random() * 0.1f) - (double)0.05f, z + (double)(Math.random() * 0.1f) - (double)0.05f, vx, vy, vz);
                info.cancel();
            }
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"addEntity"})
    public void addEntity(Entity entity, CallbackInfo info) {
        if (entity instanceof FallingBlockEntity) {
            FallingBlockEntity fallingBlock = (FallingBlockEntity)entity;
            PhysicsMod mod = PhysicsMod.getInstance((Level)((ClientLevel)this));
            mod.fallingBlocks.add(fallingBlock.getStartPos());
        }
    }
}

