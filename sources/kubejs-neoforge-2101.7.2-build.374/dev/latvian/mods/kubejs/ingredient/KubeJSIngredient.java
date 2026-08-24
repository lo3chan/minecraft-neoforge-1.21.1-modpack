package dev.latvian.mods.kubejs.ingredient;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

public interface KubeJSIngredient extends ICustomIngredient, ItemPredicate {
   @Override
   boolean test(ItemStack stack);

   default Stream<ItemStack> getItems() {
      return ItemWrapper.getList().stream().filter(this);
   }

   default boolean isSimple() {
      return CommonProperties.get().serverOnly;
   }

   @Override
   default boolean kjs$canBeUsedForMatching() {
      return true;
   }
}
