package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import java.util.stream.Collector;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

public interface CustomIngredientKJS extends ItemPredicate {
   default Stream<ItemStack> getItems() {
      throw new NoMixinException();
   }

   @Override
   default boolean kjs$canBeUsedForMatching() {
      return false;
   }

   @NonExtendable
   @Override
   default Ingredient kjs$asIngredient() {
      return ((ICustomIngredient)this).toVanilla();
   }

   @NonExtendable
   @Override
   default ItemStack[] kjs$getStackArray() {
      return this.getItems().toArray(ItemStack[]::new);
   }

   @Override
   default ItemStackSet kjs$getDisplayStacks() {
      return this.getItems().collect(Collector.of(ItemStackSet::new, ItemStackSet::add, ItemStackSet::merge));
   }
}
