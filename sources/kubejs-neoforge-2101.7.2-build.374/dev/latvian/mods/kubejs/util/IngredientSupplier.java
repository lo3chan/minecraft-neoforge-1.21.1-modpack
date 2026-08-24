package dev.latvian.mods.kubejs.util;

import java.util.function.Supplier;
import net.minecraft.world.item.crafting.Ingredient;

@FunctionalInterface
public interface IngredientSupplier extends Supplier<Ingredient> {
   Ingredient get();
}
