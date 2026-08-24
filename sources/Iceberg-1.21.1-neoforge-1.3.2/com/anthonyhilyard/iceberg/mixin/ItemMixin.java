package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.util.EntityCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {Item.class},
   priority = 100
)
public class ItemMixin {
   @Inject(
      method = {"getPlayerPOVHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/ClipContext$Fluid;)Lnet/minecraft/world/phys/BlockHitResult;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void icebergGetPlayerPOVHitResult(Level level, Player player, Fluid clipContext, CallbackInfoReturnable<HitResult> info) {
      if (level instanceof EntityCollector) {
         info.setReturnValue(new BlockHitResult(Vec3.ZERO, Direction.DOWN, BlockPos.ZERO, false));
      }
   }
}
