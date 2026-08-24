package vazkii.psi.api.internal;

import java.util.LinkedHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class MathHelper {
   public static float pointDistancePlane(double x1, double y1, double x2, double y2) {
      return (float)Math.hypot(x1 - x2, y1 - y2);
   }

   public static double pointDistanceSpace(double x1, double y1, double z1, double x2, double y2, double z2) {
      return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0) + Math.pow(z1 - z2, 2.0));
   }

   public static LinkedHashSet<BlockPos> getBlocksAlongRay(Vec3 origin, Vec3 end, int maxBlocks) {
      LinkedHashSet<BlockPos> positions = new LinkedHashSet<>();
      if (maxBlocks == 0) {
         return positions;
      } else {
         if (origin.equals(end)) {
            positions.add(new BlockPos((int)origin.x, (int)origin.y, (int)origin.z));
         } else {
            double endX = end.x;
            double endY = end.y;
            double endZ = end.z;
            double originX = origin.x;
            double originY = origin.y;
            double originZ = origin.z;
            int blockX = Mth.floor(originX);
            int blockY = Mth.floor(originY);
            int blockZ = Mth.floor(originZ);
            MutableBlockPos blockPos = new MutableBlockPos(blockX, blockY, blockZ);
            positions.add(blockPos.immutable());
            double lengthX = endX - originX;
            double lengthY = endY - originY;
            double lengthZ = endZ - originZ;
            int signumX = Mth.sign(lengthX);
            int signumY = Mth.sign(lengthY);
            int signumZ = Mth.sign(lengthZ);
            double stepSizeX = signumX == 0 ? 1.7976931348623157E308 : signumX / lengthX;
            double stepSizeY = signumY == 0 ? 1.7976931348623157E308 : signumY / lengthY;
            double stepSizeZ = signumZ == 0 ? 1.7976931348623157E308 : signumZ / lengthZ;
            double totalStepsX = stepSizeX * (signumX > 0 ? 1.0 - Mth.frac(originX) : Mth.frac(originX));
            double totalStepsY = stepSizeY * (signumY > 0 ? 1.0 - Mth.frac(originY) : Mth.frac(originY));
            double totalStepsZ = stepSizeZ * (signumZ > 0 ? 1.0 - Mth.frac(originZ) : Mth.frac(originZ));

            while ((totalStepsX <= 1.0 || totalStepsY <= 1.0 || totalStepsZ <= 1.0) && positions.size() != maxBlocks) {
               if (totalStepsX < totalStepsY) {
                  if (totalStepsX < totalStepsZ) {
                     blockX += signumX;
                     totalStepsX += stepSizeX;
                  } else {
                     blockZ += signumZ;
                     totalStepsZ += stepSizeZ;
                  }
               } else if (totalStepsY < totalStepsZ) {
                  blockY += signumY;
                  totalStepsY += stepSizeY;
               } else {
                  blockZ += signumZ;
                  totalStepsZ += stepSizeZ;
               }

               blockPos.set(blockX, blockY, blockZ);
               positions.add(blockPos.immutable());
            }
         }

         return positions;
      }
   }
}
