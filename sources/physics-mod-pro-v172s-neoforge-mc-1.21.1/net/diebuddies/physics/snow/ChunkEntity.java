package net.diebuddies.physics.snow;

import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class ChunkEntity {
   public Vector3i position;
   public Vector3i batchPosition;
   public ArenaBuffer.MemorySegment vertexSegment;
   public ArenaBuffer.MemorySegment indexSegment;
   public AABB3D aabb;
   public Vector3d center;

   public void calculateTransformations() {
      Matrix4d transformation = new Matrix4d();
      double scale = 1.0 / IChunk.CHUNK_MULTIPLE;
      transformation.identity();
      transformation.translate(
         (this.batchPosition.x * IChunk.CHUNK_SIZE + 0.5) * scale,
         (this.batchPosition.y * IChunk.CHUNK_SIZE + 0.5) * scale,
         (this.batchPosition.z * IChunk.CHUNK_SIZE + 0.5) * scale
      );
      transformation.scale(scale);
      transformation.transformAab(this.aabb.getMin(), this.aabb.getMax(), this.aabb.getMin(), this.aabb.getMax());
   }
}
