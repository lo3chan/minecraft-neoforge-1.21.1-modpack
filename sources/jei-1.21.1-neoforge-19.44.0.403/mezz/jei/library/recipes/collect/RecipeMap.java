package mezz.jei.library.recipes.collect;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientSupplier;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import org.jetbrains.annotations.UnmodifiableView;

public class RecipeMap {
   private final RecipeIngredientTable recipeTable = new RecipeIngredientTable();
   private final Multimap<Object, RecipeType<?>> ingredientUidToCategoryMap = Multimaps.newSetMultimap(
      new Object2ObjectOpenHashMap(), () -> new ObjectOpenHashSet(2)
   );
   private final Multimap<Object, RecipeType<?>> categoryCatalystUidToRecipeCategoryMap = Multimaps.newSetMultimap(
      new Object2ObjectOpenHashMap(), ObjectOpenHashSet::new
   );
   private final Comparator<RecipeType<?>> recipeTypeComparator;
   private final IIngredientManager ingredientManager;
   private final RecipeIngredientRole role;

   public RecipeMap(Comparator<RecipeType<?>> recipeTypeComparator, IIngredientManager ingredientManager, RecipeIngredientRole role) {
      this.recipeTypeComparator = recipeTypeComparator;
      this.ingredientManager = ingredientManager;
      this.role = role;
   }

   public <T> Stream<RecipeType<?>> getRecipeTypes(ITypedIngredient<T> ingredient) {
      Object ingredientUid = this.getIngredientUid(ingredient);
      Collection<RecipeType<?>> recipeCategoryUids = this.ingredientUidToCategoryMap.get(ingredientUid);
      Collection<RecipeType<?>> catalystRecipeCategoryUids = this.categoryCatalystUidToRecipeCategoryMap.get(ingredientUid);
      return Stream.concat(recipeCategoryUids.stream(), catalystRecipeCategoryUids.stream()).sorted(this.recipeTypeComparator);
   }

   public <T> void addCatalystForCategory(RecipeType<?> recipeType, ITypedIngredient<T> ingredient) {
      Object ingredientUid = this.getIngredientUid(ingredient);
      this.categoryCatalystUidToRecipeCategoryMap.put(ingredientUid, recipeType);
   }

   @UnmodifiableView
   public <T> List<T> getRecipes(RecipeType<T> recipeType, ITypedIngredient<?> ingredient) {
      Object ingredientUid = this.getIngredientUid(ingredient);
      return this.recipeTable.get(recipeType, ingredientUid);
   }

   public <T> boolean isCatalystForRecipeCategory(RecipeType<T> recipeType, ITypedIngredient<?> ingredient) {
      Object ingredientUid = this.getIngredientUid(ingredient);
      Collection<RecipeType<?>> catalystCategories = this.categoryCatalystUidToRecipeCategoryMap.get(ingredientUid);
      return catalystCategories.contains(recipeType);
   }

   public <T> void addRecipe(RecipeType<T> recipeType, T recipe, IIngredientSupplier ingredientSupplier) {
      Set<Object> ingredientUids = new HashSet<>();

      for (ITypedIngredient<?> ingredient : ingredientSupplier.getIngredients(this.role)) {
         Object ingredientUid = this.getIngredientUid(ingredient);
         ingredientUids.add(ingredientUid);
      }

      if (!ingredientUids.isEmpty()) {
         for (Object ingredientUid : ingredientUids) {
            this.ingredientUidToCategoryMap.put(ingredientUid, recipeType);
         }

         this.recipeTable.add(recipe, recipeType, ingredientUids);
      }
   }

   public void compact() {
      this.recipeTable.compact();
   }

   private <T> Object getIngredientUid(ITypedIngredient<T> typedIngredient) {
      IIngredientType<T> type = typedIngredient.getType();
      IIngredientHelper<T> ingredientHelper = this.ingredientManager.getIngredientHelper(type);
      return ingredientHelper.getUid(typedIngredient, UidContext.Recipe);
   }
}
