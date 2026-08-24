package com.aetherteam.aether.item.combat.abilities.armor;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface GravititeArmor {
   static void boostedJump(LivingEntity entity) {
      if (EquipmentUtil.hasFullGravititeSet(entity)) {
         if (entity instanceof Player player) {
            if (player.onGround() && ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).isGravititeJumpActive()) {
               player.push(0.0, 1.0, 0.0);
               if (player instanceof ServerPlayer serverPlayer) {
                  serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
               }
            }
         } else {
            entity.push(0.0, 1.0, 0.0);
         }
      }
   }
}
