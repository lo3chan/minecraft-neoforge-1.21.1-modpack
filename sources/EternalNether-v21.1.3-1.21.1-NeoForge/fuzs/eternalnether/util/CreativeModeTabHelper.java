package fuzs.eternalnether.util;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

@Deprecated(
   forRemoval = true
)
public final class CreativeModeTabHelper {
   static final Collection<Item> POTION_ITEMS = ImmutableSet.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW);

   private CreativeModeTabHelper() {
   }

   public static Component getTitle(ResourceLocation resourceLocation) {
      String translationKey = "itemGroup.%s.%s".formatted(resourceLocation.getNamespace(), resourceLocation.getPath());
      return Component.translatable(translationKey);
   }

   public static DisplayItemsGenerator getDisplayItems(String modId) {
      return getDisplayItems(modId, Predicates.alwaysTrue());
   }

   public static DisplayItemsGenerator getDisplayItems(String modId, Predicate<ItemStack> filter) {
      return (itemDisplayParameters, output) -> {
         Consumer<ItemStack> itemStacks = itemStack -> {
            if (filter.test(itemStack)) {
               output.accept(itemStack);
            }
         };
         appendAllItems(modId, itemDisplayParameters.holders(), itemStacks);
         appendAllEnchantments(modId, itemDisplayParameters.holders(), itemStacks);
         appendAllPotions(modId, itemDisplayParameters.holders(), itemStacks);
      };
   }

   public static void appendAllItems(String modId, Provider registries, Consumer<ItemStack> itemStacks) {
      getHoldersFromNamespace(Registries.ITEM, registries, modId).<ItemStack>map(ItemStack::new).forEach(itemStacks);
   }

   public static void appendAllEnchantments(String modId, Provider registries, Consumer<ItemStack> itemStacks) {
      getHoldersFromNamespace(Registries.ENCHANTMENT, registries, modId)
         .map(holder -> new EnchantmentInstance(holder, ((Enchantment)holder.value()).getMaxLevel()))
         .<ItemStack>map(EnchantedBookItem::createForEnchantment)
         .forEach(itemStacks);
   }

   public static void appendAllPotions(String modId, Provider registries, Consumer<ItemStack> itemStacks) {
      List<Reference<Potion>> potions = getHoldersFromNamespace(Registries.POTION, registries, modId)
         .filter(holder -> !((Potion)holder.value()).getEffects().isEmpty())
         .sorted(Comparator.comparing(holder -> (MobEffectInstance)((Potion)holder.value()).getEffects().getFirst()))
         .toList();

      for (Item item : POTION_ITEMS) {
         for (Reference<Potion> potion : potions) {
            itemStacks.accept(PotionContents.createItemStack(item, potion));
         }
      }
   }

   public static <T> Stream<Reference<T>> getHoldersFromNamespace(ResourceKey<? extends Registry<? extends T>> registryKey, Provider registries, String modId) {
      return registries.lookup(registryKey)
         .stream()
         .<Reference<T>>flatMap(HolderLookup::listElements)
         .filter(holder -> holder.key().location().getNamespace().equals(modId));
   }
}
