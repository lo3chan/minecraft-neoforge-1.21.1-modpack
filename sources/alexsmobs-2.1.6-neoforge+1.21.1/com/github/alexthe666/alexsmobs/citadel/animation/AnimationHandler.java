package com.github.alexthe666.alexsmobs.citadel.animation;

import com.github.alexthe666.alexsmobs.citadel.Citadel;
import com.github.alexthe666.alexsmobs.citadel.server.message.AnimationMessage;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.ArrayUtils;

public enum AnimationHandler {
   INSTANCE;

   public <T extends Entity & IAnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
      if (!entity.level().isClientSide()) {
         entity.setAnimation(animation);
         Citadel.sendMSGToAll(new AnimationMessage(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation)));
      }
   }

   public <T extends Entity & IAnimatedEntity> void updateAnimations(T entity) {
      if (entity.getAnimation() == null) {
         entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
      } else if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
         if (entity.getAnimationTick() == 0) {
            AnimationEvent.Start event = new AnimationEvent.Start(entity, entity.getAnimation());
            if (!((AnimationEvent.Start)NeoForge.EVENT_BUS.post(event)).isCanceled()) {
               this.sendAnimationMessage(entity, event.getAnimation());
            }
         }

         if (entity.getAnimationTick() < entity.getAnimation().getDuration()) {
            entity.setAnimationTick(entity.getAnimationTick() + 1);
            NeoForge.EVENT_BUS.post(new AnimationEvent.Tick(entity, entity.getAnimation(), entity.getAnimationTick()));
         }

         if (entity.getAnimationTick() == entity.getAnimation().getDuration()) {
            entity.setAnimationTick(0);
            entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
         }
      }
   }
}
