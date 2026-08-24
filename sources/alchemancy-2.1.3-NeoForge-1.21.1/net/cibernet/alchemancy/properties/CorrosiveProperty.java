package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CorrosiveProperty extends Property {
   static ResourceKey<DamageType> CORROSIVE_DAMAGE_KEY = ResourceKey.create(
      Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("alchemancy", "corrosive")
   );

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      super.onActivation(source, target, stack, damageSource);
      if (target instanceof LivingEntity living) {
         living.hurt(new DamageSource(living.damageSources().damageTypes.getHolderOrThrow(CORROSIVE_DAMAGE_KEY)), 5.0F);
      }
   }

   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      ArrayList<EquipmentSlot> slots = new ArrayList<>(List.of(EquipmentSlot.values()));
      Collections.shuffle(slots);

      for (EquipmentSlot slot : slots) {
         ItemStack stack = target.getItemBySlot(slot);
         if (slot.isArmor() && !stack.isEmpty() && this.damageItem(target, stack, slot, 5)) {
            break;
         }
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (slot.isArmor() && !(user.getRandom().nextFloat() > 0.025F)) {
         ArrayList<EquipmentSlot> slots = new ArrayList<>(List.of(EquipmentSlot.values()));
         Collections.shuffle(slots);

         for (EquipmentSlot otherSlot : slots) {
            ItemStack otherStack = user.getItemBySlot(otherSlot);
            if (otherSlot != slot && !otherStack.isEmpty() && this.damageItem(user, otherStack, slot, 5)) {
               break;
            }
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 10085632;
   }
}
