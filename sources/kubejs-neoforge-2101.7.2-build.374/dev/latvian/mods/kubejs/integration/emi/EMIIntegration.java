package dev.latvian.mods.kubejs.integration.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class EMIIntegration {
   public static EmiStack fluid(FluidStack stack) {
      return EmiStack.of(stack.getFluid(), stack.getComponentsPatch(), stack.getAmount());
   }

   public static EmiIngredient fluidIngredient(FluidIngredient ingredient) {
      return EmiIngredient.of(Arrays.stream(ingredient.getStacks()).map(EMIIntegration::fluid).toList());
   }

   public static Predicate<EmiStack> predicate(ItemPredicate ingredient) {
      return emiStack -> {
         ItemStack is = emiStack.getItemStack();
         return !is.isEmpty() && ingredient.test(is);
      };
   }

   public static Predicate<EmiStack> predicate(FluidIngredient ingredient) {
      HashSet<EmiStack> set = new HashSet<>(Arrays.stream(ingredient.getStacks()).map(EMIIntegration::fluid).toList());
      return set::contains;
   }
}
