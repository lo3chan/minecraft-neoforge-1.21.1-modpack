package net.diebuddies.physics.settings.mobs;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public class BoundingBoxGetter implements VertexConsumer {
   public Vector3d min = new Vector3d(1.7976931348623157E308);
   public Vector3d max = new Vector3d(-1.7976931348623157E308);
   private Vector3d tmp = new Vector3d();

   public VertexConsumer addVertex(float x, float y, float z) {
      this.min.min(this.tmp.set(x, y, z));
      this.max.max(this.tmp.set(x, y, z));
      return this;
   }

   public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
      return this;
   }

   public VertexConsumer setUv(float var1, float var2) {
      return this;
   }

   public VertexConsumer setUv1(int var1, int var2) {
      return this;
   }

   public VertexConsumer setUv2(int var1, int var2) {
      return this;
   }

   public VertexConsumer setNormal(float var1, float var2, float var3) {
      return this;
   }

   public void putBulkData(
      Pose matrixEntry,
      BakedQuad quad,
      float[] brightnesses,
      float red,
      float green,
      float blue,
      float alpha,
      int[] lights,
      int overlay,
      boolean useQuadColorData
   ) {
      int[] vertices = quad.getVertices();
      Matrix4f transformation = matrixEntry.pose();
      int integerSize = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
      int dataPerVertex = vertices.length / integerSize;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         ByteBuffer byteBuffer = stack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
         IntBuffer intBuffer = byteBuffer.asIntBuffer();

         for (int k = 0; k < dataPerVertex; k++) {
            intBuffer.clear();
            intBuffer.put(vertices, k * integerSize, integerSize);
            float f = byteBuffer.getFloat(0);
            float g = byteBuffer.getFloat(4);
            float h = byteBuffer.getFloat(8);
            Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
            transformation.transform(vector4f);
            this.min.min(this.tmp.set(vector4f.x(), vector4f.y(), vector4f.z()));
            this.max.max(this.tmp.set(vector4f.x(), vector4f.y(), vector4f.z()));
         }
      } catch (Throwable var24) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var23) {
               var24.addSuppressed(var23);
            }
         }

         throw var24;
      }

      if (stack != null) {
         stack.close();
      }
   }
}
