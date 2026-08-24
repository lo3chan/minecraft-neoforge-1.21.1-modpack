package jeresources.entry;

import jeresources.util.TranslationHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentEntry {
   private Holder<Enchantment> enchantment;

   public EnchantmentEntry(Holder<Enchantment> enchantment) {
      this.enchantment = enchantment;
   }

   public String getTranslatedWithLevels() {
      String s = Enchantment.getFullname(this.enchantment, 1).getString();
      if (((Enchantment)this.enchantment.value()).getMinLevel() != ((Enchantment)this.enchantment.value()).getMaxLevel()) {
         s = s + "-" + TranslationHelper.translateAndFormat("enchantment.level." + ((Enchantment)this.enchantment.value()).getMaxLevel());
      }

      return s;
   }

   public Enchantment getEnchantment() {
      return (Enchantment)this.enchantment.value();
   }

   public Holder<Enchantment> getEnchantmentHolder() {
      return this.enchantment;
   }

   public HolderSet<Item> getSupportedItems() {
      return ((Enchantment)this.enchantment.value()).getSupportedItems();
   }
}
