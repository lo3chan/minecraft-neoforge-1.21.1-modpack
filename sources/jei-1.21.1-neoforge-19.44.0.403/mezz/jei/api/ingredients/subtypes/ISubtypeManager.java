package mezz.jei.api.ingredients.subtypes;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ISubtypeManager {
   @Nullable
   default Object getSubtypeData(ItemStack ingredient, UidContext context) {
      return this.getSubtypeData(VanillaTypes.ITEM_STACK, ingredient, context);
   }

   @Nullable
   <T> Object getSubtypeData(IIngredientTypeWithSubtypes<?, T> var1, T var2, UidContext var3);

   @Nullable
   <B, T> Object getSubtypeData(IIngredientTypeWithSubtypes<B, T> var1, ITypedIngredient<T> var2, UidContext var3);

   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   default String getSubtypeInfo(ItemStack ingredient, UidContext context) {
      return this.getSubtypeInfo(VanillaTypes.ITEM_STACK, ingredient, context);
   }

   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   <T> String getSubtypeInfo(IIngredientTypeWithSubtypes<?, T> var1, T var2, UidContext var3);

   default boolean hasSubtypes(ItemStack ingredient) {
      return this.hasSubtypes(VanillaTypes.ITEM_STACK, ingredient);
   }

   <T, B> boolean hasSubtypes(IIngredientTypeWithSubtypes<B, T> var1, T var2);
}
