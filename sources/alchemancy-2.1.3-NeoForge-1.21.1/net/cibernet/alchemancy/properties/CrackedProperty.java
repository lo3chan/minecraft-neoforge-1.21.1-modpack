package net.cibernet.alchemancy.properties;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CrackedProperty extends Property {
   @Override
   public int modifyDurabilityConsumed(
      ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random
   ) {
      return resultingAmount * (random.nextFloat() < 0.4F ? 2 : 1);
   }

   @Override
   public void onActivation(@Nullable Entity user, Entity target, ItemStack weapon, DamageSource damageSource) {
      this.damageOrConsumeItem(target.level(), user, weapon, null, 1);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 8026746;
   }
}
