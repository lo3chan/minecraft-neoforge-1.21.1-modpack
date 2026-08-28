/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.snow;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.snow.SnowSearcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value={BlockOcclusionCache.class})
public class MixinBlockOcclusionCache {
    @Shadow(remap=false)
    @Final
    private BlockPos.MutableBlockPos cachedPositionObject;

    @Inject(at={@At(value="RETURN")}, method={"shouldDrawSide"}, remap=false, cancellable=true)
    public void shouldDrawSide(BlockState selfState, BlockGetter view, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValueZ() && ConfigClient.areSnowPhysicsEnabled()) {
            BlockPos.MutableBlockPos adjPos = this.cachedPositionObject;
            adjPos.set(pos.getX() + facing.getStepX(), pos.getY() + facing.getStepY(), pos.getZ() + facing.getStepZ());
            BlockState adjState = view.getBlockState((BlockPos)adjPos);
            if (SnowSearcher.getSnowProperty(adjState) != null) {
                info.setReturnValue((Object)true);
            }
        }
    }
}

