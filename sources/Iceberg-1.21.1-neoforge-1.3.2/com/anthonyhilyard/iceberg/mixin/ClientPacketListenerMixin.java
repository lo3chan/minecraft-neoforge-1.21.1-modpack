package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.events.client.NewItemPickupEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ClientPacketListener.class})
public class ClientPacketListenerMixin {
   @Inject(
      method = {"handleTakeItemEntity(Lnet/minecraft/network/protocol/game/ClientboundTakeItemEntityPacket;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"
      )},
      locals = LocalCapture.CAPTURE_FAILEXCEPTION
   )
   private void handleTakeItemEntity(
      ClientboundTakeItemEntityPacket clientboundTakeItemEntityPacket, CallbackInfo info, Entity entity, LivingEntity livingEntity
   ) {
      if (livingEntity instanceof Player player) {
         ItemEntity itemEntity = (ItemEntity)entity;
         NewItemPickupEvent.EVENT.invoker().onItemPickup(player.getUUID(), itemEntity.getItem());
      }
   }
}
