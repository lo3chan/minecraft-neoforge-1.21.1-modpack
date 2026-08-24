package com.aetherteam.aether.client.gui.screen.inventory.recipebook;

import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class AltarRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
   private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("aether", "recipe_book/altar_filter_enabled"),
      ResourceLocation.fromNamespaceAndPath("aether", "recipe_book/altar_filter_disabled"),
      ResourceLocation.fromNamespaceAndPath("aether", "recipe_book/altar_filter_enabled_highlighted"),
      ResourceLocation.fromNamespaceAndPath("aether", "recipe_book/altar_filter_disabled_highlighted")
   );
   private static final Component FILTER_NAME = Component.translatable("gui.aether.recipebook.toggleRecipes.enchantable");

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(FILTER_SPRITES);
   }

   protected Component getRecipeFilterName() {
      return FILTER_NAME;
   }

   protected Set<Item> getFuelItems() {
      return BuiltInRegistries.ITEM.getDataMap(AetherDataMaps.ALTAR_FUEL).keySet().stream().<Item>map(BuiltInRegistries.ITEM::get).collect(Collectors.toSet());
   }
}
