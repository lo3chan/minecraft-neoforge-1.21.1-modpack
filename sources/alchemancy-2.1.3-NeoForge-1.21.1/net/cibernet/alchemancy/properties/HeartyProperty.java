package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

public class HeartyProperty extends Property {
   private static final AttributeModifier HEALTH_MOD = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("alchemancy", "hearty_property_modifier"), 4.0, Operation.ADD_VALUE
   );

   @Override
   public void applyAttributes(ItemAttributeModifierEvent event) {
      EquipmentSlot slot = getEquipmentSlotForItem(event.getItemStack());
      if (slot.isArmor()) {
         event.addModifier(Attributes.MAX_HEALTH, HEALTH_MOD, EquipmentSlotGroup.bySlot(slot));
      }
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (target instanceof LivingEntity living) {
         living.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 1));
      }
   }

   @Override
   public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack) {
      if (stack.is(AlchemancyTags.Items.TRIGGERS_HEARTY) || stack.getFoodProperties(user) != null) {
         user.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 9600, 1));
      }

      return false;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16716563;
   }
}
