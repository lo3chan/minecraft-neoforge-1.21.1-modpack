package io.github.razordevs.deep_aether.item.gear;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.accessories.ring.RingItem;
import io.github.razordevs.deep_aether.init.DAItems;
import io.github.razordevs.deep_aether.item.gear.skyjade.SkyjadeAccessory;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DAEquipmentUtil {
   protected static final AttributeModifier STEP_HEIGHT_BONUS = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("deep_aether", "step_height_bonus"), 1.0, Operation.ADD_VALUE
   );
   protected static final AttributeModifier SPEED_BONUS = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("deep_aether", "speed_bonus"), 0.2, Operation.ADD_VALUE
   );

   public static boolean hasFullStratusSet(LivingEntity entity) {
      return hasArmorSet(
         entity,
         (Item)DAItems.STRATUS_HELMET.get(),
         (Item)DAItems.STRATUS_CHESTPLATE.get(),
         (Item)DAItems.STRATUS_LEGGINGS.get(),
         (Item)DAItems.STRATUS_BOOTS.get(),
         (Item)DAItems.STRATUS_GLOVES.get()
      );
   }

   public static boolean hasFullStormforgedSet(LivingEntity entity) {
      return hasArmorSet(
         entity,
         (Item)DAItems.STORMFORGED_HELMET.get(),
         (Item)DAItems.STORMFORGED_CHESTPLATE.get(),
         (Item)DAItems.STORMFORGED_LEGGINGS.get(),
         (Item)DAItems.STORMFORGED_BOOTS.get()
      );
   }

   public static boolean hasFullSkyjadeSet(LivingEntity entity) {
      return hasArmorSet(
         entity,
         (Item)DAItems.SKYJADE_HELMET.get(),
         (Item)DAItems.SKYJADE_CHESTPLATE.get(),
         (Item)DAItems.SKYJADE_LEGGINGS.get(),
         (Item)DAItems.SKYJADE_BOOTS.get(),
         DAItems.SKYJADE_GLOVES.asItem()
      );
   }

   public static void updateSkyjadeBehavior(Player player, boolean enabled) {
      AttributeInstance step = player.getAttribute(Attributes.STEP_HEIGHT);
      AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
      if (enabled) {
         if (step != null && !step.hasModifier(STEP_HEIGHT_BONUS.id())) {
            step.addTransientModifier(STEP_HEIGHT_BONUS);
         }

         if (speed != null && speed.hasModifier(SPEED_BONUS.id())) {
            speed.addTransientModifier(SPEED_BONUS);
         }
      } else {
         if (step != null) {
            step.removeModifier(STEP_HEIGHT_BONUS.id());
         }

         if (speed != null) {
            speed.removeModifier(SPEED_BONUS.id());
         }
      }
   }

   private static boolean hasArmorSet(LivingEntity entity, Item helmet, Item chestplate, Item leggings, Item boots, Item gloves) {
      return entity.getItemBySlot(EquipmentSlot.HEAD).is(helmet)
         && entity.getItemBySlot(EquipmentSlot.CHEST).is(chestplate)
         && entity.getItemBySlot(EquipmentSlot.LEGS).is(leggings)
         && entity.getItemBySlot(EquipmentSlot.FEET).is(boots)
         && (!(Boolean)AetherConfig.SERVER.require_gloves.get() || EquipmentUtil.findFirstAccessory(entity, gloves).isPresent());
   }

   private static boolean hasArmorSet(LivingEntity entity, Item helmet, Item chestplate, Item leggings, Item boots) {
      return entity.getItemBySlot(EquipmentSlot.HEAD).is(helmet)
         && entity.getItemBySlot(EquipmentSlot.CHEST).is(chestplate)
         && entity.getItemBySlot(EquipmentSlot.LEGS).is(leggings)
         && entity.getItemBySlot(EquipmentSlot.FEET).is(boots);
   }

   public static boolean hasTwoSpookyRings(LivingEntity entity) {
      return EquipmentUtil.getAccessories(entity, (Item)DAItems.SPOOKY_RING.get()).size() == 2;
   }

   @Nullable
   public static SlotEntryReference getFloatyScarf(LivingEntity entity) {
      return EquipmentUtil.getAccessory(entity, (Item)DAItems.FLOATY_SCARF.get());
   }

   public static int getSkyjadeRingCount(LivingEntity entity) {
      return EquipmentUtil.getAccessories(entity, (Item)DAItems.SKYJADE_RING.get()).size();
   }

   public static boolean hasCloudNecklace(LivingEntity entity) {
      return EquipmentUtil.findFirstAccessory(entity, (Item)DAItems.AERCLOUD_NECKLACE.get()).isPresent();
   }

   public static boolean hasWindShield(LivingEntity entity) {
      return EquipmentUtil.findFirstAccessory(entity, (Item)DAItems.WIND_SHIELD.get()).isPresent();
   }

   public static void damageRing(LivingEntity entity, RingItem ring) {
      for (SlotEntryReference slotResult : EquipmentUtil.getAccessories(entity, ring)) {
         if (slotResult != null && entity.level() instanceof ServerLevel serverLevel) {
            slotResult.stack().hurtAndBreak(1, serverLevel, entity, item -> AccessoriesAPI.breakStack(slotResult.reference()));
         }
      }
   }

   public static double handleStratusRingBoost(LivingEntity entity) {
      damageRing(entity, (RingItem)DAItems.GRAVITITE_RING.get());
      damageRing(entity, (RingItem)DAItems.STRATUS_RING.get());
      double multiplier = 1.0;
      List<SlotEntryReference> items = EquipmentUtil.getAccessories(entity, (Item)DAItems.STRATUS_RING.get());
      if (!items.isEmpty()) {
         multiplier += 1.2 * items.size();
      }

      items = EquipmentUtil.getAccessories(entity, (Item)DAItems.GRAVITITE_RING.get());
      if (!items.isEmpty()) {
         multiplier += 1.1 * items.size();
      }

      return multiplier;
   }

   public static float handleSkyjadeRingAbility(LivingEntity entity, float speed) {
      float newSpeed = speed;

      for (SlotEntryReference slotResult : EquipmentUtil.getAccessories(entity, (Item)DAItems.SKYJADE_RING.get())) {
         if (slotResult != null) {
            newSpeed = SkyjadeAccessory.handleMiningSpeed(newSpeed, slotResult.stack());
         }
      }

      return newSpeed;
   }

   public static void damageSkyjadeRing(LivingEntity entity, LevelAccessor level, BlockState state, BlockPos pos) {
      for (SlotEntryReference slotResult : EquipmentUtil.getAccessories(entity, (Item)DAItems.SKYJADE_RING.get())) {
         if (slotResult != null
            && state.getDestroySpeed(level, pos) > 0.0F
            && entity.getRandom().nextInt(6) == 0
            && entity.level() instanceof ServerLevel serverLevel) {
            slotResult.stack().hurtAndBreak(1, serverLevel, entity, item -> AccessoriesAPI.breakStack(slotResult.reference()));
         }
      }
   }
}
