package net.Pandarix.mixin;

import net.Pandarix.item.BetterBrushItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({BrushItem.class})
public class BrushItemMixin {
   @Redirect(
      method = {"onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/BrushItem;getUseDuration(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)I"
      )
   )
   public int inject(
      BrushItem instance,
      ItemStack itemStack,
      LivingEntity livingEntity,
      Level pLevel,
      LivingEntity pLivingEntity,
      ItemStack pItemStack,
      int pRemainingUseDuration
   ) {
      if (this instanceof BetterBrushItem ba$betterBrushItem) {
         return pRemainingUseDuration % ba$betterBrushItem.getBrushingSpeed() == 0 ? 4 + pRemainingUseDuration : pRemainingUseDuration;
      } else {
         return instance.getUseDuration(itemStack, livingEntity);
      }
   }
}
