package mezz.jei.library.load.registration;

import java.util.List;
import java.util.stream.Stream;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.advanced.IRecipeManagerPluginHelper;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;

public class TypedRecipeManagerPluginAdapter<T> implements IRecipeManagerPlugin {
   private final IRecipeManagerPluginHelper helper;
   private final RecipeType<T> recipeType;
   private final ISimpleRecipeManagerPlugin<T> plugin;

   public TypedRecipeManagerPluginAdapter(IRecipeManagerPluginHelper helper, RecipeType<T> recipeType, ISimpleRecipeManagerPlugin<T> plugin) {
      this.helper = helper;
      this.recipeType = recipeType;
      this.plugin = plugin;
   }

   @Override
   public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
      return this.isHandled(focus) ? List.of(this.recipeType) : List.of();
   }

   private boolean isHandled(IFocus<?> focus) {
      if (this.helper.isRecipeCatalyst(this.recipeType, focus)) {
         return true;
      } else {
         switch (focus.getRole()) {
            case INPUT:
               if (this.plugin.isHandledInput(focus.getTypedValue())) {
                  return true;
               }
               break;
            case OUTPUT:
               if (this.plugin.isHandledOutput(focus.getTypedValue())) {
                  return true;
               }
               break;
            case CATALYST:
               if (this.helper.isRecipeCatalyst(this.recipeType, focus)) {
                  return true;
               }
         }

         return false;
      }
   }

   @Override
   public <T2, V> List<T2> getRecipes(IRecipeCategory<T2> recipeCategory, IFocus<V> focus) {
      return (List<T2>)(recipeCategory.getRecipeType().equals(this.recipeType) && this.isHandled(focus) ? this.getRecipes(focus) : List.of());
   }

   private List<T> getRecipes(IFocus<?> focus) {
      if (!this.isHandled(focus)) {
         return List.of();
      } else {
         switch (focus.getRole()) {
            case INPUT:
               List<T> recipesForInput = this.plugin.getRecipesForInput(focus.getTypedValue());
               if (this.helper.isRecipeCatalyst(this.recipeType, focus)) {
                  return Stream.concat(recipesForInput.stream(), this.plugin.getAllRecipes().stream()).distinct().toList();
               }

               return recipesForInput;
            case OUTPUT:
               List<T> recipesForOutput = this.plugin.getRecipesForOutput(focus.getTypedValue());
               if (this.helper.isRecipeCatalyst(this.recipeType, focus)) {
                  return Stream.concat(recipesForOutput.stream(), this.plugin.getAllRecipes().stream()).distinct().toList();
               }

               return recipesForOutput;
            case CATALYST:
               if (this.helper.isRecipeCatalyst(this.recipeType, focus)) {
                  return this.plugin.getAllRecipes();
               }

               return List.of();
            default:
               return List.of();
         }
      }
   }

   @Override
   public <T2> List<T2> getRecipes(IRecipeCategory<T2> recipeCategory) {
      return (List<T2>)(recipeCategory.getRecipeType().equals(this.recipeType) ? this.plugin.getAllRecipes() : List.of());
   }
}
