package com.aetherteam.aether.inventory;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.slot.SlotBasedPredicate;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import io.wispforest.accessories.api.slot.UniqueSlotHandling.RegistrationCallback;
import io.wispforest.accessories.api.slot.UniqueSlotHandling.UniqueSlotBuilderFactory;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class AetherAccessorySlots implements RegistrationCallback {
   private static final ResourceLocation GLOVES_PREDICATE = ResourceLocation.fromNamespaceAndPath("aether", "gloves_items");
   private static final ResourceLocation RING_PREDICATE = ResourceLocation.fromNamespaceAndPath("aether", "ring_items");
   private static final ResourceLocation PENDANT_PREDICATE = ResourceLocation.fromNamespaceAndPath("aether", "pendant_items");
   private static final ResourceLocation CAPE_PREDICATE = ResourceLocation.fromNamespaceAndPath("aether", "cape_items");
   private static final ResourceLocation SHIELD_PREDICATE = ResourceLocation.fromNamespaceAndPath("aether", "shield_items");
   private static final ResourceLocation ACCESSORY_PREDICATE = ResourceLocation.fromNamespaceAndPath("aether", "accessory_items");
   public static final ResourceLocation GLOVES_SLOT_LOCATION = ResourceLocation.fromNamespaceAndPath("aether", "gloves_slot");
   public static final ResourceLocation RING_SLOT_LOCATION = ResourceLocation.fromNamespaceAndPath("aether", "ring_slot");
   public static final ResourceLocation PENDANT_SLOT_LOCATION = ResourceLocation.fromNamespaceAndPath("aether", "pendant_slot");
   public static final ResourceLocation CAPE_SLOT_LOCATION = ResourceLocation.fromNamespaceAndPath("aether", "cape_slot");
   public static final ResourceLocation SHIELD_SLOT_LOCATION = ResourceLocation.fromNamespaceAndPath("aether", "shield_slot");
   public static final ResourceLocation ACCESSORY_SLOT_LOCATION = ResourceLocation.fromNamespaceAndPath("aether", "accessory_slot");
   public static final AetherAccessorySlots INSTANCE = new AetherAccessorySlots();
   private static SlotTypeReference GLOVES_SLOT;
   private static SlotTypeReference RING_SLOT;
   private static SlotTypeReference PENDANT_SLOT;
   private static SlotTypeReference CAPE_SLOT;
   private static SlotTypeReference SHIELD_SLOT;
   private static SlotTypeReference ACCESSORY_SLOT;

   private AetherAccessorySlots() {
      AccessoriesAPI.registerPredicate(GLOVES_PREDICATE, SlotBasedPredicate.ofItem(item -> new ItemStack(item).is(AetherTags.Items.ACCESSORIES_GLOVES)));
      AccessoriesAPI.registerPredicate(RING_PREDICATE, SlotBasedPredicate.ofItem(item -> new ItemStack(item).is(AetherTags.Items.ACCESSORIES_RINGS)));
      AccessoriesAPI.registerPredicate(PENDANT_PREDICATE, SlotBasedPredicate.ofItem(item -> new ItemStack(item).is(AetherTags.Items.ACCESSORIES_PENDANTS)));
      AccessoriesAPI.registerPredicate(CAPE_PREDICATE, SlotBasedPredicate.ofItem(item -> new ItemStack(item).is(AetherTags.Items.ACCESSORIES_CAPES)));
      AccessoriesAPI.registerPredicate(SHIELD_PREDICATE, SlotBasedPredicate.ofItem(item -> new ItemStack(item).is(AetherTags.Items.ACCESSORIES_SHIELDS)));
      AccessoriesAPI.registerPredicate(
         ACCESSORY_PREDICATE, SlotBasedPredicate.ofItem(item -> new ItemStack(item).is(AetherTags.Items.ACCESSORIES_MISCELLANEOUS))
      );
   }

   public void registerSlots(UniqueSlotBuilderFactory factory) {
      if (!(Boolean)AetherConfig.COMMON.use_default_accessories_menu.get()) {
         GLOVES_SLOT = factory.create(GLOVES_SLOT_LOCATION, 1)
            .slotPredicates(new ResourceLocation[]{GLOVES_PREDICATE})
            .validTypes(
               new EntityType[]{
                  EntityType.PLAYER,
                  EntityType.ARMOR_STAND,
                  EntityType.ZOMBIE,
                  EntityType.ZOMBIE_VILLAGER,
                  EntityType.HUSK,
                  EntityType.SKELETON,
                  EntityType.STRAY,
                  EntityType.PIGLIN,
                  EntityType.ZOMBIFIED_PIGLIN
               }
            )
            .allowEquipFromUse(true)
            .build();
         RING_SLOT = factory.create(RING_SLOT_LOCATION, 2)
            .slotPredicates(new ResourceLocation[]{RING_PREDICATE})
            .validTypes(new EntityType[]{EntityType.PLAYER})
            .allowEquipFromUse(true)
            .build();
         PENDANT_SLOT = factory.create(PENDANT_SLOT_LOCATION, 1)
            .slotPredicates(new ResourceLocation[]{PENDANT_PREDICATE})
            .validTypes(
               new EntityType[]{
                  EntityType.PLAYER,
                  EntityType.ARMOR_STAND,
                  EntityType.ZOMBIE,
                  EntityType.ZOMBIE_VILLAGER,
                  EntityType.HUSK,
                  EntityType.SKELETON,
                  EntityType.STRAY,
                  EntityType.PIGLIN,
                  EntityType.ZOMBIFIED_PIGLIN
               }
            )
            .allowEquipFromUse(true)
            .build();
         CAPE_SLOT = factory.create(CAPE_SLOT_LOCATION, 1)
            .slotPredicates(new ResourceLocation[]{CAPE_PREDICATE})
            .validTypes(new EntityType[]{EntityType.PLAYER, EntityType.ARMOR_STAND})
            .allowEquipFromUse(true)
            .build();
         SHIELD_SLOT = factory.create(SHIELD_SLOT_LOCATION, 1)
            .slotPredicates(new ResourceLocation[]{SHIELD_PREDICATE})
            .validTypes(new EntityType[]{EntityType.PLAYER, EntityType.ARMOR_STAND})
            .allowEquipFromUse(true)
            .build();
         ACCESSORY_SLOT = factory.create(ACCESSORY_SLOT_LOCATION, 2)
            .slotPredicates(new ResourceLocation[]{ACCESSORY_PREDICATE})
            .validTypes(new EntityType[]{EntityType.PLAYER, EntityType.ARMOR_STAND})
            .allowEquipFromUse(true)
            .build();
      }
   }

   @Nullable
   public static SlotTypeReference getGlovesSlotType() {
      return GLOVES_SLOT;
   }

   @Nullable
   public static SlotTypeReference getRingSlotType() {
      return RING_SLOT;
   }

   @Nullable
   public static SlotTypeReference getPendantSlotType() {
      return PENDANT_SLOT;
   }

   @Nullable
   public static SlotTypeReference getCapeSlotType() {
      return CAPE_SLOT;
   }

   @Nullable
   public static SlotTypeReference getShieldSlotType() {
      return SHIELD_SLOT;
   }

   @Nullable
   public static SlotTypeReference getAccessorySlotType() {
      return ACCESSORY_SLOT;
   }
}
