package net.mehvahdjukaar.moonlight.api.fluids;

import net.mehvahdjukaar.moonlight.api.fluids.platform.FluidsHelperImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FluidsHelper {
   public static boolean extractFluidFromTank(BlockEntity var0, Direction var1, int var2) {
      return FluidsHelperImpl.extractFluidFromTank(var0, var1, var2);
   }

   public static Integer fillFluidTank(BlockEntity var0, FluidOffer var1, Direction var2) {
      return FluidsHelperImpl.fillFluidTank(var0, var1, var2);
   }

   public static boolean hasFluidHandler(Level var0, BlockPos var1, Direction var2) {
      return FluidsHelperImpl.hasFluidHandler(var0, var1, var2);
   }

   public static FluidOffer getFluidInTank(Level var0, BlockPos var1, Direction var2, BlockEntity var3) {
      return FluidsHelperImpl.getFluidInTank(var0, var1, var2, var3);
   }
}
