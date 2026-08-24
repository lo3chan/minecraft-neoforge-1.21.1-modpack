package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class ArmoredBastionProperty extends Property {
   @Override
   public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, Pre event) {
      if (user.getKnownMovement().length() <= 0.0) {
         event.setNewDamage(event.getNewDamage() * (slot.isArmor() ? 0.65F : 0.85F));
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 0;
   }
}
