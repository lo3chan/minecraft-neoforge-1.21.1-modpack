package fuzs.puzzleslib.api.util.v1;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public final class ShapesHelper {
   private ShapesHelper() {
   }

   public static Map<Direction, VoxelShape> rotate(VoxelShape voxelShape) {
      Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

      for (Direction direction : Direction.values()) {
         shapes.put(direction, rotate(direction.getRotation(), voxelShape));
      }

      return Maps.immutableEnumMap(shapes);
   }

   public static Map<Direction, VoxelShape> rotateHorizontally(VoxelShape voxelShape) {
      Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

      for (Direction direction : Plane.HORIZONTAL) {
         Quaternionf rotation = getHorizontalRotation(direction);
         shapes.put(direction, rotate(rotation, voxelShape));
      }

      return Maps.immutableEnumMap(shapes);
   }

   public static Quaternionf getHorizontalRotation(Direction direction) {
      return new Quaternionf().rotationY((float)Math.atan2(direction.getStepX(), direction.getStepZ()));
   }

   public static VoxelShape rotate(Quaternionf rotation, VoxelShape voxelShape) {
      return rotate(rotation, voxelShape, new Vector3d(0.5, 0.5, 0.5));
   }

   public static VoxelShape rotate(Quaternionf rotation, VoxelShape voxelShape, Vector3d originOffset) {
      VoxelShape[] joinedVoxelShape = new VoxelShape[]{Shapes.empty()};
      voxelShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
         Vector3d start = rotation.transform(new Vector3d(minX, minY, minZ).sub(originOffset)).add(originOffset);
         Vector3d end = rotation.transform(new Vector3d(maxX, maxY, maxZ).sub(originOffset)).add(originOffset);
         joinedVoxelShape[0] = Shapes.or(joinedVoxelShape[0], box(start.x, start.y, start.z, end.x, end.y, end.z));
      });
      return joinedVoxelShape[0];
   }

   public static VoxelShape box(double startX, double startY, double startZ, double endX, double endY, double endZ) {
      return Shapes.box(
         Math.min(startX, endX), Math.min(startY, endY), Math.min(startZ, endZ), Math.max(startX, endX), Math.max(startY, endY), Math.max(startZ, endZ)
      );
   }

   public static VoxelShape[] boxes(int endInclusive, IntFunction<VoxelShape> voxelShapeFactory) {
      return IntStream.rangeClosed(0, endInclusive).mapToObj(voxelShapeFactory).toArray(VoxelShape[]::new);
   }

   public static VoxelShape cube(double size) {
      return cube(size, size, size);
   }

   public static VoxelShape cube(double sizeX, double sizeY, double sizeZ) {
      double halfY = sizeY / 2.0;
      return column(sizeX, sizeZ, 8.0 - halfY, 8.0 + halfY);
   }

   public static VoxelShape column(double sizeXZ, double minY, double maxY) {
      return column(sizeXZ, sizeXZ, minY, maxY);
   }

   public static VoxelShape column(double sizeX, double sizeZ, double minY, double maxY) {
      double halfX = sizeX / 2.0;
      double halfZ = sizeZ / 2.0;
      return Block.box(8.0 - halfX, minY, 8.0 - halfZ, 8.0 + halfX, maxY, 8.0 + halfZ);
   }

   public static VoxelShape boxZ(double sizeXY, double minZ, double maxZ) {
      return boxZ(sizeXY, sizeXY, minZ, maxZ);
   }

   public static VoxelShape boxZ(double sizeX, double sizeY, double minZ, double maxZ) {
      double halfY = sizeY / 2.0;
      return boxZ(sizeX, 8.0 - halfY, 8.0 + halfY, minZ, maxZ);
   }

   public static VoxelShape boxZ(double sizeX, double minY, double maxY, double minZ, double maxZ) {
      double halfX = sizeX / 2.0;
      return Block.box(8.0 - halfX, minY, minZ, 8.0 + halfX, maxY, maxZ);
   }
}
