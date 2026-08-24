package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.util.RedstoneSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({BlockStateBase.class})
public class BlockStateBaseMixin {
   @WrapOperation(
      method = {"getSignal"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/Block;getSignal(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I"
      )}
   )
   public int getSignal(Block instance, BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction, Operation<Integer> original) {
      return blockGetter instanceof ServerLevel level
         ? Math.max((Integer)original.call(new Object[]{instance, state, blockGetter, pos, direction}), RedstoneSources.getSourcePower(level, pos))
         : (Integer)original.call(new Object[]{instance, state, blockGetter, pos, direction});
   }

   @WrapOperation(
      method = {"getDirectSignal"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/Block;getDirectSignal(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I"
      )}
   )
   public int getDirectSignal(Block instance, BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction, Operation<Integer> original) {
      if (blockGetter instanceof ServerLevel level) {
         RedstoneSources.RedstoneSource source = RedstoneSources.getSourceAt(level, pos);
         if (source.direction == direction) {
            return Math.max((Integer)original.call(new Object[]{instance, state, blockGetter, pos, direction}), source.power);
         }
      }

      return (Integer)original.call(new Object[]{instance, state, blockGetter, pos, direction});
   }
}
