package io.github.razordevs.deep_aether.init;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import io.github.razordevs.deep_aether.recipe.DABookCategory;
import io.github.razordevs.deep_aether.recipe.DARecipeTypes;
import io.github.razordevs.deep_aether.recipe.combiner.CombinerRecipe;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

public class DARecipeCategories {
   public static final Supplier<RecipeBookCategories> DEEP_AETHER_COMBINEABLE_SEARCH = Suppliers.memoize(
      () -> RecipeBookCategories.valueOf("DEEP_AETHER_COMBINEABLE_SEARCH")
   );
   public static final Supplier<RecipeBookCategories> DEEP_AETHER_COMBINEABLE_FODDER = Suppliers.memoize(
      () -> RecipeBookCategories.valueOf("DEEP_AETHER_COMBINEABLE_FODDER")
   );
   public static final Supplier<RecipeBookCategories> DEEP_AETHER_COMBINEABLE_MISC = Suppliers.memoize(
      () -> RecipeBookCategories.valueOf("DEEP_AETHER_COMBINEABLE_MISC")
   );

   public static void registerRecipeCategories(RegisterRecipeBookCategoriesEvent event) {
      event.registerBookCategories(
         DARecipeBookTypes.COMBINER,
         ImmutableList.of(
            (RecipeBookCategories)DEEP_AETHER_COMBINEABLE_SEARCH.get(),
            (RecipeBookCategories)DEEP_AETHER_COMBINEABLE_FODDER.get(),
            (RecipeBookCategories)DEEP_AETHER_COMBINEABLE_MISC.get()
         )
      );
      event.registerAggregateCategory(
         (RecipeBookCategories)DEEP_AETHER_COMBINEABLE_SEARCH.get(),
         ImmutableList.of((RecipeBookCategories)DEEP_AETHER_COMBINEABLE_FODDER.get(), (RecipeBookCategories)DEEP_AETHER_COMBINEABLE_MISC.get())
      );
      event.registerRecipeCategoryFinder(
         (RecipeType)DARecipeTypes.COMBINING.get(),
         recipe -> {
            if (recipe.value() instanceof CombinerRecipe value) {
               return value.daCategory() == DABookCategory.COMBINEABLE_FODDER
                  ? (RecipeBookCategories)DEEP_AETHER_COMBINEABLE_FODDER.get()
                  : (RecipeBookCategories)DEEP_AETHER_COMBINEABLE_MISC.get();
            } else {
               return (RecipeBookCategories)DEEP_AETHER_COMBINEABLE_MISC.get();
            }
         }
      );
   }
}
