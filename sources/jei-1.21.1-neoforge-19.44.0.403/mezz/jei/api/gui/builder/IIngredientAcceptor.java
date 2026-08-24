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
public interface IIngredientAcceptor<THIS extends IIngredientAcceptor<THIS>> extends IIngredientConsumer {
   <I> THIS addIngredients(IIngredientType<I> var1, List<I> var2);

   <I> THIS addIngredient(IIngredientType<I> var1, I var2);

   THIS addIngredientsUnsafe(List<?> var1);

   default THIS addIngredients(Ingredient ingredient) {
      return this.addIngredients(VanillaTypes.ITEM_STACK, List.of(ingredient.getItems()));
   }

   default <I> THIS addTypedIngredient(ITypedIngredient<I> typedIngredient) {
      return this.addIngredient(typedIngredient.getType(), typedIngredient.getIngredient());
   }

   THIS addTypedIngredients(List<ITypedIngredient<?>> var1);

   THIS addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> var1);

   default THIS addItemStacks(List<ItemStack> itemStacks) {
      return this.addIngredients(VanillaTypes.ITEM_STACK, itemStacks);
   }

   default THIS addItemStack(ItemStack itemStack) {
      return this.addIngredient(VanillaTypes.ITEM_STACK, itemStack);
   }

   @Override
   default IIngredientConsumer addItemLike(ItemLike itemLike) {
      return this.addItemStack(itemLike.asItem().getDefaultInstance());
   }

   THIS addFluidStack(Fluid var1);

   THIS addFluidStack(Fluid var1, long var2);

   THIS addFluidStack(Fluid var1, long var2, DataComponentPatch var4);
}
