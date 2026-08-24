package fuzs.puzzleslib.api.shape.v1;

import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3d;

@Deprecated
public final class ShapesHelper {
   private ShapesHelper() {
   }

   public static Map<Direction, VoxelShape> rotate(VoxelShape voxelShape) {
      return fuzs.puzzleslib.api.util.v1.ShapesHelper.rotate(voxelShape);
   }

   public static Map<Direction, VoxelShape> rotateHorizontally(VoxelShape voxelShape) {
      return fuzs.puzzleslib.api.util.v1.ShapesHelper.rotateHorizontally(voxelShape);
   }

   public static Quaternionf getHorizontalRotation(Direction direction) {
      return fuzs.puzzleslib.api.util.v1.ShapesHelper.getHorizontalRotation(direction);
   }

   @Deprecated(
      forRemoval = true
   )
   public static VoxelShape rotate(Direction direction, VoxelShape voxelShape) {
      return rotate(direction.getRotation(), voxelShape);
   }

   @Deprecated(
      forRemoval = true
   )
   public static VoxelShape rotate(Direction direction, VoxelShape voxelShape, Vector3d originOffset) {
      return rotate(direction.getRotation(), voxelShape, originOffset);
   }

   public static VoxelShape rotate(Quaternionf rotation, VoxelShape voxelShape) {
      return fuzs.puzzleslib.api.util.v1.ShapesHelper.rotate(rotation, voxelShape);
   }

   public static VoxelShape rotate(Quaternionf rotation, VoxelShape voxelShape, Vector3d originOffset) {
      return fuzs.puzzleslib.api.util.v1.ShapesHelper.rotate(rotation, voxelShape, originOffset);
   }

   public static VoxelShape box(double startX, double startY, double startZ, double endX, double endY, double endZ) {
      return fuzs.puzzleslib.api.util.v1.ShapesHelper.box(startX, startY, startZ, endX, endY, endZ);
   }
}
