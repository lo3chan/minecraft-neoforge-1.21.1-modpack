package mezz.jei.api.gui.builder;

import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

public interface IClickableIngredientFactory {
   default IClickableIngredientFactory.IBuilder<ItemStack> createBuilder(ItemStack itemStack) {
      return this.createBuilder(VanillaTypes.ITEM_STACK, itemStack);
   }

   <T> IClickableIngredientFactory.IBuilder<T> createBuilder(ITypedIngredient<T> var1);

   <T> IClickableIngredientFactory.IBuilder<T> createBuilder(IIngredientType<T> var1, T var2);

   public interface IBuilder<T> {
      Optional<IClickableIngredient<T>> buildWithArea(int var1, int var2, int var3, int var4);

      Optional<IClickableIngredient<T>> buildWithArea(Rect2i var1);
   }
}
