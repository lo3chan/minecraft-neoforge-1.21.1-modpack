package com.github.alexthe666.alexsmobs.citadel;

import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.citadel.server.entity.CitadelEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class CitadelClientProxy extends CitadelProxy {
   @Override
   public void handleAnimationPacket(int entityId, int index) {
      if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getEntity(entityId) instanceof IAnimatedEntity animated) {
         if (index == -1) {
            animated.setAnimation(IAnimatedEntity.NO_ANIMATION);
         } else {
            animated.setAnimation(animated.getAnimations()[index]);
         }

         animated.setAnimationTick(0);
      }
   }

   @Override
   public void handlePropertiesPacket(String propertyID, CompoundTag compound, int entityID) {
      if (compound != null && Minecraft.getInstance().level != null) {
         if ((propertyID.equals("CitadelPatreonConfig") || propertyID.equals("CitadelTagUpdate"))
            && Minecraft.getInstance().level.getEntity(entityID) instanceof LivingEntity living) {
            CitadelEntityData.setCitadelTag(living, compound);
         }
      }
   }
}
