package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class LightweightProperty extends Property {
   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity entity) {
      if (!entity.isNoGravity()) {
         entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.02, 0.0));
         entity.hasImpulse = true;
      }
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile entity) {
      if (!entity.isNoGravity()) {
         entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.02, 0.0));
         entity.hasImpulse = true;
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 13686501;
   }
}
