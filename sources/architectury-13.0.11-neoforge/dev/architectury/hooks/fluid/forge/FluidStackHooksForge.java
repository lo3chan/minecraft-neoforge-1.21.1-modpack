package dev.architectury.hooks.fluid.forge;

import dev.architectury.fluid.FluidStack;
import dev.architectury.fluid.forge.FluidStackImpl;

public final class FluidStackHooksForge {
   private FluidStackHooksForge() {
   }

   public static FluidStack fromForge(net.neoforged.neoforge.fluids.FluidStack stack) {
      return FluidStackImpl.fromValue.apply(stack);
   }

   public static net.neoforged.neoforge.fluids.FluidStack toForge(FluidStack stack) {
      return (net.neoforged.neoforge.fluids.FluidStack)FluidStackImpl.toValue.apply(stack);
   }
}
