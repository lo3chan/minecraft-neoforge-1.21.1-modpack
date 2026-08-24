package net.mehvahdjukaar.moonlight.api.fluids.platform;

import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.fluids.FluidOffer;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;

public class FluidsHelperImpl {
   public static boolean extractFluidFromTank(BlockEntity tileBack, Direction dir, int amount) {
      IFluidHandler handlerBack = (IFluidHandler)tileBack.getLevel()
         .getCapability(FluidHandler.BLOCK, tileBack.getBlockPos(), tileBack.getBlockState(), tileBack, dir);
      if (handlerBack != null) {
         if (handlerBack.drain(250 * amount, FluidAction.SIMULATE).getAmount() != 250 * amount) {
            return false;
         } else {
            handlerBack.drain(250 * amount, FluidAction.EXECUTE);
            tileBack.setChanged();
            return true;
         }
      } else {
         return false;
      }
   }

   public static Integer fillFluidTank(BlockEntity tileBelow, FluidOffer offer, Direction dir) {
      IFluidHandler handlerDown = (IFluidHandler)tileBelow.getLevel()
         .getCapability(FluidHandler.BLOCK, tileBelow.getBlockPos(), tileBelow.getBlockState(), tileBelow, dir);
      if (handlerDown != null && offer.fluid() instanceof SoftFluidStackImpl impl) {
         FluidStack stack = impl.toForgeFluid();
         if (!stack.isEmpty()) {
            stack.setAmount(250 * offer.minAmount());
            if (stack.isEmpty()) {
               return null;
            }

            int filled = handlerDown.fill(stack, FluidAction.EXECUTE);
            tileBelow.setChanged();
            return Mth.ceil(filled / 250.0F);
         }
      }

      return null;
   }

   public static boolean hasFluidHandler(Level level, BlockPos pos, Direction dir) {
      return FluidUtil.getFluidHandler(level, pos, dir).isPresent();
   }

   @Nullable
   public static FluidOffer getFluidInTank(Level level, BlockPos pos, Direction dir, BlockEntity source) {
      Optional<IFluidHandler> opt = FluidUtil.getFluidHandler(level, pos, dir);
      if (opt.isPresent()) {
         for (int i = 1; i <= 4; i++) {
            int toDrain = i * 250;
            FluidStack fluidInTank = opt.get().drain(toDrain, FluidAction.SIMULATE);
            if (!fluidInTank.isEmpty()) {
               SoftFluidStack forgeFluid = SoftFluidStackImpl.fromForgeFluid(fluidInTank, level.registryAccess());
               if (!forgeFluid.isEmpty()) {
                  int actualAmount = fluidInTank.getAmount() / 250;
                  return FluidOffer.of(forgeFluid, actualAmount);
               }
            }
         }
      }

      return null;
   }
}
