package com.aetherteam.aether.integration.rei.categories.item;

import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay.Serializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class AetherCookingRecipeDisplay<T extends Recipe<?>> extends BasicDisplay {
   private final CategoryIdentifier<?> identifier;
   private final float experience;
   private final int cookingTime;
   private final boolean isIncubation;

   public AetherCookingRecipeDisplay(
      CategoryIdentifier<AetherCookingRecipeDisplay<T>> identifier,
      List<EntryIngredient> inputs,
      List<EntryIngredient> outputs,
      float experience,
      int cookingTime,
      boolean isIncubation,
      Optional<ResourceLocation> location
   ) {
      super(inputs, outputs, location);
      this.identifier = identifier;
      this.experience = experience;
      this.cookingTime = cookingTime;
      this.isIncubation = isIncubation;
   }

   public static <T extends Recipe<?>> AetherCookingRecipeDisplay<T> of(CategoryIdentifier<AetherCookingRecipeDisplay<T>> categoryIdentifier, T recipe) {
      int cookingTime = 0;
      float experience = 0.0F;
      if (recipe instanceof AbstractCookingRecipe cookingRecipe) {
         cookingTime = cookingRecipe.getCookingTime();
         experience = cookingRecipe.getExperience();
      } else if (recipe instanceof IncubationRecipe incubationRecipe) {
         cookingTime = incubationRecipe.getIncubationTime();
      }

      return new AetherCookingRecipeDisplay<>(
         categoryIdentifier, getInput(recipe), getOutput(recipe), experience, cookingTime, recipe instanceof IncubationRecipe, Optional.empty()
      );
   }

   private static List<EntryIngredient> getInput(Recipe<?> recipe) {
      if (recipe instanceof AltarRepairRecipe) {
         ItemStack damagedItem = ((Ingredient)recipe.getIngredients().getFirst()).getItems()[0].copy();
         damagedItem.setDamageValue(damagedItem.getMaxDamage() * 3 / 4);
         return List.of(EntryIngredients.of(damagedItem));
      } else {
         return List.of(EntryIngredients.ofIngredient((Ingredient)recipe.getIngredients().getFirst()));
      }
   }

   private static List<EntryIngredient> getOutput(Recipe<?> recipe) {
      return recipe instanceof AbstractAetherCookingRecipe cookingRecipe ? List.of(EntryIngredients.of(cookingRecipe.getResult())) : List.of();
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return this.identifier;
   }

   public float getExperience() {
      return this.experience;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   public boolean isIncubation() {
      return this.isIncubation;
   }

   public static <T extends Recipe<?>> DisplaySerializer<AetherCookingRecipeDisplay<T>> serializer(CategoryIdentifier<AetherCookingRecipeDisplay<T>> identifier) {
      return Serializer.of((input, output, location1, tag) -> {
         float experience = tag.getFloat("experience");
         int cookingTime = tag.getInt("cookingTime");
         boolean isIncubation = tag.getBoolean("isIncubation");
         return new AetherCookingRecipeDisplay(identifier, input, output, experience, cookingTime, isIncubation, location1);
      }, (display, tag) -> {
         tag.putFloat("experience", display.experience);
         tag.putInt("cookingTime", display.cookingTime);
         tag.putBoolean("isIncubation", display.isIncubation);
      });
   }
}
