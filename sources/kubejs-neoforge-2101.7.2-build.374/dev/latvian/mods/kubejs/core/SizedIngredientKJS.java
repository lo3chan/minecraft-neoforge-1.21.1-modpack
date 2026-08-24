package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public interface SizedIngredientKJS extends Replaceable, IngredientSupplierKJS, ItemMatch {
   default SizedIngredient kjs$self() {
      return (SizedIngredient)this;
   }

   @Override
   default Object replaceThisWith(RecipeScriptContext cx, Object with) {
      Ingredient ingredient = IngredientWrapper.wrap(cx.cx(), with);
      return !ingredient.equals(this.kjs$self().ingredient()) ? new SizedIngredient(ingredient, this.kjs$self().count()) : this;
   }

   @Override
   default Ingredient kjs$asIngredient() {
      return this.kjs$self().ingredient();
   }

   @Override
   default boolean matches(RecipeMatchContext cx, ItemStack item, boolean exact) {
      return this.kjs$self().ingredient().matches(cx, item, exact);
   }

   @Override
   default boolean matches(RecipeMatchContext cx, Ingredient in, boolean exact) {
      return this.kjs$self().ingredient().matches(cx, in, exact);
   }

   default JsonElement kjs$toFlatJson() {
      return KubeJSCodecs.toJsonOrThrow(this.kjs$self(), SizedIngredient.FLAT_CODEC);
   }

   default JsonElement kjs$toNestedJson() {
      return KubeJSCodecs.toJsonOrThrow(this.kjs$self(), SizedIngredient.NESTED_CODEC);
   }
}
