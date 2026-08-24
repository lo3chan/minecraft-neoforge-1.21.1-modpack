package com.aetherteam.aether.item.combat.abilities.weapon;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public interface GravititeWeapon {
   default void launchEntity(LivingEntity target, LivingEntity attacker) {
      if (EquipmentUtil.isFullStrength(attacker) && !target.getType().is(AetherTags.Entities.UNLAUNCHABLE) && (target.onGround() || target.isInFluidType())) {
         target.push(0.0, 1.0, 0.0);
         if (target instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
         }
      }
   }
}
