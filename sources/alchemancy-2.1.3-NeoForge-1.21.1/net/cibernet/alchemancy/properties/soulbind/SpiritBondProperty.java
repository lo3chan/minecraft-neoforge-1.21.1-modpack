package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class SpiritBondProperty extends Property {
   @Override
   public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, Pre event) {
      if (weapon.isDamageableItem()) {
         this.damageItem(user, weapon, slot, (int)Math.ceil(event.getNewDamage()));
         event.setNewDamage(event.getNewDamage() - 1.0F);
      }
   }

   @Override
   public void onHeal(LivingEntity user, ItemStack stack, EquipmentSlot slot, float amount) {
      if (canRepair(stack)) {
         repairItem(stack, (int)(amount * 5.0F));
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16748841;
   }
}
