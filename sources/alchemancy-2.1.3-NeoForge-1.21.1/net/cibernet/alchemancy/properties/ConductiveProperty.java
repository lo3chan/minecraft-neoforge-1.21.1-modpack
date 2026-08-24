package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ShockUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class ConductiveProperty extends Property {
   @Override
   public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, Pre event) {
      if (event.getSource().is(AlchemancyTags.DamageTypes.SHOCK_DAMAGE) && event.getNewDamage() > 1.0F) {
         ShockUtils.meleeShockAttack(user, user.position(), event.getOriginalDamage() * 0.5F);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 12741452;
   }
}
