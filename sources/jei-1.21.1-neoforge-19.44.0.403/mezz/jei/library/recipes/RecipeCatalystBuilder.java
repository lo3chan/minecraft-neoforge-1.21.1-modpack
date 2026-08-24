package mezz.jei.library.recipes;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.library.recipes.collect.RecipeMap;

public class RecipeCatalystBuilder {
   private final Builder<IRecipeCategory<?>, ITypedIngredient<?>> recipeCategoryCatalystsBuilder = ImmutableListMultimap.builder();
   private final RecipeMap recipeCatalystMap;

   public RecipeCatalystBuilder(RecipeMap recipeCatalystMap) {
      this.recipeCatalystMap = recipeCatalystMap;
   }

   public void addCategoryCatalysts(IRecipeCategory<?> recipeCategory, List<ITypedIngredient<?>> categoryCatalystIngredients) {
      this.recipeCategoryCatalystsBuilder.putAll(recipeCategory, categoryCatalystIngredients);

      for (ITypedIngredient<?> catalystIngredient : categoryCatalystIngredients) {
         this.addCategoryCatalyst(catalystIngredient, recipeCategory);
      }
   }

   private <T> void addCategoryCatalyst(ITypedIngredient<T> catalystIngredient, IRecipeCategory<?> recipeCategory) {
      RecipeType<?> recipeType = recipeCategory.getRecipeType();
      this.recipeCatalystMap.addCatalystForCategory(recipeType, catalystIngredient);
   }

   public ImmutableListMultimap<IRecipeCategory<?>, ITypedIngredient<?>> buildRecipeCategoryCatalysts() {
      return this.recipeCategoryCatalystsBuilder.build();
   }
}
