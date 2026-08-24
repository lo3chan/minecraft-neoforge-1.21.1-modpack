package com.aetherteam.aether.item.combat.abilities.armor;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

public interface PhoenixArmor {
   static void boostLavaSwimming(LivingEntity entity) {
      if (EquipmentUtil.hasFullPhoenixSet(entity)) {
         entity.clearFire();
         if (entity.isInLava()) {
            entity.resetFallDistance();
            if (entity instanceof Player player) {
               AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
               float defaultBoost = boostWithDepthStrider(entity, 1.75F, 1.0F);
               data.setPhoenixSubmergeLength(Math.min(data.getPhoenixSubmergeLength() + 0.1, 1.0));
               defaultBoost *= (float)data.getPhoenixSubmergeLength();
               entity.moveRelative(0.04F * defaultBoost, new Vec3(entity.xxa, entity.yya, entity.zza));
            } else {
               float defaultBoost = boostWithDepthStrider(entity, 1.75F, 1.0F);
               entity.moveRelative(0.04F * defaultBoost, new Vec3(entity.xxa, entity.yya, entity.zza));
            }
         }

         if (entity.level() instanceof ServerLevel level) {
            level.sendParticles(
               ParticleTypes.FLAME,
               entity.getX() + level.getRandom().nextGaussian() / 5.0,
               entity.getY() + level.getRandom().nextGaussian() / 3.0,
               entity.getZ() + level.getRandom().nextGaussian() / 5.0,
               1,
               0.0,
               0.0,
               0.0,
               0.0
            );
         }
      }

      if ((!EquipmentUtil.hasFullPhoenixSet(entity) || !entity.isInLava()) && entity instanceof Player player) {
         ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).setPhoenixSubmergeLength(0.0);
      }
   }

   static void boostVerticalLavaSwimming(LivingEntity entity) {
      if (EquipmentUtil.hasFullPhoenixSet(entity)) {
         entity.clearFire();
         if (entity.isInLava()) {
            entity.resetFallDistance();
            if (entity instanceof Player player) {
               AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
               float defaultBoost = boostWithDepthStrider(entity, 1.5F, 0.05F);
               data.setPhoenixSubmergeLength(Math.min(data.getPhoenixSubmergeLength() + 0.1, 1.0));
               defaultBoost *= (float)data.getPhoenixSubmergeLength();
               if (entity.getDeltaMovement().y() > 0.0 || entity.isCrouching()) {
                  entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, defaultBoost, 1.0));
               }
            } else {
               float defaultBoost = boostWithDepthStrider(entity, 1.5F, 0.05F);
               if (entity.getDeltaMovement().y() > 0.0 || entity.isCrouching()) {
                  entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, defaultBoost, 1.0));
               }
            }
         }
      }
   }

   private static float boostWithDepthStrider(LivingEntity entity, float start, float increment) {
      float defaultBoost = start;
      float depthStriderModifier = Math.min(
         (float)EnchantmentHelper.getEnchantmentLevel(entity.level().holderOrThrow(Enchantments.DEPTH_STRIDER), entity), 3.0F
      );
      if (depthStriderModifier > 0.0F) {
         defaultBoost = start + depthStriderModifier * increment;
      }

      return defaultBoost;
   }

   static void damageArmor(LivingEntity entity) {
      if (entity instanceof Player player) {
         AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         if (EquipmentUtil.hasAnyPhoenixArmor(entity) && entity.isInWaterRainOrBubble()) {
            if (entity.level().getGameTime() % 15L == 0L) {
               data.setObsidianConversionTime(data.getObsidianConversionTime() + 1);
               entity.level().levelEvent(1501, entity.blockPosition(), 0);
            }
         } else {
            data.setObsidianConversionTime(0);
         }

         if (data.getObsidianConversionTime() >= data.getObsidianConversionTimerMax()) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
               if (equipmentSlot.getType() == Type.HUMANOID_ARMOR) {
                  ItemStack equippedStack = entity.getItemBySlot(equipmentSlot);
                  if (equippedStack.is((Item)AetherItems.PHOENIX_HELMET.get())) {
                     breakPhoenixArmor(entity, equippedStack, new ItemStack((ItemLike)AetherItems.OBSIDIAN_HELMET.get()), equipmentSlot);
                  } else if (equippedStack.is((Item)AetherItems.PHOENIX_CHESTPLATE.get())) {
                     breakPhoenixArmor(entity, equippedStack, new ItemStack((ItemLike)AetherItems.OBSIDIAN_CHESTPLATE.get()), equipmentSlot);
                  } else if (equippedStack.is((Item)AetherItems.PHOENIX_LEGGINGS.get())) {
                     breakPhoenixArmor(entity, equippedStack, new ItemStack((ItemLike)AetherItems.OBSIDIAN_LEGGINGS.get()), equipmentSlot);
                  } else if (equippedStack.is((Item)AetherItems.PHOENIX_BOOTS.get())) {
                     breakPhoenixArmor(entity, equippedStack, new ItemStack((ItemLike)AetherItems.OBSIDIAN_BOOTS.get()), equipmentSlot);
                  }
               }
            }

            SlotEntryReference slotResult = EquipmentUtil.getAccessory(entity, (Item)AetherItems.PHOENIX_GLOVES.get());
            if (slotResult != null) {
               breakPhoenixGloves(entity, slotResult, new ItemStack((ItemLike)AetherItems.OBSIDIAN_GLOVES.get()));
            }
         }
      }
   }

   private static void breakPhoenixArmor(LivingEntity entity, ItemStack equippedStack, ItemStack outcomeStack, EquipmentSlot slot) {
      outcomeStack = new ItemStack(outcomeStack.getItemHolder(), 1, equippedStack.getComponentsPatch());
      entity.setItemSlot(slot, outcomeStack);
      if (entity instanceof ServerPlayer serverPlayer) {
         CriteriaTriggers.INVENTORY_CHANGED.trigger(serverPlayer, serverPlayer.getInventory(), outcomeStack);
      }
   }

   private static void breakPhoenixGloves(LivingEntity entity, SlotEntryReference slotResult, ItemStack outcomeStack) {
      outcomeStack = new ItemStack(outcomeStack.getItemHolder(), 1, slotResult.stack().getComponentsPatch());
      AccessoriesCapability accessories = AccessoriesCapability.get(entity);
      if (accessories != null) {
         AccessoriesContainer accessoriesContainer = accessories.getContainer(slotResult.reference().type());
         if (accessoriesContainer != null) {
            accessoriesContainer.getAccessories().setItem(slotResult.reference().slot(), outcomeStack);
         }
      }

      if (entity instanceof ServerPlayer serverPlayer) {
         CriteriaTriggers.INVENTORY_CHANGED.trigger(serverPlayer, serverPlayer.getInventory(), outcomeStack);
      }
   }

   static boolean extinguishUser(LivingEntity entity, DamageSource source) {
      return EquipmentUtil.hasFullPhoenixSet(entity) && source.is(DamageTypeTags.IS_FIRE);
   }
}
