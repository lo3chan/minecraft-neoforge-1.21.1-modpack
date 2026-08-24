package net.blay09.mods.balm.neoforge.fluid;

import net.blay09.mods.balm.api.fluid.FluidTank;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

public class NeoForgeFluidTank implements IFluidHandler {
   private final FluidTank fluidTank;

   public NeoForgeFluidTank(FluidTank fluidTank) {
      this.fluidTank = fluidTank;
   }

   public int getTanks() {
      return 1;
   }

   @NotNull
   public FluidStack getFluidInTank(int tank) {
      return new FluidStack(this.fluidTank.getFluid(), this.fluidTank.getAmount());
   }

   public int getTankCapacity(int tank) {
      return this.fluidTank.getCapacity();
   }

   public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
      return this.fluidTank.canFill(stack.getFluid());
   }

   public int fill(FluidStack resource, FluidAction action) {
      return this.fluidTank.fill(resource.getFluid(), resource.getAmount(), action.simulate());
   }

   @NotNull
   public FluidStack drain(FluidStack resource, FluidAction action) {
      int drained = this.fluidTank.drain(resource.getFluid(), resource.getAmount(), action.simulate());
      return new FluidStack(this.fluidTank.getFluid(), drained);
   }

   @NotNull
   public FluidStack drain(int maxDrain, FluidAction action) {
      int drained = this.fluidTank.drain(this.fluidTank.getFluid(), maxDrain, action.simulate());
      return new FluidStack(this.fluidTank.getFluid(), drained);
   }
}
