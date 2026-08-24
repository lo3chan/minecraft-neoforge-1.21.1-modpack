package net.cibernet.alchemancy.properties;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.jetbrains.annotations.Nullable;

public class SpikingProperty extends Property {
   @Override
   public float modifyStepOnFriction(Entity user, ItemStack stack, float originalResult, float result) {
      return 0.55F;
   }

   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      target.setDeltaMovement(0.0, -2.0, 0.0);
      target.fallDistance += 5.0F;
   }

   @Override
   public void modifyKnockBackApplied(LivingEntity user, ItemStack weapon, LivingEntity target, LivingKnockBackEvent event) {
      target.setDeltaMovement(0.0, -2.0, 0.0);
      event.setCanceled(true);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 9666431;
   }
}
