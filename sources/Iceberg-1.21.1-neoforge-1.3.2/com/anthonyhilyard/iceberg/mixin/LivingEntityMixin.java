package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.events.client.NewItemPickupEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public class LivingEntityMixin {
   @Inject(
      method = {"onItemPickup(Lnet/minecraft/world/entity/item/ItemEntity;)V"},
      at = {@At("HEAD")}
   )
   private void onItemPickup(ItemEntity itemEntity, CallbackInfo info) {
      if ((LivingEntity)this instanceof Player) {
         Player player = (Player)this;
         NewItemPickupEvent.EVENT.invoker().onItemPickup(player.getUUID(), itemEntity.getItem());
      }
   }
}
