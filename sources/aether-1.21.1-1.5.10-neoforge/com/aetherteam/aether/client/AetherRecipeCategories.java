package com.aetherteam.aether.client;

import com.aetherteam.aether.inventory.AetherRecipeBookTypes;
import com.aetherteam.aether.recipe.AetherBookCategory;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import java.util.function.Supplier;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

public class AetherRecipeCategories {
   public static final Supplier<RecipeBookCategories> ENCHANTING_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_ENCHANTING_SEARCH"));
   public static final Supplier<RecipeBookCategories> ENCHANTING_FOOD = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_ENCHANTING_FOOD"));
   public static final Supplier<RecipeBookCategories> ENCHANTING_BLOCKS = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_ENCHANTING_BLOCKS"));
   public static final Supplier<RecipeBookCategories> ENCHANTING_MISC = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_ENCHANTING_MISC"));
   public static final Supplier<RecipeBookCategories> ENCHANTING_REPAIR = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_ENCHANTING_REPAIR"));
   public static final Supplier<RecipeBookCategories> FREEZABLE_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_FREEZABLE_SEARCH"));
   public static final Supplier<RecipeBookCategories> FREEZABLE_BLOCKS = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_FREEZABLE_BLOCKS"));
   public static final Supplier<RecipeBookCategories> FREEZABLE_MISC = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_FREEZABLE_MISC"));
   public static final Supplier<RecipeBookCategories> INCUBATION_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_INCUBATION_SEARCH"));
   public static final Supplier<RecipeBookCategories> INCUBATION_MISC = Suppliers.memoize(() -> RecipeBookCategories.valueOf("AETHER_INCUBATION_MISC"));

   public static void registerRecipeCategories(RegisterRecipeBookCategoriesEvent event) {
      event.registerBookCategories(
         AetherRecipeBookTypes.ALTAR,
         ImmutableList.of(ENCHANTING_SEARCH.get(), ENCHANTING_FOOD.get(), ENCHANTING_BLOCKS.get(), ENCHANTING_MISC.get(), ENCHANTING_REPAIR.get())
      );
      event.registerAggregateCategory(
         ENCHANTING_SEARCH.get(), ImmutableList.of(ENCHANTING_FOOD.get(), ENCHANTING_BLOCKS.get(), ENCHANTING_MISC.get(), ENCHANTING_REPAIR.get())
      );
      event.registerRecipeCategoryFinder(
         (RecipeType)AetherRecipeTypes.ENCHANTING.get(),
         recipe -> {
            if (!(recipe.value() instanceof AltarRepairRecipe)
               && !(
                  recipe.value() instanceof AbstractAetherCookingRecipe abstractAetherCookingRecipe
                     && abstractAetherCookingRecipe.aetherCategory() == AetherBookCategory.ENCHANTING_REPAIR
               )) {
               if (recipe.value() instanceof AbstractAetherCookingRecipe abstractAetherCookingRecipex) {
                  if (abstractAetherCookingRecipex.aetherCategory() == AetherBookCategory.ENCHANTING_FOOD) {
                     return ENCHANTING_FOOD.get();
                  }

                  if (abstractAetherCookingRecipex.aetherCategory() == AetherBookCategory.ENCHANTING_BLOCKS) {
                     return ENCHANTING_BLOCKS.get();
                  }
               }

               return ENCHANTING_MISC.get();
            } else {
               return ENCHANTING_REPAIR.get();
            }
         }
      );
      event.registerBookCategories(AetherRecipeBookTypes.FREEZER, ImmutableList.of(FREEZABLE_SEARCH.get(), FREEZABLE_BLOCKS.get(), FREEZABLE_MISC.get()));
      event.registerAggregateCategory(FREEZABLE_SEARCH.get(), ImmutableList.of(FREEZABLE_BLOCKS.get(), FREEZABLE_MISC.get()));
      event.registerRecipeCategoryFinder(
         (RecipeType)AetherRecipeTypes.FREEZING.get(),
         recipe -> recipe.value() instanceof AbstractAetherCookingRecipe abstractAetherCookingRecipe
               && abstractAetherCookingRecipe.aetherCategory() == AetherBookCategory.FREEZABLE_BLOCKS
            ? FREEZABLE_BLOCKS.get()
            : FREEZABLE_MISC.get()
      );
      event.registerBookCategories(AetherRecipeBookTypes.INCUBATOR, ImmutableList.of(INCUBATION_SEARCH.get(), INCUBATION_MISC.get()));
      event.registerAggregateCategory(INCUBATION_SEARCH.get(), ImmutableList.of(INCUBATION_MISC.get()));
      event.registerRecipeCategoryFinder((RecipeType)AetherRecipeTypes.INCUBATION.get(), recipe -> INCUBATION_MISC.get());
   }
}
