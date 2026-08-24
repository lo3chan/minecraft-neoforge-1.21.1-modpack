package fuzs.puzzleslib.impl.item;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class CreativeModeTabHelper {
   static final Collection<Item> POTION_ITEMS = ImmutableSet.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW);

   private CreativeModeTabHelper() {
   }

   public static Component getTitle(ResourceLocation resourceLocation) {
      String translationKey = "itemGroup.%s.%s".formatted(resourceLocation.getNamespace(), resourceLocation.getPath());
      return Component.translatable(translationKey);
   }

   public static DisplayItemsGenerator getDisplayItems(String modId) {
      return (itemDisplayParameters, output) -> {
         generateItemTypes(modId, itemDisplayParameters, output);
         generateEnchantmentBookTypes(modId, itemDisplayParameters, output);
         generatePotionEffectTypes(modId, itemDisplayParameters, output);
         generatePaintingTypes(modId, itemDisplayParameters, output);
      };
   }

   public static void generateItemTypes(String modId, ItemDisplayParameters itemDisplayParameters, Output output) {
      itemDisplayParameters.holders()
         .lookup(Registries.ITEM)
         .ifPresent(
            registryLookup -> registryLookup.listElements()
               .filter(holder -> holder.key().location().getNamespace().equals(modId))
               .map(ItemStack::new)
               .forEach(itemStack -> output.accept(itemStack, TabVisibility.PARENT_AND_SEARCH_TABS))
         );
   }

   public static void generateEnchantmentBookTypes(String modId, ItemDisplayParameters itemDisplayParameters, Output output) {
      itemDisplayParameters.holders()
         .lookup(Registries.ENCHANTMENT)
         .ifPresent(
            registryLookup -> CreativeModeTabs.generateEnchantmentBookTypesOnlyMaxLevel(
               (itemStack, tabVisibility) -> {
                  if (((ItemEnchantments)itemStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY))
                     .keySet()
                     .stream()
                     .findAny()
                     .flatMap(Holder::unwrapKey)
                     .map(ResourceKey::location)
                     .<String>map(ResourceLocation::getNamespace)
                     .filter(modId::equals)
                     .isPresent()) {
                     output.accept(itemStack, tabVisibility);
                  }
               },
               registryLookup,
               TabVisibility.PARENT_TAB_ONLY
            )
         );
   }

   public static void generatePotionEffectTypes(String modId, ItemDisplayParameters itemDisplayParameters, Output output) {
      itemDisplayParameters.holders()
         .lookup(Registries.POTION)
         .ifPresent(
            registryLookup -> {
               for (Item item : POTION_ITEMS) {
                  CreativeModeTabs.generatePotionEffectTypes(
                     (itemStack, tabVisibility) -> {
                        if (((PotionContents)itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY))
                           .potion()
                           .flatMap(Holder::unwrapKey)
                           .map(ResourceKey::location)
                           .<String>map(ResourceLocation::getNamespace)
                           .filter(modId::equals)
                           .isPresent()) {
                           output.accept(itemStack, tabVisibility);
                        }
                     },
                     registryLookup,
                     item,
                     TabVisibility.PARENT_AND_SEARCH_TABS,
                     itemDisplayParameters.enabledFeatures()
                  );
               }
            }
         );
   }

   public static void generatePaintingTypes(String modId, ItemDisplayParameters itemDisplayParameters, Output output) {
      itemDisplayParameters.holders()
         .lookup(Registries.PAINTING_VARIANT)
         .ifPresent(
            registryLookup -> CreativeModeTabs.generatePresetPaintings(
               output,
               itemDisplayParameters.holders(),
               registryLookup,
               holder -> holder.unwrapKey().map(ResourceKey::location).<String>map(ResourceLocation::getNamespace).filter(modId::equals).isPresent(),
               TabVisibility.PARENT_AND_SEARCH_TABS
            )
         );
   }
}
