package com.nyfaria.nyfsspiders;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class CommonClass {
   public static void init() {
   }

   public static BlockPos blockPos(double pX, double pY, double pZ) {
      return new BlockPos(Mth.floor(pX), Mth.floor(pY), Mth.floor(pZ));
   }

   public static BlockPos blockPos(Vec3 pVec3) {
      return blockPos(pVec3.x, pVec3.y, pVec3.z);
   }
}
