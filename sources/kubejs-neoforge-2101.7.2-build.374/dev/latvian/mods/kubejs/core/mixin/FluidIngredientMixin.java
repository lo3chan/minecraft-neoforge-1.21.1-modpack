package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.FluidIngredientKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.spongepowered.asm.mixin.Mixin;

@RemapPrefixForJS("kjs$")
@Mixin(
   value = {FluidIngredient.class},
   remap = false
)
public abstract class FluidIngredientMixin implements FluidIngredientKJS {
   @Override
   public FluidIngredient kjs$self() {
      return (FluidIngredient)this;
   }
}
