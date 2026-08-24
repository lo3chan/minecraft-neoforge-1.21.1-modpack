package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.rhino.Context;
import java.util.Arrays;
import java.util.List;
import me.shedaniel.rei.api.common.entry.comparison.FluidComparatorRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class REIRegisterFluidSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
   private final FluidComparatorRegistry registry;

   public REIRegisterFluidSubtypesKubeEvent(FluidComparatorRegistry registry) {
      this.registry = registry;
   }

   @Override
   public void register(Context cx, Object filter, SubtypeInterpreter interpreter) {
      FluidIngredient in = (FluidIngredient)RecipeViewerEntryType.FLUID.wrapPredicate(cx, filter);
      this.registry.register((ctx, stack) -> {
         Object result = interpreter.apply(stack);
         if (result == null) {
            return 0L;
         } else {
            return result instanceof Number n ? Double.doubleToLongBits(n.doubleValue()) : result.hashCode();
         }
      }, Arrays.stream(in.getStacks()).map(FluidStack::getFluid).toArray(Fluid[]::new));
   }

   @Override
   public void useComponents(Context cx, Object filter, List<DataComponentType<?>> components) {
      FluidIngredient in = (FluidIngredient)RecipeViewerEntryType.FLUID.wrapPredicate(cx, filter);
      this.registry.register(DataComponentComparator.of(components), Arrays.stream(in.getStacks()).map(FluidStack::getFluid).toArray(Fluid[]::new));
   }
}
