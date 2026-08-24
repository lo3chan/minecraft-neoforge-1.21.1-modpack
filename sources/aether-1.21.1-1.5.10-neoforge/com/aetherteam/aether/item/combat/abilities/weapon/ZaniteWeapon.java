package com.aetherteam.aether.item.combat.abilities.weapon;

import com.aetherteam.aether.item.EquipmentUtil;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;

public interface ZaniteWeapon {
   ResourceLocation DAMAGE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("aether", "zanite_weapon_attack_damage");

   default Entry increaseDamage(ItemAttributeModifiers modifiers, ItemStack stack) {
      return new Entry(
         Attributes.ATTACK_DAMAGE,
         new AttributeModifier(
            DAMAGE_MODIFIER_ID, this.calculateDamageIncrease(Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER_ID, modifiers, stack), Operation.ADD_VALUE
         ),
         EquipmentSlotGroup.MAINHAND
      );
   }

   default int calculateDamageIncrease(Holder<Attribute> base, ResourceLocation bonusModifier, ItemAttributeModifiers modifiers, ItemStack stack) {
      AtomicReference<Double> baseStat = new AtomicReference<>(0.0);
      modifiers.forEach(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
         if (attribute.value() == base.value() && !modifier.id().equals(bonusModifier)) {
            baseStat.updateAndGet(v -> v + modifier.amount());
         }
      });
      double baseDamage = baseStat.get();
      double boostedDamage = EquipmentUtil.calculateZaniteBuff(stack, baseDamage);
      boostedDamage -= baseDamage;
      if (boostedDamage < 0.0) {
         boostedDamage = 0.0;
      }

      return (int)Math.round(boostedDamage);
   }
}
