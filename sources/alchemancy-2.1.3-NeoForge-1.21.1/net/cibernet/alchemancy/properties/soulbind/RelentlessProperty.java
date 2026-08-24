package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import org.jetbrains.annotations.Nullable;

public class RelentlessProperty extends Property {
   @Override
   public int getColor(ItemStack stack) {
      return 19711;
   }

   @Override
   public int modifyDurabilityConsumed(
      ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random
   ) {
      return user != null && random.nextFloat() <= 1.0F / Math.max(0.6F, getEffectScale(user)) * 0.6F ? 0 : resultingAmount;
   }

   @Override
   public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, Pre event) {
      float newDamage = Mth.ceil(Math.max(event.getOriginalDamage() * 0.5F, event.getNewDamage() * (1.0F - getEffectScale(user) * 0.2F)));
      if (event.getNewDamage() > newDamage) {
         event.setNewDamage(newDamage);
      }
   }

   public static float getEffectScale(LivingEntity user) {
      return 1.0F - user.getHealth() / user.getMaxHealth();
   }
}
