package com.aetherteam.aether.client.gui.screen.inventory.recipebook;

import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class IncubatorRecipeBookComponent extends RecipeBookComponent {
   private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("aether", "recipe_book/incubator_filter_enabled"),
      ResourceLocation.fromNamespaceAndPath("aether", "recipe_book/incubator_filter_disabled"),
      ResourceLocation.fromNamespaceAndPath("aether", "recipe_book/incubator_filter_enabled_highlighted"),
      ResourceLocation.fromNamespaceAndPath("aether", "recipe_book/incubator_filter_disabled_highlighted")
   );
   private static final Component FILTER_NAME = Component.translatable("gui.aether.recipebook.toggleRecipes.incubatable");
   @Nullable
   private Ingredient fuels;

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(FILTER_SPRITES);
   }

   public void slotClicked(@Nullable Slot slot) {
      super.slotClicked(slot);
      if (slot != null && slot.index < this.menu.getSize()) {
         this.ghostRecipe.clear();
      }
   }

   public void setupGhostRecipe(RecipeHolder<?> recipe, List<Slot> slots) {
      this.ghostRecipe.setRecipe(recipe);
      Slot fuelSlot = slots.get(1);
      if (fuelSlot.getItem().isEmpty()) {
         if (this.fuels == null) {
            this.fuels = Ingredient.of(this.getFuelItems().stream().map(ItemStack::new));
         }

         this.ghostRecipe.addIngredient(this.fuels, fuelSlot.x, fuelSlot.y);
      }

      Ingredient ingredient = (Ingredient)recipe.value().getIngredients().getFirst();
      if (!ingredient.isEmpty()) {
         Slot eggSlot = (Slot)slots.getFirst();
         this.ghostRecipe.addIngredient(ingredient, eggSlot.x, eggSlot.y);
      }
   }

   protected Component getRecipeFilterName() {
      return FILTER_NAME;
   }

   protected Set<Item> getFuelItems() {
      return BuiltInRegistries.ITEM
         .getDataMap(AetherDataMaps.INCUBATOR_FUEL)
         .keySet()
         .stream()
         .<Item>map(BuiltInRegistries.ITEM::get)
         .collect(Collectors.toSet());
   }
}
