package mezz.jei.api.gui.builder;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IIngredientConsumer {
   <I> IIngredientConsumer addIngredients(IIngredientType<I> var1, List<I> var2);

   <I> IIngredientConsumer addIngredient(IIngredientType<I> var1, I var2);

   IIngredientConsumer addIngredientsUnsafe(List<?> var1);

   default IIngredientConsumer addIngredients(Ingredient ingredient) {
      return this.addIngredients(VanillaTypes.ITEM_STACK, List.of(ingredient.getItems()));
   }

   default <I> IIngredientConsumer addTypedIngredient(ITypedIngredient<I> typedIngredient) {
      return this.addIngredient(typedIngredient.getType(), typedIngredient.getIngredient());
   }

   IIngredientConsumer addTypedIngredients(List<ITypedIngredient<?>> var1);

   IIngredientConsumer addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> var1);

   default IIngredientConsumer addItemStacks(List<ItemStack> itemStacks) {
      return this.addIngredients(VanillaTypes.ITEM_STACK, itemStacks);
   }

   default IIngredientConsumer addItemStack(ItemStack itemStack) {
      return this.addIngredient(VanillaTypes.ITEM_STACK, itemStack);
   }

   default IIngredientConsumer addItemLike(ItemLike itemLike) {
      return this.addItemStack(itemLike.asItem().getDefaultInstance());
   }

   IIngredientConsumer addFluidStack(Fluid var1);

   IIngredientConsumer addFluidStack(Fluid var1, long var2);

   IIngredientConsumer addFluidStack(Fluid var1, long var2, DataComponentPatch var4);
}
