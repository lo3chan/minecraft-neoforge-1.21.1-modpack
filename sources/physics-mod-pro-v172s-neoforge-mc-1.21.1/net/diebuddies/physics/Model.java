package net.diebuddies.physics;

import java.nio.ByteBuffer;
import net.diebuddies.math.Math;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Pack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Model {
   public TextureAtlasSprite texture;
   public Matrix4f textureMatrix;
   public TextureAtlasSprite animationSprite;
   public int textureID;
   public Mesh mesh;
   public Mesh physicsMesh;
   public ArenaBuffer.MemorySegment memorySegment;
   public boolean onlyVisual;

   public void createModelMemorySegment(PhysicsWorld physics, boolean shade) {
      int size = this.mesh.indices.size();
      int positionSize = size * 3 * 4;
      int colorSize = size * 4;
      int uvSize = size * 2 * 4;
      int normalSize = size * 4;
      int vertexSize = positionSize + colorSize + uvSize + normalSize;
      boolean usePBRData = StarterClient.iris || StarterClient.optifabric;
      if (usePBRData) {
         int midUvSize = size * 2 * 4;
         int tangentSize = size * 4;
         vertexSize += midUvSize + tangentSize;
      }

      ByteBuffer data = null;
      boolean stackAlloc = false;
      if (vertexSize <= StarterClient.memoryStack.getSize()) {
         MemoryStack stack = StarterClient.memoryStack.push();
         data = stack.malloc(vertexSize);
         stackAlloc = true;
      } else {
         data = MemoryUtil.memAlloc(vertexSize);
      }

      long pointer = MemoryUtil.memAddress(data);
      float minU = 0.0F;
      float maxU = 1.0F;
      float minV = 0.0F;
      float maxV = 1.0F;
      if (this.texture != null && this.textureMatrix == null) {
         minU = this.texture.getU0();
         maxU = this.texture.getU1();
         minV = this.texture.getV0();
         maxV = this.texture.getV1();
      }

      for (int i = 0; i < size; i++) {
         int index = this.mesh.indices.getInt(i);
         Vector3f p = this.mesh.positions.get(index);
         MemoryUtil.memPutFloat(pointer, p.x);
         MemoryUtil.memPutFloat(pointer + 4L, p.y);
         MemoryUtil.memPutFloat(pointer + 8L, p.z);
         if (this.mesh.colors.size() > 0) {
            MemoryUtil.memPutInt(pointer + 12L, this.mesh.colors.getInt(index));
         } else {
            MemoryUtil.memPutInt(pointer + 12L, -1);
         }

         if (this.mesh.uvs.size() == 0) {
            MemoryUtil.memPutFloat(pointer + 16L, Math.remapClamp(0.5F, 0.0F, 1.0F, minU, maxU));
            MemoryUtil.memPutFloat(pointer + 20L, Math.remapClamp(0.5F, 0.0F, 1.0F, minV, maxV));
         } else {
            Vector2f uv = this.mesh.uvs.get(index);
            MemoryUtil.memPutFloat(pointer + 16L, Math.remapClamp(uv.x, 0.0F, 1.0F, minU, maxU));
            MemoryUtil.memPutFloat(pointer + 20L, Math.remapClamp(uv.y, 0.0F, 1.0F, minV, maxV));
         }

         if (shade && this.mesh.normals.size() > 0) {
            Vector3f normal = this.mesh.normals.get(index);
            MemoryUtil.memPutInt(pointer + 24L, Pack.normal(normal.x, normal.y, normal.z));
         } else {
            MemoryUtil.memPutInt(pointer + 24L, Pack.Y_POS_NORMAL);
         }

         if (usePBRData && this.mesh.midcoords != null) {
            if (shade && this.mesh.tangents.size() > 0) {
               Vector4f tangent = this.mesh.tangents.get(index);
               MemoryUtil.memPutInt(pointer + 28L, Pack.normal(tangent.x, tangent.y, tangent.z, tangent.w));
            } else {
               MemoryUtil.memPutInt(pointer + 28L, Pack.X_POS_TANGENT);
            }

            if (this.mesh.midcoords.size() == 0) {
               MemoryUtil.memPutFloat(pointer + 32L, Math.remapClamp(0.5F, 0.0F, 1.0F, minU, maxU));
               MemoryUtil.memPutFloat(pointer + 36L, Math.remapClamp(0.5F, 0.0F, 1.0F, minV, maxV));
            } else {
               Vector2f miduv = this.mesh.midcoords.get(i / 3);
               MemoryUtil.memPutFloat(pointer + 32L, Math.remapClamp(miduv.x, 0.0F, 1.0F, minU, maxU));
               MemoryUtil.memPutFloat(pointer + 36L, Math.remapClamp(miduv.y, 0.0F, 1.0F, minV, maxV));
            }

            pointer += 12L;
         }

         pointer += 28L;
      }

      this.memorySegment = physics.getModelVertexData().uploadData(data);
      if (stackAlloc) {
         StarterClient.memoryStack.pop();
      } else {
         MemoryUtil.memFree(data);
      }

      this.mesh.clearMemory();
      if (this.physicsMesh != null) {
         this.physicsMesh.clearMemory();
      }
   }
}
