package net.Pandarix.mixin;

import net.Pandarix.BACommon;
import net.Pandarix.item.BetterBrushItem;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BrushableBlockEntity.class})
public abstract class FasterBrushingMixin {
   @Inject(
      method = {"brush(JLnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/Direction;)Z"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/entity/BrushableBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"
      )}
   )
   private void injectMethod(long worldTime, Player player, Direction hitDirection, CallbackInfoReturnable<Boolean> cir) {
      try {
         if (this instanceof BrushableBlockEntity ba$brushableBlockEntity && player.getUseItem().getItem() instanceof BetterBrushItem ba$brushItem) {
            ba$brushableBlockEntity.coolDownEndsAtTick = ba$brushableBlockEntity.coolDownEndsAtTick + (-10 + ba$brushItem.getBrushingSpeed());
         }
      } catch (Exception var9) {
         BACommon.LOGGER.info("Could not apply faster brushing due to error: " + var9);
      }
   }
}
