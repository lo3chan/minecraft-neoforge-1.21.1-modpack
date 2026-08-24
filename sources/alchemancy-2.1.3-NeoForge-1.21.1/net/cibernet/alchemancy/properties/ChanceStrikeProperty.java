package net.cibernet.alchemancy.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import org.jetbrains.annotations.Nullable;

public class ChanceStrikeProperty extends Property {
   private static final double BASE_CHANCE = 0.5;

   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      if (user != null) {
         activateByEntity(user, target, weapon);
      }
   }

   @Override
   public void modifyAttackDamage(Entity user, ItemStack weapon, Pre event) {
      event.setNewDamage(event.getNewDamage() * (user.getRandom().nextFloat() * 0.6F + 0.7F));
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
   }

   @Override
   public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource) {
      if (slot.isArmor() && damageSource.getDirectEntity() != null) {
         activateByEntity(user, damageSource.getDirectEntity(), weapon);
      }
   }

   @Override
   public boolean shouldActivate(Level level, @Nullable Entity source, Entity target, ItemStack stack) {
      return level.getRandom().nextDouble() <= this.getChance(source);
   }

   @Override
   public boolean shouldActivateByBlock(Level level, BlockPos pos, Entity target, ItemStack stack) {
      return level.getRandom().nextDouble() <= this.getChance(null);
   }

   public double getChance(@Nullable Entity user) {
      return user instanceof LivingEntity living && living.getAttributes().hasAttribute(Attributes.LUCK)
         ? living.getAttributeValue(Attributes.LUCK) * 0.2 + 0.5
         : 0.5;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16773027;
   }
}
