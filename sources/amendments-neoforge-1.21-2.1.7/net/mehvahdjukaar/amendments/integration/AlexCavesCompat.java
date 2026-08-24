package net.mehvahdjukaar.amendments.integration;

import net.mehvahdjukaar.amendments.integration.platform.AlexCavesCompatImpl;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AlexCavesCompat {
   public static void acidDamage(SoftFluidStack var0, Level var1, BlockPos var2, BlockState var3, Entity var4) {
      AlexCavesCompatImpl.acidDamage(var0, var1, var2, var3, var4);
   }

   public static void acidParticles(SoftFluidStack var0, Level var1, BlockPos var2, RandomSource var3, double var4) {
      AlexCavesCompatImpl.acidParticles(var0, var1, var2, var3, var4);
   }
}
