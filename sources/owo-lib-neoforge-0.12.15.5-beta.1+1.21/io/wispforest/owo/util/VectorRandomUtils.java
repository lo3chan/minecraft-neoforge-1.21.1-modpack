package io.wispforest.owo.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class VectorRandomUtils {
   private VectorRandomUtils() {
   }

   public static Vec3 getRandomCenteredOnBlock(Level world, BlockPos pos, double deviation) {
      return getRandomOffset(world, new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), deviation);
   }

   public static Vec3 getRandomWithinBlock(Level world, BlockPos pos) {
      return getRandomOffset(world, Vec3.atLowerCornerOf(pos).add(0.5, 0.5, 0.5), 0.5);
   }

   public static Vec3 getRandomOffset(Level world, Vec3 center, double deviation) {
      return getRandomOffsetSpecific(world, center, deviation, deviation, deviation);
   }

   public static Vec3 getRandomOffsetSpecific(Level world, Vec3 center, double deviationX, double deviationY, double deviationZ) {
      RandomSource r = world.getRandom();
      double x = center.x() + (r.nextDouble() - 0.5) * deviationX;
      double y = center.y() + (r.nextDouble() - 0.5) * deviationY;
      double z = center.z() + (r.nextDouble() - 0.5) * deviationZ;
      return new Vec3(x, y, z);
   }
}
