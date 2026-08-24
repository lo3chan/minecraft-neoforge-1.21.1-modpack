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

@Mixin({LiquidBlockRenderer.class})
public class MixinLiquidBlockRenderer {
   @Inject(
      at = {@At("HEAD")},
      method = {"isFaceOccludedByNeighbor"},
      cancellable = true
   )
   private static void isFaceOccludedByNeighbor(
      BlockGetter blockGetter, BlockPos blockPos, Direction direction, float height, BlockState blockState, CallbackInfoReturnable<Boolean> info
   ) {
      if (ConfigClient.areOceanPhysicsEnabled() && direction == Direction.UP) {
         BlockState state = blockGetter.getBlockState(blockPos);
         FluidState fluidState = state.getFluidState();
         if (fluidState.is(FluidTags.WATER) && !state.blocksMotion()) {
            Vec3 flow = state.getFluidState().getFlow(blockGetter, blockPos);
            if (flow.x == 0.0 && flow.z == 0.0) {
               info.setReturnValue(true);
            }
         }
      }
   }
}
