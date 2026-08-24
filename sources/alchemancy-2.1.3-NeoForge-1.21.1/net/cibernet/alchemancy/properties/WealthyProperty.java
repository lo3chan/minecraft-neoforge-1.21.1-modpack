package net.cibernet.alchemancy.properties;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

public class WealthyProperty extends Property {
   @Override
   public void modifyEnchantmentLevels(GetEnchantmentLevelEvent event) {
      Mutable enchantments = event.getEnchantments();
      modifyEnchantmentLevel(enchantments, event.getLookup(), Enchantments.FORTUNE, level -> level + 1);
      modifyEnchantmentLevel(enchantments, event.getLookup(), Enchantments.LOOTING, level -> level + 1);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 1900399;
   }
}
