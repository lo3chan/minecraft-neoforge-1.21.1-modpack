/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.BlockUpdate;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ocean.BlockToByte;
import net.diebuddies.physics.ocean.OceanBlockUpdate;
import net.diebuddies.physics.snow.SnowSearcher;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LevelChunk.class})
public class MixinLevelChunk {
    @Shadow
    private Level level;

    @Inject(at={@At(value="HEAD")}, method={"setBlockState"})
    private void setBlockState(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> ci) {
        if (isMoving) {
            return;
        }
        if (RenderSystem.isOnRenderThread() && this.level instanceof ClientLevel) {
            BlockState before = this.level.getBlockState(pos);
            PhysicsMod mod = PhysicsMod.getInstance(this.level);
            mod.blockUpdates.add(pos.immutable());
            if (before != null && state != null && (before.isAir() && !state.isAir() || state.isAir() && !before.isAir() || !before.isAir() && state.getBlock() == Blocks.WATER)) {
                BlockUpdate update = new BlockUpdate(this.level, pos.immutable(), before);
                if (before.hasBlockEntity()) {
                    update.blockEntity = this.level.getBlockEntity(pos);
                }
                mod.updateQueue.add(update);
            }
            if (ConfigClient.areSnowPhysicsEnabled() && before != null && state != null && (SnowSearcher.getSnowProperty(state) != null || SnowSearcher.getSnowProperty(before) != null)) {
                mod.physicsWorld.getSnowWorld().blockUpdates.add(new BlockUpdate(this.level, pos.immutable(), state));
            }
            if (ConfigClient.areOceanPhysicsEnabled() && before != null && state != null) {
                mod.physicsWorld.getOceanWorld().getBlockUpdates().add(new OceanBlockUpdate(this.level, pos.immutable(), BlockToByte.convert(state)));
            }
        }
    }
}

