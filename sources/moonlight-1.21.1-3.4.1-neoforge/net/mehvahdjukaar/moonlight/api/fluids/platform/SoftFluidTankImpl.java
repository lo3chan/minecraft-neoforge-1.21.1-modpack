package net.mehvahdjukaar.moonlight.api.fluids.platform;

import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidTank;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

@Deprecated
public class SoftFluidTankImpl extends SoftFluidTank {
   public static SoftFluidTank create(int capacity, HolderGetter<SoftFluid> registries) {
      return new SoftFluidTankImpl(capacity, registries);
   }

   @Deprecated(
      forRemoval = true
   )
   protected SoftFluidTankImpl(int capacity) {
      super(capacity, SoftFluidRegistry.get(Utils.hackyGetRegistryAccess()).asLookup());
   }

   protected SoftFluidTankImpl(int capacity, HolderGetter<SoftFluid> registries) {
      super(capacity, registries);
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean addVanillaFluid(FluidStack fluidStack) {
      SoftFluidStack s = SoftFluidStackImpl.fromForgeFluid(fluidStack, Utils.hackyGetRegistryAccess());
      return s.isEmpty() ? false : this.addFluid(s, false) == s.getCount();
   }

   public boolean transferToFluidTank(IFluidHandler fluidDestination, int bottles) {
      if (!this.isEmpty() && this.getFluidCount() >= bottles) {
         FluidStack stack = ((SoftFluidStackImpl)this.fluidStack).toForgeFluid();
         int milliBuckets = stack.getAmount();
         if (!stack.isEmpty()) {
            int fillableAmount = fluidDestination.fill(stack, FluidAction.SIMULATE);
            if (fillableAmount == milliBuckets) {
               fluidDestination.fill(stack, FluidAction.EXECUTE);
               this.fluidStack.shrink(bottles);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean transferToFluidTank(IFluidHandler fluidDestination) {
      return this.transferToFluidTank(fluidDestination, 1);
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean drainFluidTank(IFluidHandler fluidSource, int bottles) {
      return this.drainFluidTank(fluidSource, bottles, Utils.hackyGetRegistryAccess());
   }

   public boolean drainFluidTank(IFluidHandler fluidSource, int bottles, Provider ra) {
      if (this.getSpace() < bottles) {
         return false;
      } else {
         int milliBuckets = SoftFluidStackImpl.bottlesToMB(bottles);
         FluidStack drainable = fluidSource.drain(milliBuckets, FluidAction.SIMULATE);
         if (!drainable.isEmpty() && drainable.getAmount() == milliBuckets) {
            boolean transfer = false;
            if (this.fluidStack.isEmpty()) {
               this.setFluid(drainable, ra);
               transfer = true;
            } else if (((SoftFluidStackImpl)this.fluidStack).isFluidEqual(drainable, ra)) {
               transfer = true;
            }

            if (transfer) {
               fluidSource.drain(milliBuckets, FluidAction.EXECUTE);
               return true;
            }
         }

         return false;
      }
   }

   public boolean drainFluidTank(IFluidHandler fluidSource, Provider ra) {
      return this.drainFluidTank(fluidSource, 1, ra);
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean drainFluidTank(IFluidHandler fluidSource) {
      return this.drainFluidTank(fluidSource, 1, Utils.hackyGetRegistryAccess());
   }

   public void copy(IFluidHandler other, Provider ra) {
      FluidStack forgeFluid = other.getFluidInTank(0).copy();
      this.setFluid(forgeFluid, ra);
      this.capCapacity();
   }

   @Deprecated(
      forRemoval = true
   )
   public void copy(IFluidHandler other) {
      this.copy(other, Utils.hackyGetRegistryAccess());
   }

   @Deprecated(
      forRemoval = true
   )
   public void setFluid(FluidStack fluidStack) {
      this.setFluid(SoftFluidStackImpl.fromForgeFluid(fluidStack, Utils.hackyGetRegistryAccess()));
   }

   public void setFluid(FluidStack fluidStack, Provider ra) {
      this.setFluid(SoftFluidStackImpl.fromForgeFluid(fluidStack, ra));
   }
}
