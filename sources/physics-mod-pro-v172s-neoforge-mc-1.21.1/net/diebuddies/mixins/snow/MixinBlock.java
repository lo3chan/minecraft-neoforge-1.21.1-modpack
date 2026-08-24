package net.diebuddies.mixins.snow;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.snow.SnowSearcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Block.class})
public class MixinBlock {
   @Inject(
      at = {@At("RETURN")},
      method = {"shouldRenderFace"},
      cancellable = true
   )
   private static void shouldRenderFace(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction, BlockPos blockPos2, CallbackInfoReturnable<Boolean> info
   ) {
      if (!info.getReturnValueZ() && ConfigClient.areSnowPhysicsEnabled() && SnowSearcher.getSnowProperty(blockGetter.getBlockState(blockPos2)) != null) {
         info.setReturnValue(true);
      }
   }
}
