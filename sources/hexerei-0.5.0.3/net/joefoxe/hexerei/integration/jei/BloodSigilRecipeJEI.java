package net.joefoxe.hexerei.integration.jei;

import com.google.common.collect.Lists;
import java.util.List;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class BloodSigilRecipeJEI {
   private final ItemStack INPUT = new ItemStack((ItemLike)ModItems.BLOOD_SIGIL.get());
   private FluidStack OUTPUT_FLUID = new FluidStack(Fluids.WATER, 250);

   public BloodSigilRecipeJEI(FluidStack outputFluid) {
      this.OUTPUT_FLUID = outputFluid;
   }

   public ItemStack getInput() {
      return this.INPUT;
   }

   public FluidStack getOutputFluid() {
      return this.OUTPUT_FLUID;
   }

   public static List<BloodSigilRecipeJEI> getRecipeList() {
      List<BloodSigilRecipeJEI> recipeList = Lists.newArrayList();
      recipeList.add(new BloodSigilRecipeJEI(new FluidStack((Fluid)ModFluids.BLOOD_FLUID.get(), 250)));
      return recipeList;
   }
}
