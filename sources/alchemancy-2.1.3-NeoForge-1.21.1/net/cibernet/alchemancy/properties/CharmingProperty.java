package net.cibernet.alchemancy.properties;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CharmingProperty extends Property {
   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      if (target instanceof Animal animal && animal.canFallInLove()) {
         animal.setInLove(user instanceof Player player ? player : null);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16748516;
   }
}
