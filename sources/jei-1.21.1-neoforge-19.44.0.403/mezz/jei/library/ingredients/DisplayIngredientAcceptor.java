package mezz.jei.library.ingredients;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.ingredients.itemStacks.TypedItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class DisplayIngredientAcceptor implements IIngredientAcceptor<DisplayIngredientAcceptor> {
   private final IIngredientManager ingredientManager;
   private final List<ITypedIngredient<?>> ingredients = new ArrayList<>();

   public DisplayIngredientAcceptor(IIngredientManager ingredientManager) {
      this.ingredientManager = ingredientManager;
   }

   public DisplayIngredientAcceptor addIngredientsUnsafe(List<?> ingredients) {
      Preconditions.checkNotNull(ingredients, "ingredients");

      for (Object ingredient : ingredients) {
         ITypedIngredient<?> typedIngredient = TypedIngredient.createAndFilterInvalid(this.ingredientManager, ingredient, false);
         this.ingredients.add(typedIngredient);
      }

      return this;
   }

   public DisplayIngredientAcceptor addItemStack(ItemStack itemStack) {
      ErrorUtil.checkNotNull(itemStack, "itemStack");
      this.addIngredientInternal(VanillaTypes.ITEM_STACK, itemStack);
      return this;
   }

   public DisplayIngredientAcceptor addItemStacks(List<ItemStack> itemStacks) {
      return this.addIngredients(VanillaTypes.ITEM_STACK, itemStacks);
   }

   public <T> DisplayIngredientAcceptor addIngredients(IIngredientType<T> ingredientType, List<T> ingredients) {
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      Preconditions.checkNotNull(ingredients, "ingredients");
      List<ITypedIngredient<T>> typedIngredients = TypedIngredient.createAndFilterInvalidList(this.ingredientManager, ingredientType, ingredients, false);
      this.ingredients.addAll(typedIngredients);
      return this;
   }

   public DisplayIngredientAcceptor addIngredients(Ingredient ingredient) {
      Preconditions.checkNotNull(ingredient, "ingredient");
      List<ITypedIngredient<ItemStack>> typedIngredients = TypedIngredient.createAndFilterInvalidList(this.ingredientManager, ingredient, false);
      this.ingredients.addAll(typedIngredients);
      return this;
   }

   public <T> DisplayIngredientAcceptor addIngredient(IIngredientType<T> ingredientType, T ingredient) {
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      this.addIngredientInternal(ingredientType, ingredient);
      return this;
   }

   public <I> DisplayIngredientAcceptor addTypedIngredient(ITypedIngredient<I> typedIngredient) {
      ErrorUtil.checkNotNull(typedIngredient, "typedIngredient");
      ITypedIngredient<I> copy = TypedIngredient.defensivelyCopyTypedIngredientFromApi(this.ingredientManager, typedIngredient);
      this.ingredients.add(copy);
      return this;
   }

   public DisplayIngredientAcceptor addItemLike(ItemLike itemLike) {
      Preconditions.checkNotNull(itemLike, "itemLike");
      ITypedIngredient<ItemStack> ingredient = TypedItemStack.create(itemLike);
      this.ingredients.add(ingredient);
      return this;
   }

   public DisplayIngredientAcceptor addFluidStack(Fluid fluid) {
      IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
      return this.addFluidInternal(fluidHelper, fluid.builtInRegistryHolder(), fluidHelper.bucketVolume(), DataComponentPatch.EMPTY);
   }

   public DisplayIngredientAcceptor addFluidStack(Fluid fluid, long amount) {
      IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
      return this.addFluidInternal(fluidHelper, fluid.builtInRegistryHolder(), amount, DataComponentPatch.EMPTY);
   }

   public DisplayIngredientAcceptor addFluidStack(Fluid fluid, long amount, DataComponentPatch componentPatch) {
      IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
      return this.addFluidInternal(fluidHelper, fluid.builtInRegistryHolder(), amount, componentPatch);
   }

   private <T> DisplayIngredientAcceptor addFluidInternal(IPlatformFluidHelperInternal<T> fluidHelper, Holder<Fluid> fluid, long amount, DataComponentPatch tag) {
      T fluidStack = fluidHelper.create(fluid, amount, tag);
      IIngredientTypeWithSubtypes<Fluid, T> fluidIngredientType = fluidHelper.getFluidIngredientType();
      this.addIngredientInternal(fluidIngredientType, fluidStack);
      return this;
   }

   public DisplayIngredientAcceptor addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
      ErrorUtil.checkNotNull(ingredients, "ingredients");

      for (ITypedIngredient<?> typedIngredient : ingredients) {
         this.addTypedIngredient(typedIngredient);
      }

      return this;
   }

   public DisplayIngredientAcceptor addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
      ErrorUtil.checkNotNull(ingredients, "ingredients");

      for (Optional<ITypedIngredient<?>> o : ingredients) {
         if (o.isPresent()) {
            this.addTypedIngredient(o.get());
         } else {
            this.ingredients.add(null);
         }
      }

      return this;
   }

   private <T> void addIngredientInternal(IIngredientType<T> ingredientType, @Nullable T ingredient) {
      ITypedIngredient<T> result = TypedIngredient.createAndFilterInvalid(this.ingredientManager, ingredientType, ingredient, false);
      this.ingredients.add(result);
   }

   @UnmodifiableView
   public List<ITypedIngredient<?>> getAllIngredients() {
      return Collections.unmodifiableList(this.ingredients);
   }

   public IntSet getMatches(IFocusGroup focusGroup, RecipeIngredientRole role) {
      List<IFocus<?>> focuses = focusGroup.getFocuses(role).toList();
      IntSet results = new IntOpenHashSet();

      for (IFocus<?> focus : focuses) {
         this.getMatches(focus, results);
      }

      return results;
   }

   private <T> void getMatches(IFocus<T> focus, IntSet results) {
      List<ITypedIngredient<?>> ingredients = this.getAllIngredients();
      if (!ingredients.isEmpty()) {
         ITypedIngredient<T> focusValue = focus.getTypedValue();
         IIngredientType<T> ingredientType = focusValue.getType();
         IIngredientHelper<T> ingredientHelper = this.ingredientManager.getIngredientHelper(ingredientType);
         Object focusUid = ingredientHelper.getUid(focusValue, UidContext.Ingredient);

         for (int i = 0; i < ingredients.size(); i++) {
            ITypedIngredient<?> typedIngredient = ingredients.get(i);
            if (typedIngredient != null) {
               ITypedIngredient<T> ingredient = typedIngredient.cast(ingredientType);
               if (ingredient != null) {
                  Object uniqueId = ingredientHelper.getUid(ingredient, UidContext.Ingredient);
                  if (focusUid.equals(uniqueId)) {
                     results.add(i);
                  }
               }
            }
         }
      }
   }
}
