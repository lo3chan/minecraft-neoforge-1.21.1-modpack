package com.aetherteam.aether.item.accessories.abilities;

import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public interface SlowFallAccessory {
   default void handleSlowFall(LivingEntity livingEntity) {
      AttributeInstance gravity = livingEntity.getAttribute(Attributes.GRAVITY);
      if (gravity != null
         && livingEntity.getDeltaMovement().y() <= -0.06
         && !livingEntity.onGround()
         && !livingEntity.isFallFlying()
         && !livingEntity.isInFluidType()
         && !livingEntity.isShiftKeyDown()
         && gravity.getValue() > 0.0075) {
         livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(1.0, 0.6, 1.0));
      }

      livingEntity.checkSlowFallDistance();
      if (livingEntity instanceof ServerPlayer serverPlayer) {
         ServerGamePacketListenerImplAccessor serverGamePacketListenerImplAccessor = (ServerGamePacketListenerImplAccessor)serverPlayer.connection;
         serverGamePacketListenerImplAccessor.aether$setAboveGroundTickCount(0);
      }
   }
}
