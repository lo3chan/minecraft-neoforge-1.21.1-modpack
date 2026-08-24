package fuzs.puzzleslib.api.item.v2.crafting;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.Objects;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public interface CombinedIngredients {
   CombinedIngredients INSTANCE = ProxyImpl.get().getCombinedIngredients();

   Ingredient all(Ingredient... var1);

   Ingredient any(Ingredient... var1);

   Ingredient difference(Ingredient var1, Ingredient var2);

   default Ingredient components(ItemLike item, DataComponentPatch components) {
      Objects.requireNonNull(item, "item is null");
      Objects.requireNonNull(components, "components is null");
      ItemStack itemStack = new ItemStack(item);
      itemStack.applyComponents(components);
      return this.components(itemStack);
   }

   Ingredient components(ItemStack var1);
}
