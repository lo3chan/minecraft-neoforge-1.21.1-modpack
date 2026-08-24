package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

@RemapPrefixForJS("kjs$")
public interface FluidIngredientKJS extends WithCodec, FluidMatch {
   default FluidIngredient kjs$self() {
      throw new NoMixinException();
   }

   @Override
   default Codec<?> getCodec(Context cx) {
      return FluidIngredient.CODEC;
   }

   default SizedFluidIngredient kjs$withAmount(int amount) {
      return new SizedFluidIngredient(this.kjs$self(), amount);
   }

   @Override
   default boolean matches(RecipeMatchContext cx, FluidStack s, boolean exact) {
      return !s.isEmpty() && ((FluidIngredient)this).test(s);
   }

   @Override
   default boolean matches(RecipeMatchContext cx, FluidIngredient in, boolean exact) {
      if (in == FluidIngredient.empty()) {
         return false;
      } else {
         try {
            for (FluidStack stack : ((FluidIngredient)this).getStacks()) {
               if (in.test(stack)) {
                  return true;
               }
            }

            return false;
         } catch (Exception var8) {
            throw new KubeRuntimeException("Failed to test fluid ingredient " + in, var8);
         }
      }
   }
}
