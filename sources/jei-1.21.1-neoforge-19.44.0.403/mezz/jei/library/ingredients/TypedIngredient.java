package mezz.jei.library.ingredients;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.ingredients.itemStacks.TypedItemStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public final class TypedIngredient<T> implements ITypedIngredient<T> {
   private final IIngredientType<T> ingredientType;
   private final T ingredient;

   private static <T> void checkParameters(IIngredientType<T> ingredientType, T ingredient) {
      Preconditions.checkNotNull(ingredientType, "ingredientType");
      Preconditions.checkNotNull(ingredient, "ingredient");
      Class<? extends T> ingredientClass = ingredientType.getIngredientClass();
      if (!ingredientClass.isInstance(ingredient)) {
         throw new IllegalArgumentException(
            "Invalid ingredient found.  Should be an instance of: " + ingredientClass + " Instead got: " + ingredient.getClass()
         );
      }
   }

   public static <T> ITypedIngredient<T> normalize(ITypedIngredient<T> typedIngredient, IIngredientHelper<T> ingredientHelper) {
      IIngredientType<T> type = typedIngredient.getType();
      if (type == VanillaTypes.ITEM_STACK) {
         ITypedIngredient<ItemStack> normalized = TypedItemStack.normalize(typedIngredient);
         return (ITypedIngredient<T>)normalized;
      } else {
         T ingredient = typedIngredient.getIngredient();
         T normalized = ingredientHelper.normalizeIngredient(ingredient);
         return createUnvalidated(type, normalized);
      }
   }

   public static <T> ITypedIngredient<T> createUnvalidated(IIngredientType<T> ingredientType, T ingredient) {
      return (ITypedIngredient<T>)(ingredientType == VanillaTypes.ITEM_STACK
         ? TypedItemStack.create((ItemStack)ingredient)
         : new TypedIngredient<>(ingredientType, ingredient));
   }

   @Nullable
   public static <T> ITypedIngredient<?> createAndFilterInvalid(IIngredientManager ingredientManager, @Nullable T ingredient, boolean normalize) {
      if (ingredient == null) {
         return null;
      } else {
         IIngredientType<T> type = ingredientManager.getIngredientType(ingredient);
         return type == null ? null : createAndFilterInvalid(ingredientManager, type, ingredient, normalize);
      }
   }

   @Nullable
   public static <T> ITypedIngredient<T> createAndFilterInvalid(
      IIngredientManager ingredientManager, IIngredientType<T> ingredientType, @Nullable T ingredient, boolean normalize
   ) {
      if (ingredient == null) {
         return null;
      } else {
         IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
         return createAndFilterInvalid(ingredientHelper, ingredientType, ingredient, normalize);
      }
   }

   public static <T> List<ITypedIngredient<T>> createAndFilterInvalidNonnullList(
      IIngredientManager ingredientManager, IIngredientType<T> ingredientType, Collection<T> ingredients, boolean normalize
   ) {
      IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
      List<ITypedIngredient<T>> results = new ArrayList<>(ingredients.size());

      for (T ingredient : ingredients) {
         ITypedIngredient<T> result = createAndFilterInvalid(ingredientHelper, ingredientType, ingredient, normalize);
         if (result != null) {
            results.add(result);
         }
      }

      return results;
   }

   public static <T> List<ITypedIngredient<T>> createAndFilterInvalidList(
      IIngredientManager ingredientManager, IIngredientType<T> ingredientType, List<T> ingredients, boolean normalize
   ) {
      IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
      List<ITypedIngredient<T>> results = new ArrayList<>(ingredients.size());

      for (T ingredient : ingredients) {
         ITypedIngredient<T> result = createAndFilterInvalid(ingredientHelper, ingredientType, ingredient, normalize);
         results.add(result);
      }

      return results;
   }

   public static List<ITypedIngredient<ItemStack>> createAndFilterInvalidList(IIngredientManager ingredientManager, Ingredient ingredient, boolean normalize) {
      ItemStack[] itemStacks = ingredient.getItems();
      IIngredientHelper<ItemStack> ingredientHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
      List<ITypedIngredient<ItemStack>> results = new ArrayList<>(itemStacks.length);

      for (ItemStack itemStack : itemStacks) {
         ITypedIngredient<ItemStack> result = createAndFilterInvalid(ingredientHelper, VanillaTypes.ITEM_STACK, itemStack, normalize);
         results.add(result);
      }

      return results;
   }

   @Nullable
   public static <T> ITypedIngredient<T> createAndFilterInvalid(
      IIngredientHelper<T> ingredientHelper, IIngredientType<T> ingredientType, @Nullable T ingredient, boolean normalize
   ) {
      if (ingredient == null) {
         return null;
      } else {
         try {
            if (normalize) {
               ingredient = ingredientHelper.normalizeIngredient(ingredient);
            }

            if (!ingredientHelper.isValidIngredient(ingredient)) {
               return null;
            }
         } catch (RuntimeException var6) {
            String ingredientInfo = ingredientHelper.getErrorInfo(ingredient);
            throw new IllegalArgumentException("Crashed when checking if ingredient is valid. Ingredient Info: " + ingredientInfo, var6);
         }

         return createUnvalidated(ingredientType, ingredient);
      }
   }

   @Nullable
   public static <T> ITypedIngredient<T> defensivelyCopyTypedIngredientFromApi(IIngredientManager ingredientManager, ITypedIngredient<T> value) {
      if (!(value instanceof TypedItemStack) && !(value instanceof TypedIngredient)) {
         IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(value.getType());
         T ingredient = ingredientHelper.copyIngredient(value.getIngredient());
         return createAndFilterInvalid(ingredientManager, value.getType(), ingredient, false);
      } else {
         return value;
      }
   }

   private TypedIngredient(IIngredientType<T> ingredientType, T ingredient) {
      checkParameters(ingredientType, ingredient);
      this.ingredientType = ingredientType;
      this.ingredient = ingredient;
   }

   @Override
   public T getIngredient() {
      return this.ingredient;
   }

   @Override
   public IIngredientType<T> getType() {
      return this.ingredientType;
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("type", this.ingredientType.getUid()).add("ingredient", this.ingredient).toString();
   }
}
