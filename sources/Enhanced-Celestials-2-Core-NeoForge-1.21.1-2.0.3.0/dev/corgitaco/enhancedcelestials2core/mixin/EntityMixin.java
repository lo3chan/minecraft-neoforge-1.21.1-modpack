package dev.corgitaco.enhancedcelestials2core.mixin;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public class EntityMixin {
   @Inject(
      method = {"spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;"},
      at = {@At("HEAD")}
   )
   private void modifyDrops(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
      if (this instanceof LivingEntity entity) {
         Level world = entity.level();
         if (!world.isClientSide) {
            EnhancedCelestials.lunarForecastWorldData(world).ifPresent(data -> data.currentLunarEvent().onEntityItemDrop((ServerLevel)world, entity, stack));
         }
      }
   }
}
