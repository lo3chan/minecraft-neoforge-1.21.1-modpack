package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;

public class LeapingProperty extends Property implements IDataHolder<LeapingProperty.LeapData> {
   public static final AttributeModifier SAFE_FALL_MOD = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("alchemancy", "leaping_property_modifier"), 7.0, Operation.ADD_VALUE
   );
   public static final AttributeModifier ANIMAL_JUMP_BOOST_MOD = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("alchemancy", "leaping_property_modifier"), 0.5, Operation.ADD_MULTIPLIED_TOTAL
   );
   private static final int GRACE_TIME = 4;
   private static final int MAX_CHAIN = 4;
   private static final LeapingProperty.LeapData DEFAULT = new LeapingProperty.LeapData(0, 0, true);

   @Override
   public void applyAttributes(ItemAttributeModifierEvent event) {
      EquipmentSlot slot = getEquipmentSlotForItem(event.getItemStack());
      if (slot == EquipmentSlot.LEGS) {
         event.addModifier(Attributes.SAFE_FALL_DISTANCE, SAFE_FALL_MOD, EquipmentSlotGroup.LEGS);
      } else if (slot == EquipmentSlot.FEET) {
         event.addModifier(Attributes.SAFE_FALL_DISTANCE, SAFE_FALL_MOD, EquipmentSlotGroup.FEET);
      } else if (slot == EquipmentSlot.BODY) {
         event.addModifier(Attributes.SAFE_FALL_DISTANCE, SAFE_FALL_MOD, EquipmentSlotGroup.BODY);
         event.addModifier(Attributes.JUMP_STRENGTH, ANIMAL_JUMP_BOOST_MOD, EquipmentSlotGroup.BODY);
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (user.level().isClientSide && (slot == EquipmentSlot.FEET || slot == EquipmentSlot.LEGS)) {
         LeapingProperty.LeapData data = this.getData(stack);
         int chain = data.chain;
         int timestamp = data.lastLandedTimestamp;
         boolean wasOnGround = data.wasOnGround;
         if (wasOnGround != user.onGround()) {
            if (user.onGround()) {
               timestamp = user.tickCount;
            }

            wasOnGround = user.onGround();
         } else if (user.onGround() && (user.tickCount < timestamp || user.tickCount > timestamp + 4)) {
            chain = 0;
         }

         LeapingProperty.LeapData newData = new LeapingProperty.LeapData(chain, timestamp, wasOnGround);
         if (!data.equals(newData)) {
            this.setData(stack, newData);
         }
      }
   }

   @Override
   public void onJump(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingJumpEvent event) {
      if (entity.level().isClientSide() && (slot == EquipmentSlot.FEET || slot == EquipmentSlot.LEGS)) {
         LeapingProperty.LeapData data = this.getData(stack);
         event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().multiply(1.0, 1.2 + data.chain * 0.2, 1.0));
         if (data.chain < 4) {
            this.setData(stack, new LeapingProperty.LeapData(data.chain + 1, data.lastLandedTimestamp, data.wasOnGround));
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ((MobEffect)MobEffects.JUMP.value()).getColor();
   }

   public LeapingProperty.LeapData readData(CompoundTag tag) {
      return new LeapingProperty.LeapData(tag.getInt("chain"), tag.getInt("last_landed_timestamp"), tag.getBoolean("was_on_ground"));
   }

   public CompoundTag writeData(final LeapingProperty.LeapData data) {
      return new CompoundTag() {
         {
            this.putInt("chain", data.chain);
            this.putInt("last_landed_timestamp", data.lastLandedTimestamp);
            this.putBoolean("was_on_ground", data.wasOnGround);
         }
      };
   }

   public LeapingProperty.LeapData getDefaultData() {
      return DEFAULT;
   }

   public record LeapData(int chain, int lastLandedTimestamp, boolean wasOnGround) {
   }
}
