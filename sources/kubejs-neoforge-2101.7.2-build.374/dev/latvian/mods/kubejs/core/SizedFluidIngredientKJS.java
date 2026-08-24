package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

@RemapPrefixForJS("kjs$")
public interface SizedFluidIngredientKJS extends Replaceable, FluidMatch {
   default SizedFluidIngredient kjs$self() {
      return (SizedFluidIngredient)this;
   }

   @Override
   default Object replaceThisWith(RecipeScriptContext cx, Object with) {
      FluidIngredient ingredient = FluidWrapper.wrapIngredient(cx.cx(), with);
      return !ingredient.equals(this.kjs$self().ingredient()) ? new SizedFluidIngredient(ingredient, this.kjs$self().amount()) : this;
   }

   default JsonElement kjs$toFlatJson() {
      return KubeJSCodecs.toJsonOrThrow(this.kjs$self(), SizedFluidIngredient.FLAT_CODEC);
   }

   default JsonElement kjs$toNestedJson() {
      return KubeJSCodecs.toJsonOrThrow(this.kjs$self(), SizedFluidIngredient.NESTED_CODEC);
   }

   @Override
   default boolean matches(RecipeMatchContext cx, FluidStack s, boolean exact) {
      return ((FluidMatch)this.kjs$self().ingredient()).matches(cx, s, exact);
   }

   @Override
   default boolean matches(RecipeMatchContext cx, FluidIngredient in, boolean exact) {
      return ((FluidMatch)this.kjs$self().ingredient()).matches(cx, in, exact);
   }
}
