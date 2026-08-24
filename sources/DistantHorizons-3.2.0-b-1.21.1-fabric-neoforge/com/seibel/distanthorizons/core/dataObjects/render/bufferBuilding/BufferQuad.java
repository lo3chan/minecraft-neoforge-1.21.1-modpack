package com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.enums.EDhDirection;

public final class BufferQuad {
   public static final int NORMAL_MAX_QUAD_WIDTH = 2048;
   public static final int MAX_QUAD_WIDTH_FOR_EARTH_CURVATURE = 16;
   public short x;
   public short y;
   public short z;
   public short widthEastWest;
   public short widthNorthSouthOrHeight;
   public int color;
   public short textureTileId;
   public byte irisBlockMaterialId;
   public byte skyLight;
   public byte blockLight;
   public EDhDirection direction;
   public boolean hasError = false;
   public long sortKeyEastWest;
   public long sortKeyNorthSouth;

   public void set(
      short x,
      short y,
      short z,
      short widthEastWest,
      short widthNorthSouthOrHeight,
      int color,
      short textureTileId,
      byte irisBlockMaterialId,
      byte skylight,
      byte blockLight,
      EDhDirection direction
   ) {
      if (widthEastWest == 0 || widthNorthSouthOrHeight == 0) {
         throw new IllegalArgumentException("Size 0 quad!");
      } else if (widthEastWest >= 0 && widthNorthSouthOrHeight >= 0) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.widthEastWest = widthEastWest;
         this.widthNorthSouthOrHeight = widthNorthSouthOrHeight;
         this.color = color;
         this.textureTileId = textureTileId;
         this.irisBlockMaterialId = irisBlockMaterialId;
         this.skyLight = skylight;
         this.blockLight = blockLight;
         this.direction = direction;
         this.sortKeyEastWest = this.computeSortKey(direction, true);
         this.sortKeyNorthSouth = this.computeSortKey(direction, false);
      } else {
         throw new IllegalArgumentException("Negative sized quad!");
      }
   }

   private long computeSortKey(EDhDirection dir, boolean eastWest) {
      if (eastWest) {
         switch (dir.axis) {
            case X:
               return (long)this.x << 48 | (long)this.y << 32 | (long)this.z << 16;
            case Y:
               return (long)this.y << 48 | (long)this.z << 32 | (long)this.x << 16;
            case Z:
               return (long)this.z << 48 | (long)this.y << 32 | (long)this.x << 16;
            default:
               throw new IllegalArgumentException("Invalid Axis enum: [" + dir.axis + "].");
         }
      } else {
         switch (dir.axis) {
            case X:
               return (long)this.x << 48 | (long)this.z << 32 | (long)this.y << 16;
            case Y:
               return (long)this.y << 48 | (long)this.x << 32 | (long)this.z << 16;
            case Z:
               return (long)this.z << 48 | (long)this.x << 32 | (long)this.y << 16;
            default:
               throw new IllegalArgumentException("Invalid Axis enum: [" + dir.axis + "].");
         }
      }
   }

   public int compare(BufferQuad quad, BufferMergeDirectionEnum compareDirection) {
      if (this.direction != quad.direction) {
         throw new IllegalArgumentException("The other quad is not in the same direction: " + quad.direction + " vs " + this.direction);
      } else {
         return compareDirection == BufferMergeDirectionEnum.EastWest
            ? Long.compare(this.sortKeyEastWest, quad.sortKeyEastWest)
            : Long.compare(this.sortKeyNorthSouth, quad.sortKeyNorthSouth);
      }
   }

   public boolean tryMerge(BufferQuad quad, BufferMergeDirectionEnum mergeDirection) {
      if (quad.hasError || this.hasError) {
         return false;
      } else if (this.direction != quad.direction) {
         return false;
      } else if ((mergeDirection != BufferMergeDirectionEnum.EastWest || this.y == quad.y)
         && (mergeDirection != BufferMergeDirectionEnum.NorthSouthOrUpDown || this.x == quad.x)) {
         short thisPerpendicularCompareStartPos;
         short thisParallelCompareStartPos;
         short otherPerpendicularCompareStartPos;
         short otherParallelCompareStartPos;
         switch (this.direction.axis) {
            case X:
               if (mergeDirection == BufferMergeDirectionEnum.EastWest) {
                  thisPerpendicularCompareStartPos = this.z;
                  thisParallelCompareStartPos = this.x;
                  otherPerpendicularCompareStartPos = quad.z;
                  otherParallelCompareStartPos = quad.x;
               } else {
                  thisPerpendicularCompareStartPos = this.y;
                  thisParallelCompareStartPos = this.z;
                  otherPerpendicularCompareStartPos = quad.y;
                  otherParallelCompareStartPos = quad.z;
               }
               break;
            case Y:
               if (mergeDirection == BufferMergeDirectionEnum.EastWest) {
                  thisPerpendicularCompareStartPos = this.x;
                  thisParallelCompareStartPos = this.z;
                  otherPerpendicularCompareStartPos = quad.x;
                  otherParallelCompareStartPos = quad.z;
               } else {
                  thisPerpendicularCompareStartPos = this.z;
                  thisParallelCompareStartPos = this.y;
                  otherPerpendicularCompareStartPos = quad.z;
                  otherParallelCompareStartPos = quad.y;
               }
               break;
            case Z:
               if (mergeDirection == BufferMergeDirectionEnum.EastWest) {
                  thisPerpendicularCompareStartPos = this.x;
                  thisParallelCompareStartPos = this.z;
                  otherPerpendicularCompareStartPos = quad.x;
                  otherParallelCompareStartPos = quad.z;
               } else {
                  thisPerpendicularCompareStartPos = this.y;
                  thisParallelCompareStartPos = this.z;
                  otherPerpendicularCompareStartPos = quad.y;
                  otherParallelCompareStartPos = quad.z;
               }
               break;
            default:
               throw new IllegalArgumentException("Unsupported axis: [" + this.direction.axis + "]");
         }

         short thisPerpendicularCompareWidth;
         short thisParallelCompareWidth;
         short otherPerpendicularCompareWidth;
         short otherParallelCompareWidth;
         if (mergeDirection == BufferMergeDirectionEnum.EastWest) {
            thisPerpendicularCompareWidth = this.widthEastWest;
            thisParallelCompareWidth = this.widthNorthSouthOrHeight;
            otherPerpendicularCompareWidth = quad.widthEastWest;
            otherParallelCompareWidth = quad.widthNorthSouthOrHeight;
         } else {
            thisPerpendicularCompareWidth = this.widthNorthSouthOrHeight;
            thisParallelCompareWidth = this.widthEastWest;
            otherPerpendicularCompareWidth = quad.widthNorthSouthOrHeight;
            otherParallelCompareWidth = quad.widthEastWest;
         }

         int maxQuadWidth = 2048;
         if (Config.Client.Advanced.Graphics.Experimental.earthCurveRatio.get() != 0) {
            maxQuadWidth = 16;
         }

         if (thisPerpendicularCompareWidth >= maxQuadWidth) {
            return false;
         } else if (Math.floorDiv((int)otherPerpendicularCompareStartPos, maxQuadWidth) != Math.floorDiv((int)thisPerpendicularCompareStartPos, maxQuadWidth)) {
            return false;
         } else if (thisPerpendicularCompareStartPos + thisPerpendicularCompareWidth < otherPerpendicularCompareStartPos
            || thisParallelCompareStartPos != otherParallelCompareStartPos) {
            return false;
         } else if (thisPerpendicularCompareStartPos + thisPerpendicularCompareWidth > otherPerpendicularCompareStartPos) {
            if (thisPerpendicularCompareStartPos < otherPerpendicularCompareStartPos + otherPerpendicularCompareWidth
               && Config.Client.Advanced.Debugging.showOverlappingQuadErrors.get()) {
               quad.hasError = true;
               this.hasError = true;
            }

            return false;
         } else if (thisParallelCompareWidth != otherParallelCompareWidth) {
            return false;
         } else if (this.color == quad.color
            && this.textureTileId == quad.textureTileId
            && this.irisBlockMaterialId == quad.irisBlockMaterialId
            && this.skyLight == quad.skyLight
            && this.blockLight == quad.blockLight) {
            if (mergeDirection == BufferMergeDirectionEnum.NorthSouthOrUpDown) {
               this.widthNorthSouthOrHeight = (short)(this.widthNorthSouthOrHeight + quad.widthNorthSouthOrHeight);
            } else {
               this.widthEastWest = (short)(this.widthEastWest + quad.widthEastWest);
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
