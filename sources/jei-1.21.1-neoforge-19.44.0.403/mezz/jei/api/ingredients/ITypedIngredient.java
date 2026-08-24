package mezz.jei.api.ingredients;

import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface ITypedIngredient<T> {
   IIngredientType<T> getType();

   T getIngredient();

   default <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
      return ingredientType.castIngredient(this.getIngredient());
   }

   @Nullable
   default <V> V getCastIngredient(IIngredientType<V> ingredientType) {
      return ingredientType.getCastIngredient(this.getIngredient());
   }

   @Nullable
   default <V> ITypedIngredient<V> cast(IIngredientType<V> ingredientType) {
      return (ITypedIngredient<V>)(this.getType().equals(ingredientType) ? this : null);
   }

   @Nullable
   default ITypedIngredient<ItemStack> castToItemStackType() {
      return this.cast(VanillaTypes.ITEM_STACK);
   }

   default <B> B getBaseIngredient(IIngredientTypeWithSubtypes<B, T> ingredientType) {
      return ingredientType.getBase(this.getIngredient());
   }

   default Optional<ItemStack> getItemStack() {
      return this.getIngredient(VanillaTypes.ITEM_STACK);
   }
}
