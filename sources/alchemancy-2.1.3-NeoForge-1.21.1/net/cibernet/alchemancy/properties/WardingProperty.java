package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class WardingProperty extends Property {
   @Override
   public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, Pre event) {
      if (slot.isArmor() || user.getUseItem() == weapon) {
         event.setNewDamage(event.getNewDamage() * 0.85F);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 9922967;
   }
}
