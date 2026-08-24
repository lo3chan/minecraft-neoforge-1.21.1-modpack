package com.aetherteam.aether.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class BlockLogicUtil {
   public static BlockPos tunnelFromOddSquareRoom(BoundingBox box, Direction direction, int width) {
      int offsetFromCenter = (direction.getAxis() == Axis.X ? box.getZSpan() : box.getXSpan()) >> 1;
      int sidedOffset = width >> 1;
      int xOffset = direction.getStepX() * offsetFromCenter - direction.getStepZ() * sidedOffset - Math.max(0, direction.getStepX());
      int zOffset = direction.getStepZ() * offsetFromCenter + direction.getStepX() * sidedOffset - Math.max(0, direction.getStepZ());
      return box.getCenter().offset(xOffset, -(box.getYSpan() >> 1), zOffset);
   }

   public static BlockPos tunnelFromEvenSquareRoom(BoundingBox box, Direction direction, int width) {
      int offsetFromCenter = (direction.getAxis() == Axis.X ? box.getZSpan() : box.getXSpan()) + 1 >> 1;
      int sidedOffset = width >> 1;
      int xOffset = direction.getStepX() * offsetFromCenter
         - direction.getStepZ() * sidedOffset
         - Math.max(0, direction.getStepX())
         + Math.min(0, direction.getStepZ());
      int zOffset = direction.getStepZ() * offsetFromCenter
         + direction.getStepX() * sidedOffset
         - Math.max(0, direction.getStepZ())
         - Math.max(0, direction.getStepX());
      return box.getCenter().offset(xOffset, -(box.getYSpan() >> 1), zOffset);
   }

   public static boolean isOutOfBounds(BlockPos pos, ChunkPos centerChunk) {
      int x = SectionPos.blockToSectionCoord(pos.getX());
      int z = SectionPos.blockToSectionCoord(pos.getZ());
      int xDistance = Math.abs(x - centerChunk.x);
      int zDistance = Math.abs(z - centerChunk.z);
      return xDistance > 1 || zDistance > 1;
   }
}
