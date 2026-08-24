package dev.tr7zw.notenoughanimations.access;

import net.minecraft.world.entity.LivingEntity;

public interface ExtendedLivingRenderState {
   void setEntity(LivingEntity var1);

   LivingEntity getEntity();
}
