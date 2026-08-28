/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.LiquidBlockRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.ocean;

import net.diebuddies.config.ConfigClient;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LiquidBlockRenderer.class})
public class MixinLiquidBlockRenderer {
    @Inject(at={@At(value="HEAD")}, method={"isFaceOccludedByNeighbor"}, cancellable=true)
    private static void isFaceOccludedByNeighbor(BlockGetter blockGetter, BlockPos blockPos, Direction direction, float height, BlockState blockState, CallbackInfoReturnable<Boolean> info) {
        BlockState state;
        FluidState fluidState;
        if (ConfigClient.areOceanPhysicsEnabled() && direction == Direction.UP && (fluidState = (state = blockGetter.getBlockState(blockPos)).getFluidState()).is(FluidTags.WATER) && !state.blocksMotion()) {
            Vec3 flow = state.getFluidState().getFlow(blockGetter, blockPos);
            if (flow.x == 0.0 && flow.z == 0.0) {
                info.setReturnValue((Object)true);
            }
        }
    }
}

