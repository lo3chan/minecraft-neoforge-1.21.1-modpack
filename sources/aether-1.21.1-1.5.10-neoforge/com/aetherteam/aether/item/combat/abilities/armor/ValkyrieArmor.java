package com.aetherteam.aether.item.combat.abilities.armor;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface ValkyrieArmor {
   static void handleFlight(LivingEntity entity) {
      if (EquipmentUtil.hasFullValkyrieSet(entity) && entity instanceof Player player && !player.getAbilities().flying) {
         AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         Vec3 deltaMovement = player.getDeltaMovement();
         if (data.isJumping() && !onGround(player)) {
            if (data.getFlightModifier() >= data.getFlightModifierMax()) {
               data.setFlightModifier(data.getFlightModifierMax());
            }

            if (data.getFlightTimer() > 2) {
               if (data.getFlightTimer() < data.getFlightTimerMax()) {
                  data.setFlightModifier(data.getFlightModifier() + 0.25F);
                  data.setFlightTimer(data.getFlightTimer() + 1);
               }
            } else {
               data.setFlightTimer(data.getFlightTimer() + 1);
            }
         } else if (!data.isJumping()) {
            data.setFlightModifier(1.0F);
         }

         if (onGround(player)) {
            data.setFlightTimer(0);
            data.setFlightModifier(1.0F);
         }

         if (data.isJumping()
            && !onGround(player)
            && data.getFlightTimer() > 2
            && data.getFlightTimer() < data.getFlightTimerMax()
            && data.getFlightModifier() > 1.0F) {
            player.setDeltaMovement(deltaMovement.x(), 0.025F * data.getFlightModifier(), deltaMovement.z());
         }

         if (player instanceof ServerPlayer serverPlayer) {
            ServerGamePacketListenerImplAccessor serverGamePacketListenerImplAccessor = (ServerGamePacketListenerImplAccessor)serverPlayer.connection;
            serverGamePacketListenerImplAccessor.aether$setAboveGroundTickCount(0);
         }
      }
   }

   private static boolean onGround(Player player) {
      return player.onGround() || player.isInFluidType();
   }
}
