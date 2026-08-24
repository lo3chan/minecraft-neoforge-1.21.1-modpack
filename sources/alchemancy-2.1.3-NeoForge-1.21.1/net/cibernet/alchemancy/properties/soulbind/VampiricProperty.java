package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class VampiricProperty extends Property {
   @Override
   public void modifyAttackDamage(Entity user, ItemStack weapon, Pre event) {
      if (user instanceof LivingEntity living) {
         living.heal(Mth.clamp(event.getNewDamage() * 0.2F, 1.0F, event.getNewDamage()));
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      Level level = user.level();
      if (!level.isClientSide() && level.canSeeSky(user.blockPosition()) && level.isDay() && !level.isRaining() && user.getRandom().nextFloat() < 0.02F) {
         if (user.getRandom().nextFloat() < 0.1F) {
            user.setRemainingFireTicks(user.getRemainingFireTicks() + 80);
         } else if (stack.isDamageableItem()) {
            this.damageItem(user, stack, slot, 1);
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 12852006;
   }
}
