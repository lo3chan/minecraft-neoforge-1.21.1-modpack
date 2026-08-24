package net.bettercombat.logic;

import com.google.common.collect.Multimap;
import java.util.Arrays;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.ComboState;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.utils.AttributeModifierHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.Nullable;

public class PlayerAttackHelper {
   private static final Object attributesLock = new Object();

   public static float getDualWieldingAttackDamageMultiplier(Player player, AttackHand hand) {
      return isDualWielding(player)
         ? (
            hand.isOffHand()
               ? BetterCombatMod.config.dual_wielding_off_hand_damage_multiplier
               : BetterCombatMod.config.dual_wielding_main_hand_damage_multiplier
         )
         : 1.0F;
   }

   public static boolean shouldAttackWithOffHand(Player player, int comboCount) {
      return isDualWielding(player) && comboCount % 2 == 1;
   }

   public static boolean isDualWielding(Player player) {
      WeaponAttributes mainAttributes = WeaponRegistry.getAttributes(player.getMainHandItem());
      WeaponAttributes offAttributes = WeaponRegistry.getAttributes(player.getOffhandItem());
      return isDualWielding(mainAttributes, offAttributes);
   }

   public static boolean isDualWielding(WeaponAttributes mainAttributes, WeaponAttributes offAttributes) {
      return mainAttributes != null && !mainAttributes.isTwoHanded() && offAttributes != null && !offAttributes.isTwoHanded();
   }

   public static boolean isTwoHandedWielding(Player player) {
      WeaponAttributes mainAttributes = WeaponRegistry.getAttributes(player.getMainHandItem());
      return mainAttributes != null ? mainAttributes.isTwoHanded() : false;
   }

   public static float getAttackCooldownTicksCapped(Player player) {
      return Math.max(player.getCurrentItemAttackStrengthDelay(), (float)BetterCombatMod.config.attack_interval_cap);
   }

   @Nullable
   public static AttackHand getCurrentAttack(Player player, int comboCount) {
      if (isDualWielding(player)) {
         boolean isOffHand = shouldAttackWithOffHand(player, comboCount);
         ItemStack itemStack = isOffHand ? player.getOffhandItem() : player.getMainHandItem();
         WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
         if (attributes != null && attributes.attacks() != null) {
            int handSpecificComboCount = (isOffHand && comboCount > 0 ? comboCount - 1 : comboCount) / 2;
            PlayerAttackHelper.AttackSelection attackSelection = selectAttack(handSpecificComboCount, attributes, player, isOffHand);
            if (attackSelection == null) {
               return null;
            }

            WeaponAttributes.Attack attack = attackSelection.attack;
            ComboState combo = attackSelection.comboState;
            return new AttackHand(attack, combo, isOffHand, attributes, itemStack);
         }
      } else {
         ItemStack itemStack = player.getMainHandItem();
         WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
         if (attributes != null && attributes.attacks() != null) {
            PlayerAttackHelper.AttackSelection attackSelection = selectAttack(comboCount, attributes, player, false);
            if (attackSelection == null) {
               return null;
            }

            WeaponAttributes.Attack attack = attackSelection.attack;
            ComboState combo = attackSelection.comboState;
            return new AttackHand(attack, combo, false, attributes, itemStack);
         }
      }

      return null;
   }

   @Nullable
   private static PlayerAttackHelper.AttackSelection selectAttack(int comboCount, WeaponAttributes attributes, Player player, boolean isOffHandAttack) {
      WeaponAttributes.Attack[] attacks = attributes.attacks();
      attacks = Arrays.stream(attacks)
         .filter(attack -> attack.conditions() == null || attack.conditions().length == 0 || evaluateConditions(attack.conditions(), player, isOffHandAttack))
         .toArray(WeaponAttributes.Attack[]::new);
      if (comboCount < 0) {
         comboCount = 0;
      }

      if (attacks.length == 0) {
         return null;
      } else {
         int index = comboCount % attacks.length;
         return new PlayerAttackHelper.AttackSelection(attacks[index], new ComboState(index + 1, attacks.length));
      }
   }

   private static boolean evaluateConditions(WeaponAttributes.Condition[] conditions, Player player, boolean isOffHandAttack) {
      return Arrays.stream(conditions).allMatch(condition -> evaluateCondition(condition, player, isOffHandAttack));
   }

