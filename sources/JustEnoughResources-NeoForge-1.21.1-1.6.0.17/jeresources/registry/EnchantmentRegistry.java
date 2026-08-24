package jeresources.registry;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import jeresources.config.Settings;
import jeresources.entry.EnchantmentEntry;
import jeresources.util.RegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentRegistry {
   private Set<EnchantmentEntry> enchantments = new HashSet<>();
   private static EnchantmentRegistry instance;

   public static EnchantmentRegistry getInstance() {
      return instance == null ? (instance = new EnchantmentRegistry()) : instance;
   }

   public EnchantmentRegistry() {
      for (Holder<Enchantment> enchantment : getEnchants()) {
         if (enchantment != null) {
            this.enchantments.add(new EnchantmentEntry(enchantment));
         }
      }

      this.removeAll(Settings.excludedEnchants);
   }

   public Set<EnchantmentEntry> getEnchantments(ItemStack itemStack) {
      Set<EnchantmentEntry> set = new HashSet<>();

      for (EnchantmentEntry enchantmentEntry : this.enchantments) {
         if (enchantmentEntry.getEnchantment().isSupportedItem(itemStack)) {
            set.add(enchantmentEntry);
         }
      }

      return set;
   }

   private void excludeFormRegistry(Holder<Enchantment> enchantment) {
      this.enchantments
         .removeIf(
            enchantmentEntry -> enchantmentEntry.getEnchantment()
               .description()
               .getString()
               .equals(((Enchantment)enchantment.value()).description().getString())
         );
   }

   private void excludeFormRegistry(String sEnchantment) {
      for (Holder<Enchantment> enchantment : getEnchants()) {
         if (enchantment != null && ((Enchantment)enchantment.value()).description().getString().toLowerCase().contains(sEnchantment.toLowerCase())) {
            this.excludeFormRegistry(enchantment);
         }
      }
   }

   public void removeAll(String[] excludedEnchants) {
      for (String enchant : excludedEnchants) {
         this.excludeFormRegistry(enchant);
      }
   }

   private static Set<Reference<Enchantment>> getEnchants() {
      return RegistryHelper.getRegistry(Registries.ENCHANTMENT).holders().collect(Collectors.toSet());
   }
}
