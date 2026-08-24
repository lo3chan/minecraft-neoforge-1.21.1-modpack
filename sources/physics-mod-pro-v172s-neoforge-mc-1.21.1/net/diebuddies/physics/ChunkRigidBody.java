package net.diebuddies.physics;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import org.lwjgl.system.MemoryStack;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;

public class ChunkRigidBody {
   private PxRigidActor chunk;
   private List<PxShape> blocks = new ObjectArrayList();
   private PxShapeFlags shapeFlags = new PxShapeFlags((byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
   private PxFilterData filterData = new PxFilterData(1, 23, 0, 0);
   private boolean destroyed;

   public ChunkRigidBody(double x, double y, double z) {
      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)x, (float)y, (float)z);
         PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F);
         PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
         this.chunk = StarterClient.physics.createRigidStatic(tmpPose);
      } catch (Throwable var12) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (mem != null) {
         mem.close();
      }
   }

   public void attachBox(float x, float y, float z, float width, float height, float depth) {
      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxBoxGeometry boxGeometry = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, width * 0.5F, height * 0.5F, depth * 0.5F);
         PxShape boxShape = StarterClient.physics.createShape(boxGeometry, StarterClient.defaultMaterial, true, this.shapeFlags);
         boxShape.setLocalPose(
            PxTransform.createAt(
               mem,
               MemoryStack::nmalloc,
               PxVec3.createAt(mem, MemoryStack::nmalloc, x, y, z),
               PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
            )
         );
         boxShape.setSimulationFilterData(this.filterData);
         this.chunk.attachShape(boxShape);
         this.blocks.add(boxShape);
      } catch (Throwable var11) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (mem != null) {
         mem.close();
      }
   }

   public PxRigidActor getActor() {
      return this.chunk;
   }

   public void destroy() {
      if (!this.destroyed) {
         for (PxShape shape : this.blocks) {
            shape.release();
         }

         this.chunk.release();
         this.shapeFlags.destroy();
         this.filterData.destroy();
         this.destroyed = true;
      }
   }
}
