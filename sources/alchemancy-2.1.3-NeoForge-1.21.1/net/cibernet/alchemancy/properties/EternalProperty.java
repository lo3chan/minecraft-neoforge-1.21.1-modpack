package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.mixin.accessors.ItemEntityAccessor;
import net.cibernet.alchemancy.properties.voidborn.VoidbornProperty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class EternalProperty extends VoidbornProperty {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
   }

   @Override
   public void onIncomingDamageReceived(Entity user, ItemStack stack, EquipmentSlot slot, DamageSource source, LivingIncomingDamageEvent event) {
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      super.onEntityItemTick(stack, itemEntity);
      if (!itemEntity.level().isClientSide()) {
         ((ItemEntityAccessor)itemEntity).setAge(0);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16720134;
   }
}
