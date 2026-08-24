package net.bettercombat.mixin;

import net.bettercombat.mixin.player.PlayerEntityAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ServerGamePacketListenerImpl.class})
public class ServerPlayNetworkHandlerMixin {
   @Redirect(
      method = {"handlePlayerAction(Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"
      )
   )
   public ItemStack getStackInHand(ServerPlayer instance, InteractionHand hand) {
      ItemStack result = null;
      switch (hand) {
         case MAIN_HAND:
            result = ((PlayerEntityAccessor)instance).getInventory().getSelected();
            break;
         case OFF_HAND:
            result = (ItemStack)((PlayerEntityAccessor)instance).getInventory().offhand.get(0);
      }

      return result;
   }
}
