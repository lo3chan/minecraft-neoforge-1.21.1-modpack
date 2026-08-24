package mezz.jei.api.registration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ISubtypeRegistration {
   <B, I> void registerSubtypeInterpreter(IIngredientTypeWithSubtypes<B, I> var1, B var2, ISubtypeInterpreter<I> var3);

   default void registerSubtypeInterpreter(Item item, ISubtypeInterpreter<ItemStack> interpreter) {
      this.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, interpreter);
   }

   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   <B, I> void registerSubtypeInterpreter(IIngredientTypeWithSubtypes<B, I> var1, B var2, IIngredientSubtypeInterpreter<I> var3);

   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   default void registerSubtypeInterpreter(Item item, IIngredientSubtypeInterpreter<ItemStack> interpreter) {
      this.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, interpreter);
   }
}
