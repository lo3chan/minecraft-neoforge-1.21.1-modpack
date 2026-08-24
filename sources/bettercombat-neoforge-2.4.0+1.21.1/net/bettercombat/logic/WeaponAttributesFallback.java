package net.bettercombat.logic;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.config.FallbackConfig;
import net.bettercombat.utils.PatternMatching;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;

public class WeaponAttributesFallback {
   public static void initialize() {
      FallbackConfig config = (FallbackConfig)BetterCombatMod.fallbackConfig.value;

      for (ResourceLocation itemId : BuiltInRegistries.ITEM.keySet()) {
         Item item = (Item)BuiltInRegistries.ITEM.get(itemId);
         if (!PatternMatching.matches(itemId.toString(), config.blacklist_item_id_regex)) {
            FallbackConfig.CompatibilitySpecifier[] specifiers = null;
            if (hasAttributeModifier(item, Attributes.ATTACK_DAMAGE)) {
               specifiers = config.fallback_compatibility;
            } else if (item instanceof ProjectileWeaponItem) {
               specifiers = config.ranged_weapons;
            }

            if (specifiers != null) {
               for (FallbackConfig.CompatibilitySpecifier fallbackOption : specifiers) {
                  if (WeaponRegistry.getAttributes(itemId) == null && PatternMatching.matches(itemId.toString(), fallbackOption.item_id_regex)) {
                     AttributesContainer container = WeaponRegistry.containers.get(ResourceLocation.parse(fallbackOption.weapon_attributes));
                     if (container != null) {
                        WeaponRegistry.resolveAndRegisterAttributes(itemId, container);
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean hasAttributeModifier(Item item, Holder<Attribute> searchedAttribute) {
      ItemAttributeModifiers attributes = (ItemAttributeModifiers)item.components().get(DataComponents.ATTRIBUTE_MODIFIERS);

      for (Entry entry : attributes.modifiers()) {
         Holder<Attribute> attribute = entry.attribute();
         if (attribute == searchedAttribute || attribute.equals(searchedAttribute)) {
            return true;
         }
      }

      return false;
   }
}
