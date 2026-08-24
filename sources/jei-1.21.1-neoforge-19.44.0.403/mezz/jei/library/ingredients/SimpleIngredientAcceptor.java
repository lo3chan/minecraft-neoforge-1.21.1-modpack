package mezz.jei.library.ingredients;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.ingredients.itemStacks.TypedItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class SimpleIngredientAcceptor implements IIngredientAcceptor<SimpleIngredientAcceptor> {
   private final IIngredientManager ingredientManager;
   private final List<ITypedIngredient<?>> ingredients = new ArrayList<>();

   public SimpleIngredientAcceptor(IIngredientManager ingredientManager) {
      this.ingredientManager = ingredientManager;
   }

   public SimpleIngredientAcceptor addItemLike(ItemLike itemLike) {
      Preconditions.checkNotNull(itemLike, "itemLike");
      ITypedIngredient<ItemStack> ingredient = TypedItemStack.create(itemLike);
      this.ingredients.add(ingredient);
      return this;
   }

   public SimpleIngredientAcceptor addIngredientsUnsafe(List<?> ingredients) {
      Preconditions.checkNotNull(ingredients, "ingredients");

      for (Object ingredient : ingredients) {
         ITypedIngredient<?> typedIngredient = TypedIngredient.createAndFilterInvalid(this.ingredientManager, ingredient, false);
         if (typedIngredient != null) {
            this.ingredients.add(typedIngredient);
         }
      }

      return this;
   }

   public <T> SimpleIngredientAcceptor addIngredients(IIngredientType<T> ingredientType, List<T> ingredients) {
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      Preconditions.checkNotNull(ingredients, "ingredients");

      for (ITypedIngredient<T> typedIngredientOptional : TypedIngredient.createAndFilterInvalidList(this.ingredientManager, ingredientType, ingredients, false)) {
         if (typedIngredientOptional != null) {
            this.ingredients.add(typedIngredientOptional);
         }
      }

      return this;
   }

   public SimpleIngredientAcceptor addItemStack(ItemStack itemStack) {
      ErrorUtil.checkNotNull(itemStack, "itemStack");
      this.addIngredientInternal(VanillaTypes.ITEM_STACK, itemStack);
      return this;
   }

   public <T> SimpleIngredientAcceptor addIngredient(IIngredientType<T> ingredientType, T ingredient) {
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      this.addIngredientInternal(ingredientType, ingredient);
      return this;
   }

   public <I> SimpleIngredientAcceptor addTypedIngredient(ITypedIngredient<I> typedIngredient) {
      ErrorUtil.checkNotNull(typedIngredient, "typedIngredient");
      ITypedIngredient<I> copy = TypedIngredient.defensivelyCopyTypedIngredientFromApi(this.ingredientManager, typedIngredient);
      if (copy != null) {
         this.ingredients.add(copy);
      }

      return this;
   }

   public SimpleIngredientAcceptor addFluidStack(Fluid fluid) {
      IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
      return this.addFluidInternal(fluidHelper, fluid.builtInRegistryHolder(), fluidHelper.bucketVolume(), DataComponentPatch.EMPTY);
   }

   public SimpleIngredientAcceptor addFluidStack(Fluid fluid, long amount) {
      IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
      return this.addFluidInternal(fluidHelper, fluid.builtInRegistryHolder(), amount, DataComponentPatch.EMPTY);
   }

   public SimpleIngredientAcceptor addFluidStack(Fluid fluid, long amount, DataComponentPatch component) {
      IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
      return this.addFluidInternal(fluidHelper, fluid.builtInRegistryHolder(), amount, component);
   }

   private <T> SimpleIngredientAcceptor addFluidInternal(
      IPlatformFluidHelperInternal<T> fluidHelper, Holder<Fluid> fluidHolder, long amount, DataComponentPatch component
   ) {
      T fluidStack = fluidHelper.create(fluidHolder, amount, component);
      IIngredientTypeWithSubtypes<Fluid, T> fluidIngredientType = fluidHelper.getFluidIngredientType();
      this.addIngredientInternal(fluidIngredientType, fluidStack);
      return this;
   }

   public SimpleIngredientAcceptor addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
      ErrorUtil.checkNotNull(ingredients, "ingredients");

      for (ITypedIngredient<?> typedIngredient : ingredients) {
         this.addTypedIngredient(typedIngredient);
      }

      return this;
   }

   public SimpleIngredientAcceptor addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
      ErrorUtil.checkNotNull(ingredients, "ingredients");

      for (Optional<ITypedIngredient<?>> optionalTypedIngredient : ingredients) {
         if (optionalTypedIngredient.isPresent()) {
            this.ingredients.add(optionalTypedIngredient.get());
         }
      }

      return this;
   }

   public SimpleIngredientAcceptor addItemStacks(List<ItemStack> itemStacks) {
      return this.addIngredients(VanillaTypes.ITEM_STACK, itemStacks);
   }

   private <T> void addIngredientInternal(IIngredientType<T> ingredientType, @Nullable T ingredient) {
      if (ingredient != null) {
         ITypedIngredient<T> typedIngredient = TypedIngredient.createAndFilterInvalid(this.ingredientManager, ingredientType, ingredient, false);
         if (typedIngredient != null) {
            this.ingredients.add(typedIngredient);
         }
      }
   }

   @UnmodifiableView
   public List<ITypedIngredient<?>> getAllIngredients() {
      return Collections.unmodifiableList(this.ingredients);
   }
}
