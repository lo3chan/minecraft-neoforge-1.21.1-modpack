package net.diebuddies.physics;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.diebuddies.opengl.TextureHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor.ARGB32;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public class DummyVertexConsumer implements VertexConsumer {
   public boolean trackVertices = false;

   public VertexConsumer addVertex(float x, float y, float z) {
      return this;
   }

   public VertexConsumer setColor(int red, int green, int blue, int alpha) {
      return this;
   }

   public VertexConsumer setUv(float u, float v) {
      return this;
   }

   public VertexConsumer setUv1(int u, int v) {
      return this;
   }

   public VertexConsumer setUv2(int u, int v) {
      return this;
   }

   public VertexConsumer setNormal(float x, float y, float z) {
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
      if (this.trackVertices) {
         int[] js = quad.getVertices();
         Vec3i vec3i = quad.getDirection().getNormal();
         Vector3f vec3f = new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
         Matrix4f matrix4f = matrixEntry.pose();
         matrixEntry.normal().transform(vec3f);
         int integerSize = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
         int j = js.length / integerSize;
         PhysicsMod.getCurrentInstance().itemStackEntity.feature = PhysicsMod.getCurrentInstance().blockifyFeature;
         PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).textureID = TextureHelper.getLoadedTextures();
         PhysicsMod.getCurrentInstance().itemStackEntity.shade = quad.isShade();
         MemoryStack stack = MemoryStack.stackPush();

         try {
            ByteBuffer byteBuffer = stack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            Mesh mesh = PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).mesh;

            for (int k = 0; k < j; k++) {
               intBuffer.clear();
               intBuffer.put(js, k * integerSize, integerSize);
               float f = byteBuffer.getFloat(0);
               float g = byteBuffer.getFloat(4);
               float h = byteBuffer.getFloat(8);
               float r;
               float s;
               float t;
               if (useQuadColorData) {
                  float l = (byteBuffer.get(12) & 255) / 255.0F;
                  float v = (byteBuffer.get(13) & 255) / 255.0F;
                  float w = (byteBuffer.get(14) & 255) / 255.0F;
                  r = l * red;
                  s = v * green;
                  t = w * blue;
               } else {
                  r = red;
                  s = green;
                  t = blue;
               }

               float v = byteBuffer.getFloat(16);
               float w = byteBuffer.getFloat(20);
               Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
               matrix4f.transform(vector4f);
               mesh.positions.add(new Vector3f(vector4f.x(), vector4f.y(), vector4f.z()));
               mesh.addColor(r, s, t);
               mesh.normals.add(new Vector3f(vec3f.x(), vec3f.y(), vec3f.z()));
               mesh.uvs.add(new Vector2f(v, w));
            }

            int index = mesh.positions.size() - 4;
            mesh.indices.add(index);
            mesh.indices.add(index + 1);
            mesh.indices.add(index + 2);
            mesh.indices.add(index);
            mesh.indices.add(index + 2);
            mesh.indices.add(index + 3);
         } catch (Throwable var32) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var31) {
                  var32.addSuppressed(var31);
               }
            }

            throw var32;
         }

         if (stack != null) {
            stack.close();
         }
      }
   }

   public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
      if (this.trackVertices) {
         PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).textureID = TextureHelper.getLoadedTextures();
         Mesh mesh = PhysicsMod.getCurrentInstance().itemStackEntity.models.get(0).mesh;
         mesh.positions.add(new Vector3f(x, y, z));
         mesh.addColor(ARGB32.red(color) / 255.0F, ARGB32.green(color) / 255.0F, ARGB32.blue(color) / 255.0F, ARGB32.alpha(color) / 255.0F);
         mesh.normals.add(new Vector3f(normalX, normalY, normalZ));
         mesh.uvs.add(new Vector2f(u, v));
         int index = mesh.positions.size() - 1;
         mesh.indices.add(index);
      }
   }

   public VertexConsumer addVertex(Matrix4f matrix, float x, float y, float z) {
      return this;
   }

   public VertexConsumer setNormal(Pose matrix, float x, float y, float z) {
      return this;
   }

   public VertexConsumer setColor(float red, float green, float blue, float alpha) {
      return this;
   }

   public VertexConsumer setLight(int uv) {
      return this;
   }

   public VertexConsumer setOverlay(int uv) {
      return this;
   }
}
