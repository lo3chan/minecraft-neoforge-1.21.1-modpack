package net.mehvahdjukaar.moonlight.api.fluids.neoforge;

import java.util.Objects;
import net.mehvahdjukaar.moonlight.api.block.ISoftFluidTankProvider;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidTank;
import net.mehvahdjukaar.moonlight.api.fluids.platform.SoftFluidStackImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

@Deprecated(
   forRemoval = true
)
public record SoftFluidTankFluidHandlerWrapper(SoftFluidTank tank, BlockEntity be) implements IFluidHandler {
   public static <T extends BlockEntity & ISoftFluidTankProvider> SoftFluidTankFluidHandlerWrapper wrap(T be) {
      return new SoftFluidTankFluidHandlerWrapper(be.getSoftFluidTank(), be);
   }

   public int getTanks() {
      return 1;
   }

   @NotNull
   public FluidStack getFluidInTank(int i) {
      return SoftFluidStackImpl.toForgeFluid(this.tank.getFluid());
   }

   public int getTankCapacity(int i) {
      return SoftFluidStackImpl.bottlesToMB(this.tank.getCapacity());
   }

   public boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
      return this.tank.isFluidCompatible(SoftFluidStackImpl.fromForgeFluid(fluidStack, Objects.requireNonNull(this.be.getLevel()).registryAccess()));
   }

   public int fill(FluidStack fluidStack, FluidAction fluidAction) {
      RegistryAccess ra = Objects.requireNonNull(this.be.getLevel()).registryAccess();
      SoftFluidStack original = SoftFluidStackImpl.fromForgeFluid(fluidStack, ra);
      int filled = this.tank.addFluid(original, fluidAction.simulate());
      if (!fluidAction.simulate()) {
         int bottlesRemoved = SoftFluidStackImpl.fromForgeFluid(fluidStack, ra).getCount() - original.getCount();
         fluidStack.shrink(SoftFluidStackImpl.bottlesToMB(bottlesRemoved));
         this.be.setChanged();
      }

      return filled;
   }

   @NotNull
   public FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
      return this.drain(fluidStack.getAmount(), fluidAction);
   }

   @NotNull
   public FluidStack drain(int i, FluidAction fluidAction) {
      SoftFluidStack drained = this.tank.removeFluid(i, fluidAction.simulate());
      if (!fluidAction.simulate()) {
         this.be.setChanged();
      }

      return SoftFluidStackImpl.toForgeFluid(drained);
   }
}
