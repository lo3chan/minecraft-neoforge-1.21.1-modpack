package com.aetherteam.aether.mixin;

import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import com.aetherteam.aether.mixin.mixins.common.accessor.MinecraftServerAccessor;
import com.mojang.datafixers.util.Pair;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
import java.nio.file.Path;
import java.util.stream.StreamSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class AetherMixinHooks {
   private static final ResourceLocation SWUFF_CAPE_LOCATION = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/models/accessory/capes/swuff_accessory.png"
   );

   public static ItemStack isCapeVisible(LivingEntity livingEntity) {
      AccessoriesCapability accessories = AccessoriesCapability.get(livingEntity);
      if (accessories != null) {
         AccessoriesContainer accessoriesContainer = accessories.getContainer(CapeItem.getStaticIdentifier());
         if (accessoriesContainer != null) {
            ExpandedSimpleContainer simpleAccessoriesContainer = accessoriesContainer.getAccessories();
            ExpandedSimpleContainer simpleCosmeticsContainer = accessoriesContainer.getCosmeticAccessories();
            Pair<Integer, ItemStack> stack = StreamSupport.<Pair<Integer, ItemStack>>stream(simpleAccessoriesContainer.spliterator(), true)
               .findFirst()
               .orElse(null);
            Pair<Integer, ItemStack> cosmeticStack = StreamSupport.<Pair<Integer, ItemStack>>stream(simpleCosmeticsContainer.spliterator(), true)
               .findFirst()
               .orElse(null);
            if (cosmeticStack != null && !((ItemStack)cosmeticStack.getSecond()).isEmpty() && Accessories.config().clientOptions.showCosmeticAccessories()) {
               stack = cosmeticStack;
            }

            if (stack != null && accessoriesContainer.shouldRender((Integer)stack.getFirst())) {
               return (ItemStack)stack.getSecond();
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public static ResourceLocation getCapeTexture(ItemStack stack) {
      if (stack.getItem() instanceof CapeItem capeItem) {
         return stack.getHoverName().getString().equalsIgnoreCase("swuff_'s cape") ? SWUFF_CAPE_LOCATION : capeItem.getCapeTexture();
      } else {
         return null;
      }
   }

   public static boolean canUnlockLevel(Path basePath) {
      return Minecraft.getInstance().screen != null
            && Minecraft.getInstance().screen instanceof SelectWorldScreen
            && Minecraft.getInstance().getSingleplayerServer() != null
         ? basePath.getFileName()
            .toString()
            .equals(((MinecraftServerAccessor)Minecraft.getInstance().getSingleplayerServer()).aether$getStorageSource().getLevelId())
         : false;
   }

   public static boolean canReplaceCurrentAccessory(Mob mob, ItemStack candidate, ItemStack existing) {
      if (EnchantmentHelper.hasAnyEnchantments(existing)) {
         return false;
      } else if (candidate.getItem() instanceof GlovesItem candidateGloves) {
         if (existing.getItem() instanceof GlovesItem existingGloves) {
            return candidateGloves.getDamage() != existingGloves.getDamage()
               ? candidateGloves.getDamage() > existingGloves.getDamage()
               : mob.canReplaceEqualItem(candidate, existing);
         } else {
            return true;
         }
      } else if (candidate.getItem() instanceof PendantItem) {
         return !(existing.getItem() instanceof PendantItem) ? true : mob.canReplaceEqualItem(candidate, existing);
      } else {
         return false;
      }
   }

   public static SlotTypeReference getIdentifierForItem(LivingEntity livingEntity, ItemStack stack) {
      if (stack.getItem() instanceof GlovesItem glovesItem) {
         return glovesItem.getIdentifier();
      } else {
         return !(
               stack.getItem() instanceof PendantItem pendantItem
                  && (livingEntity.getType() == EntityType.PIGLIN || livingEntity.getType() == EntityType.ZOMBIFIED_PIGLIN)
            )
            ? null
            : pendantItem.getIdentifier();
      }
   }

   public static ItemStack getItemByIdentifier(LivingEntity livingEntity, SlotTypeReference identifier) {
      AccessoriesCapability accessories = AccessoriesCapability.get(livingEntity);
      if (accessories != null) {
         AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
         if (accessoriesContainer != null) {
            return accessoriesContainer.getAccessories().getItem(0);
         }
      }

      return ItemStack.EMPTY;
   }

   public static void setItemByIdentifier(LivingEntity livingEntity, ItemStack itemStack, SlotTypeReference identifier) {
      AccessoriesCapability accessories = AccessoriesCapability.get(livingEntity);
      if (accessories != null) {
         AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
         if (accessoriesContainer != null) {
            accessoriesContainer.getAccessories().setItem(0, itemStack);
         }
      }
   }
}
