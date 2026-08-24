package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class SoulHarvesterProperty extends Property {
   @Override
   public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event) {
      float effectPctg = 1.0F - Math.clamp(target.getHealth() / target.getMaxHealth() * 1.25F, 0.0F, 1.0F);
      event.setAmount(event.getAmount() + event.getAmount() * (effectPctg * 0.8F - 0.25F));
   }

   @Override
   public void onKill(LivingEntity target, LivingEntity user, ItemStack stack, LivingDeathEvent event) {
      user.heal(target.getMaxHealth() * 0.1F);
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(1.5F, 5526528, 34909);
   }
}
