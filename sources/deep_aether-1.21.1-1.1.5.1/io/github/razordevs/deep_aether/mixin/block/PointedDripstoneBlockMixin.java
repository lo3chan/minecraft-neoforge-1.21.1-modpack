package io.github.razordevs.deep_aether.mixin.block;

import com.aetherteam.aether.block.AetherBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.razordevs.deep_aether.init.DABlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock.FluidInfo;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PointedDripstoneBlock.class})
public abstract class PointedDripstoneBlockMixin {
   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
      )},
      method = {"maybeTransferFluid"}
   )
   private static void maybeTransferFluid(
      BlockState state,
      ServerLevel level,
      BlockPos pos,
      float p_221863_,
      CallbackInfo ci,
      @Local Optional<FluidInfo> optional,
      @Local Fluid fluid,
      @Local(ordinal = 1) BlockPos blockpos
   ) {
      if (optional.get().sourceState().is((Block)DABlocks.AETHER_MUD.get()) && fluid == Fluids.WATER) {
         BlockState blockstate1 = ((Block)AetherBlocks.QUICKSOIL.get()).defaultBlockState();
         level.setBlockAndUpdate(optional.get().pos(), blockstate1);
         Block.pushEntitiesUp(optional.get().sourceState(), blockstate1, level, optional.get().pos());
         level.gameEvent(GameEvent.BLOCK_CHANGE, optional.get().pos(), Context.of(blockstate1));
         level.levelEvent(1504, blockpos, 0);
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"getFluidAboveStalactite"},
      cancellable = true
   )
   private static void getFluidAboveStalactite(Level p_154182_, BlockPos p_154183_, BlockState p_154184_, CallbackInfoReturnable<Optional<FluidInfo>> cir) {
      Optional<FluidInfo> info = (Optional<FluidInfo>)cir.getReturnValue();
      if (info.isPresent() && info.get().sourceState().is(DABlocks.AETHER_MUD)) {
         cir.setReturnValue(Optional.of(new FluidInfo(info.get().pos(), Fluids.WATER, info.get().sourceState())));
      }
   }
}