   private static boolean evaluateCondition(WeaponAttributes.Condition condition, Player player, boolean isOffHandAttack) {
      if (condition == null) {
         return true;
      } else {
         switch (condition) {
            case NOT_DUAL_WIELDING:
               return !isDualWielding(player);
            case DUAL_WIELDING_ANY:
               return isDualWielding(player);
            case DUAL_WIELDING_SAME:
               return isDualWielding(player) && player.getMainHandItem().getItem() == player.getOffhandItem().getItem();
            case DUAL_WIELDING_SAME_CATEGORY:
               if (!isDualWielding(player)) {
                  return false;
               } else {
                  WeaponAttributes mainHandAttributes = WeaponRegistry.getAttributes(player.getMainHandItem());
                  WeaponAttributes offHandAttributes = WeaponRegistry.getAttributes(player.getOffhandItem());
                  if (mainHandAttributes.category() != null
                     && !mainHandAttributes.category().isEmpty()
                     && offHandAttributes.category() != null
                     && !offHandAttributes.category().isEmpty()) {
                     return mainHandAttributes.category().equals(offHandAttributes.category());
                  }

                  return false;
               }
            case NO_OFFHAND_ITEM:
               ItemStack offhandStack = player.getOffhandItem();
               if (offhandStack != null && !offhandStack.isEmpty()) {
                  return false;
               }

               return true;
            case OFF_HAND_SHIELD:
               ItemStack offhandStack = player.getOffhandItem();
               if (offhandStack == null && !(offhandStack.getItem() instanceof ShieldItem)) {
                  return false;
               }

               return true;
            case MAIN_HAND_ONLY:
               return !isOffHandAttack;
            case OFF_HAND_ONLY:
               return isOffHandAttack;
            case MOUNTED:
               return player.getVehicle() != null;
            case NOT_MOUNTED:
               return player.getVehicle() == null;
            default:
               return true;
         }
      }
   }

   public static void swapHandAttributes(Player player, Runnable runnable) {
      swapHandAttributes(player, true, runnable);
   }

   public static void swapHandAttributes(Player player, boolean useOffHand, Runnable runnable) {
      if (!useOffHand) {
         runnable.run();
      } else {
         synchronized (player) {
            Inventory inventory = player.getInventory();
            ItemStack mainHandStack = player.getMainHandItem();
            ItemStack offHandStack = (ItemStack)inventory.offhand.get(0);
            setAttributesForOffHandAttack(player, true);
            inventory.items.set(inventory.selected, offHandStack);
            inventory.offhand.set(0, offHandStack);
            runnable.run();
            inventory.items.set(inventory.selected, mainHandStack);
            inventory.offhand.set(0, offHandStack);
            setAttributesForOffHandAttack(player, false);
         }
      }
   }

   private static void setAttributesForOffHandAttack(Player player, boolean useOffHand) {
      ItemStack mainHandStack = player.getMainHandItem();
      ItemStack offHandStack = player.getOffhandItem();
      ItemStack add;
      ItemStack remove;
      if (useOffHand) {
         remove = mainHandStack;
         add = offHandStack;
      } else {
         remove = offHandStack;
         add = mainHandStack;
      }

      if (remove != null) {
         Multimap<Holder<Attribute>, AttributeModifier> modifiersMap = AttributeModifierHelper.modifierMultimap(remove);
         player.getAttributes().removeAttributeModifiers(modifiersMap);
      }

      if (add != null) {
         Multimap<Holder<Attribute>, AttributeModifier> modifiersMap = AttributeModifierHelper.modifierMultimap(add);
         player.getAttributes().addTransientAttributeModifiers(modifiersMap);
      }
   }

   public static Pose poseForPlayer(Player player) {
      ItemStack mainHandStack = player.getMainHandItem();
      WeaponAttributes mainHandAttributes = WeaponRegistry.getAttributes(mainHandStack);
      String mainPose;
      if (mainHandAttributes != null && mainHandAttributes.pose() != null) {
         mainPose = mainHandAttributes.pose();
      } else {
         mainPose = "";
      }

      ItemStack offHandStack = player.getOffhandItem();
      WeaponAttributes offHandAttributes = WeaponRegistry.getAttributes(offHandStack);
      String offPose;
      if (isDualWielding(mainHandAttributes, offHandAttributes) && offHandAttributes != null && offHandAttributes.pose() != null) {
         offPose = offHandAttributes.pose();
      } else {
         offPose = "";
      }

      return new Pose(mainPose, offPose);
   }

   public static double getStaticRange(Player player, ItemStack stack) {
      WeaponAttributes attributes = WeaponRegistry.getAttributes(stack);
      return combineAttackRange(attributes, player.getAttributeBaseValue(Attributes.ENTITY_INTERACTION_RANGE));
   }

   public static double getRangeForItem(Player player, ItemStack stack) {
      double interactionRangeValue = player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
      return getRangeWithItem(stack, interactionRangeValue);
   }

   public static double getRangeWithWeapon(Player player, double interactionRangeValue) {
      return getRangeWithItem(player.getMainHandItem(), interactionRangeValue);
   }

   private static double getRangeWithItem(ItemStack stack, double interactionRangeValue) {
      if (EntityAttributeHelper.itemHasRangeAttribute(stack)) {
         return interactionRangeValue;
      } else {
         WeaponAttributes attributes = WeaponRegistry.getAttributes(stack);
         return combineAttackRange(attributes, interactionRangeValue);
      }
   }

   public static double combineAttackRange(WeaponAttributes attributes, double interactionRangeValue) {
      double range = interactionRangeValue;
      if (attributes != null) {
         if (attributes.attackRange() != 0.0) {
            return attributes.attackRange();
         }

         range = interactionRangeValue + attributes.rangeBonus();
      }

      return range;
   }

   private record AttackSelection(WeaponAttributes.Attack attack, ComboState comboState) {
   }
}
