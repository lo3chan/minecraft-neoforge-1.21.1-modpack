package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.network.S2CDeathWardEffectsPayload;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class DeathWardProperty extends Property {
   final int[] colors = new int[]{4318847, 16645102, 15391620, 16645102};

   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
   }

   @Override
   public void onUserDeath(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingDeathEvent event) {
      if (!event.isCanceled()
         && CommonHooks.onLivingUseTotem(entity, event.getSource(), stack, slot == EquipmentSlot.OFFHAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND)
         )
       {
         if (!stack.isEmpty()) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new S2CDeathWardEffectsPayload(entity, stack.copy()), new CustomPacketPayload[0]);
         }

         this.damageOrConsumeItem(entity, stack, slot, 500);
         entity.setHealth(1.0F);
         event.setCanceled(true);
         entity.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
         entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
         entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
         entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(0.5F, this.colors);
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
   }

   @Override
   public boolean hasJournalEntry() {
      return false;
   }
}
