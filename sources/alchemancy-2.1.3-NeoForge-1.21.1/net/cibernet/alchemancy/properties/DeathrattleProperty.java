package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class DeathrattleProperty extends Property {
   @Override
   public void onUserDeath(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingDeathEvent event) {
      LivingEntity user = event.getEntity();
      float health = user.getHealth();
      activateByEntity(user, user, stack);
      user.setHealth(health);
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(2.0F, -7077887, -1365670);
   }
}
