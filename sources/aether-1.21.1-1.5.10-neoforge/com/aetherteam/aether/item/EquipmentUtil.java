package com.aetherteam.aether.item;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public final class EquipmentUtil {
   public static boolean isFullStrength(LivingEntity attacker) {
      boolean combatifyLoaded = ModList.get().isLoaded("combatify");
      return !(
         attacker instanceof Player player
            && (combatifyLoaded ? !(player.getAttackStrengthScale(1.0F) >= 1.95F) : !(player.getAttackStrengthScale(1.0F) >= 1.0F))
      );
   }

   public static double calculateZaniteBuff(ItemStack stack, double baseValue) {
      return baseValue * (2.0 * stack.getDamageValue() / stack.getMaxDamage() + 0.5);
   }

   @Nullable
   public static SlotEntryReference getGloves(LivingEntity entity) {
      Optional<SlotEntryReference> slotResultOptional = findFirstAccessory(entity, (Predicate<ItemStack>)(stack -> stack.getItem() instanceof GlovesItem));
      return slotResultOptional.orElse(null);
   }

   public static List<SlotEntryReference> getZaniteRings(LivingEntity entity) {
      return getAccessories(entity, (Item)AetherItems.ZANITE_RING.get());
   }

   @Nullable
   public static SlotEntryReference getZanitePendant(LivingEntity entity) {
      return getAccessory(entity, (Item)AetherItems.ZANITE_PENDANT.get());
   }

   public static boolean hasFreezingAccessory(LivingEntity entity) {
      return hasAccessory(entity, (Item)AetherItems.ICE_PENDANT.get()) || hasAccessory(entity, (Item)AetherItems.ICE_RING.get());
   }

   public static boolean hasSwetPacifyingAccessory(LivingEntity entity) {
      return findFirstAccessory(entity, (Predicate<ItemStack>)(stack -> stack.is(AetherTags.Items.PACIFIES_SWETS))).isPresent();
   }

   public static boolean hasSwetCape(LivingEntity entity) {
      return hasAccessory(entity, (Item)AetherItems.SWET_CAPE.get());
   }

   public static boolean hasInvisibilityCloak(LivingEntity entity) {
      return hasAccessory(entity, (Item)AetherItems.INVISIBILITY_CLOAK.get());
   }

   public static boolean hasCape(LivingEntity entity) {
      return findFirstAccessory(entity, (Predicate<ItemStack>)(stack -> stack.getItem() instanceof CapeItem)).isPresent();
   }

   @Nullable
   public static SlotEntryReference getCape(LivingEntity entity) {
      return findFirstAccessory(entity, (Predicate<ItemStack>)(stack -> stack.getItem() instanceof CapeItem)).orElse(null);
   }

   public static boolean hasAccessory(LivingEntity entity, Item item) {
      return findFirstAccessory(entity, item).isPresent();
   }

   @Nullable
   public static SlotEntryReference getAccessory(LivingEntity entity, Item item) {
      return findFirstAccessory(entity, item).orElse(null);
   }

   public static List<SlotEntryReference> getAccessories(LivingEntity entity, Item item) {
      AccessoriesCapability accessories = AccessoriesCapability.get(entity);
      return accessories != null ? accessories.getEquipped(item) : List.of();
   }

   public static boolean hasSentryBoots(LivingEntity entity) {
      return entity.getItemBySlot(EquipmentSlot.FEET).is((Item)AetherItems.SENTRY_BOOTS.get());
   }

   public static boolean hasFullGravititeSet(LivingEntity entity) {
      return hasArmorSet(
         entity,
         (Item)AetherItems.GRAVITITE_HELMET.get(),
         (Item)AetherItems.GRAVITITE_CHESTPLATE.get(),
         (Item)AetherItems.GRAVITITE_LEGGINGS.get(),
         (Item)AetherItems.GRAVITITE_BOOTS.get(),
         (Item)AetherItems.GRAVITITE_GLOVES.get()
      );
   }

   public static boolean hasFullValkyrieSet(LivingEntity entity) {
      return hasArmorSet(
         entity,
         (Item)AetherItems.VALKYRIE_HELMET.get(),
         (Item)AetherItems.VALKYRIE_CHESTPLATE.get(),
         (Item)AetherItems.VALKYRIE_LEGGINGS.get(),
         (Item)AetherItems.VALKYRIE_BOOTS.get(),
         (Item)AetherItems.VALKYRIE_GLOVES.get()
      );
   }

   public static boolean hasFullNeptuneSet(LivingEntity entity) {
      return hasArmorSet(
         entity,
         (Item)AetherItems.NEPTUNE_HELMET.get(),
         (Item)AetherItems.NEPTUNE_CHESTPLATE.get(),
         (Item)AetherItems.NEPTUNE_LEGGINGS.get(),
         (Item)AetherItems.NEPTUNE_BOOTS.get(),
         (Item)AetherItems.NEPTUNE_GLOVES.get()
      );
   }

   public static boolean hasFullPhoenixSet(LivingEntity entity) {
      return hasArmorSet(
         entity,
         (Item)AetherItems.PHOENIX_HELMET.get(),
         (Item)AetherItems.PHOENIX_CHESTPLATE.get(),
         (Item)AetherItems.PHOENIX_LEGGINGS.get(),
         (Item)AetherItems.PHOENIX_BOOTS.get(),
         (Item)AetherItems.PHOENIX_GLOVES.get()
      );
   }

   public static boolean hasAnyPhoenixArmor(LivingEntity entity) {
      return hasAnyArmor(
         entity,
         (Item)AetherItems.PHOENIX_HELMET.get(),
         (Item)AetherItems.PHOENIX_CHESTPLATE.get(),
         (Item)AetherItems.PHOENIX_LEGGINGS.get(),
         (Item)AetherItems.PHOENIX_BOOTS.get(),
         (Item)AetherItems.PHOENIX_GLOVES.get()
      );
   }

   private static boolean hasArmorSet(LivingEntity entity, Item helmet, Item chestplate, Item leggings, Item boots, Item gloves) {
      return entity.getItemBySlot(EquipmentSlot.HEAD).is(helmet)
         && entity.getItemBySlot(EquipmentSlot.CHEST).is(chestplate)
         && entity.getItemBySlot(EquipmentSlot.LEGS).is(leggings)
         && entity.getItemBySlot(EquipmentSlot.FEET).is(boots)
         && (!(Boolean)AetherConfig.SERVER.require_gloves.get() || findFirstAccessory(entity, gloves).isPresent());
   }

   private static boolean hasAnyArmor(LivingEntity entity, Item helmet, Item chestplate, Item leggings, Item boots, Item gloves) {
      return entity.getItemBySlot(EquipmentSlot.HEAD).is(helmet)
         || entity.getItemBySlot(EquipmentSlot.CHEST).is(chestplate)
         || entity.getItemBySlot(EquipmentSlot.LEGS).is(leggings)
         || entity.getItemBySlot(EquipmentSlot.FEET).is(boots)
         || findFirstAccessory(entity, gloves).isPresent();
   }

   public static Optional<SlotEntryReference> findFirstAccessory(LivingEntity entity, Item item) {
      return findFirstAccessory(entity, (Predicate<ItemStack>)(itemStack -> itemStack.is(item)));
   }

   public static Optional<SlotEntryReference> findFirstAccessory(LivingEntity entity, Predicate<ItemStack> predicate) {
      AccessoriesCapability accessories = AccessoriesCapability.get(entity);
      if (accessories != null) {
         SlotEntryReference slotEntryReference = accessories.getFirstEquipped(predicate);
         if (slotEntryReference != null) {
            return Optional.of(slotEntryReference);
         }
      }

      return Optional.empty();
   }
}
