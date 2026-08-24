package dev.worldgen.lithostitched.worldgen.structure;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;

public class BoxOctree {
   private static final int SUBDIVIDE_THRESHOLD = 10;
   private static final int MAXIMUM_DEPTH = 3;
   private final AABB boundary;
   private final Vec3i size;
   private final int depth;
   private final List<AABB> innerBoxes = new ArrayList<>();
   private final List<BoxOctree> childrenOctants = new ArrayList<>();

   public BoxOctree(AABB axisAlignedBB) {
      this(axisAlignedBB, 0);
   }

   private BoxOctree(AABB axisAlignedBB, int parentDepth) {
      this.boundary = axisAlignedBB.move(0.0, 0.0, 0.0);
      this.size = new Vec3i(
         this.roundAwayFromZero(this.boundary.getXsize()), this.roundAwayFromZero(this.boundary.getYsize()), this.roundAwayFromZero(this.boundary.getZsize())
      );
      this.depth = parentDepth + 1;
   }

   private int roundAwayFromZero(double value) {
      return value >= 0.0 ? (int)Math.ceil(value) : (int)Math.floor(value);
   }

   private void subdivide() {
      if (!this.childrenOctants.isEmpty()) {
         throw new UnsupportedOperationException("lithostitched - Tried to subdivide when there are already children octants.");
      } else {
         int halfXSize = this.size.getX() / 2;
         int halfYSize = this.size.getY() / 2;
         int halfZSize = this.size.getZ() / 2;
         this.childrenOctants
            .add(
               new BoxOctree(
                  new AABB(
                     this.boundary.minX,
                     this.boundary.minY,
                     this.boundary.minZ,
                     this.boundary.minX + halfXSize,
                     this.boundary.minY + halfYSize,
                     this.boundary.minZ + halfZSize
                  ),
                  this.depth
               )
            );
         this.childrenOctants
            .add(
               new BoxOctree(
                  new AABB(
                     this.boundary.minX,
                     this.boundary.minY,
                     this.boundary.minZ + halfZSize,
                     this.boundary.minX + halfXSize,
                     this.boundary.minY + halfYSize,
                     this.boundary.maxZ
                  ),
                  this.depth
               )
            );
         this.childrenOctants
            .add(
               new BoxOctree(
                  new AABB(
                     this.boundary.minX + halfXSize,
                     this.boundary.minY,
                     this.boundary.minZ,
                     this.boundary.maxX,
                     this.boundary.minY + halfYSize,
                     this.boundary.minZ + halfZSize
                  ),
                  this.depth
               )
            );
         this.childrenOctants
            .add(
               new BoxOctree(
                  new AABB(
                     this.boundary.minX + halfXSize,
                     this.boundary.minY,
                     this.boundary.minZ + halfZSize,
                     this.boundary.maxX,
                     this.boundary.minY + halfYSize,
                     this.boundary.maxZ
                  ),
                  this.depth
               )
            );
         this.childrenOctants
            .add(
               new BoxOctree(
                  new AABB(
                     this.boundary.minX,
                     this.boundary.minY + halfYSize,
                     this.boundary.minZ,
                     this.boundary.minX + halfXSize,
                     this.boundary.maxY,
                     this.boundary.minZ + halfZSize
                  ),
                  this.depth
               )
            );
         this.childrenOctants
            .add(
               new BoxOctree(
                  new AABB(
                     this.boundary.minX,
                     this.boundary.minY + halfYSize,
                     this.boundary.minZ + halfZSize,
                     this.boundary.minX + halfXSize,
                     this.boundary.maxY,
                     this.boundary.maxZ
                  ),
                  this.depth
               )
            );
         this.childrenOctants
            .add(
               new BoxOctree(
                  new AABB(
                     this.boundary.minX + halfXSize,
                     this.boundary.minY + halfYSize,
                     this.boundary.minZ,
                     this.boundary.maxX,
                     this.boundary.maxY,
                     this.boundary.minZ + halfZSize
                  ),
                  this.depth
               )
            );
         this.childrenOctants
            .add(
               new BoxOctree(
                  new AABB(
                     this.boundary.minX + halfXSize,
                     this.boundary.minY + halfYSize,
                     this.boundary.minZ + halfZSize,
                     this.boundary.maxX,
                     this.boundary.maxY,
                     this.boundary.maxZ
                  ),
                  this.depth
               )
            );

         for (AABB parentInnerBox : this.innerBoxes) {
            for (BoxOctree octree : this.childrenOctants) {
               if (octree.boundaryIntersects(parentInnerBox)) {
                  octree.addBox(parentInnerBox);
               }
            }
         }

         this.innerBoxes.clear();
      }
   }

   public void addBox(AABB axisAlignedBB) {
      if (this.depth < 3 && this.innerBoxes.size() > 10) {
         this.subdivide();
      }

      if (!this.childrenOctants.isEmpty()) {
         for (BoxOctree octree : this.childrenOctants) {
            if (octree.boundaryIntersects(axisAlignedBB)) {
               octree.addBox(axisAlignedBB);
            }
         }
      } else {
         for (AABB parentInnerBox : this.innerBoxes) {
            if (parentInnerBox.equals(axisAlignedBB)) {
               return;
            }
         }

         this.innerBoxes.add(axisAlignedBB);
      }
   }

   public boolean boundaryEntirelyContains(AABB axisAlignedBB) {
      return this.boundary.contains(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ)
         && this.boundary.contains(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
   }

   public boolean boundaryIntersects(AABB axisAlignedBB) {
      return this.boundary.intersects(axisAlignedBB);
   }

   public boolean withinBoundsButNotIntersectingChildren(AABB axisAlignedBB) {
      return this.boundaryEntirelyContains(axisAlignedBB) && !this.intersectsAnyBox(axisAlignedBB);
   }

   public boolean intersectsAnyBox(AABB axisAlignedBB) {
      if (!this.childrenOctants.isEmpty()) {
         for (BoxOctree octree : this.childrenOctants) {
            if (octree.boundaryIntersects(axisAlignedBB) && octree.intersectsAnyBox(axisAlignedBB)) {
               return true;
            }
         }
      } else {
         for (AABB innerBox : this.innerBoxes) {
            if (innerBox.intersects(axisAlignedBB)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean boundaryContains(BlockPos position) {
      return this.boundary.contains(position.getX(), position.getY(), position.getZ());
   }

   public boolean withinAnyBox(BlockPos position) {
      if (!this.childrenOctants.isEmpty()) {
         for (BoxOctree octree : this.childrenOctants) {
            if (octree.boundaryContains(position) && octree.withinAnyBox(position)) {
               return true;
            }
         }
      } else {
         for (AABB innerBox : this.innerBoxes) {
            if (innerBox.contains(position.getX(), position.getY(), position.getZ())) {
               return true;
            }
         }
      }

      return false;
   }
}
