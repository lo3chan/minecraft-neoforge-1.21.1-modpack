package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.util.RedstoneSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({SignalGetter.class})
public interface SignalGetterMixin {
   @WrapOperation(
      method = {"getSignal"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;shouldCheckWeakPower(Lnet/minecraft/world/level/SignalGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
      )}
   )
   default boolean shouldCheckWeakPower(BlockState instance, SignalGetter signalGetter, BlockPos pos, Direction direction, Operation<Boolean> original) {
      return (Boolean)original.call(new Object[]{instance, signalGetter, pos, direction})
         || this instanceof ServerLevel serverLevel && RedstoneSources.getSourcePower(serverLevel, pos.relative(direction.getOpposite())) > 0;
   }
}
