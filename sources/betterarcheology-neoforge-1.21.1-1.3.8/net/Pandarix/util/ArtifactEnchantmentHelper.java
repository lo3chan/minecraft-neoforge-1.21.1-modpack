package net.Pandarix.util;

import net.Pandarix.BACommon;
import net.Pandarix.enchantment.ModEnchantments;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ArtifactEnchantmentHelper {
   public static boolean hasSoaringWinds(Player player) {
      if (player == null) {
         return false;
      } else {
         try {
            Reference<Enchantment> tunneling = player.level()
               .registryAccess()
               .asGetterLookup()
               .lookupOrThrow(Registries.ENCHANTMENT)
               .getOrThrow(ModEnchantments.SOARING_WINDS_KEY);
            if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem
               && EnchantmentHelper.getItemEnchantmentLevel(tunneling, player.getItemBySlot(EquipmentSlot.CHEST)) >= 1) {
               return true;
            }
         } catch (Exception var2) {
            BACommon.LOGGER.error("Could not find enchantment in registries: " + ModEnchantments.SOARING_WINDS_KEY, var2);
         }

         return false;
      }
   }
}
