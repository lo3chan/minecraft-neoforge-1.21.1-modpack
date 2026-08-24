package net.irisshaders.iris.pathways;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class HorizonRenderer {
   private static final float TOP = 16.0F;
   private static final float BOTTOM = -16.0F;
   private static final float COS_22_5 = (float)Math.cos(Math.toRadians(22.5));
   private static final float SIN_22_5 = (float)Math.sin(Math.toRadians(22.5));
   private VertexBuffer buffer;
   private int currentRenderDistance = Minecraft.getInstance().options.getEffectiveRenderDistance();

   public HorizonRenderer() {
      this.rebuildBuffer();
   }

   private void rebuildBuffer() {
      if (this.buffer != null) {
         this.buffer.close();
      }

      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION);
      this.buildHorizon(this.currentRenderDistance * 16, buffer);
      MeshData meshData = buffer.build();
      this.buffer = new VertexBuffer(Usage.STATIC);
      this.buffer.bind();
      this.buffer.upload(meshData);
      Tesselator.getInstance().clear();
      VertexBuffer.unbind();
   }

   private void buildQuad(VertexConsumer consumer, float x1, float z1, float x2, float z2) {
      consumer.addVertex(x1, -16.0F, z1);
      consumer.addVertex(x1, 16.0F, z1);
      consumer.addVertex(x2, 16.0F, z2);
      consumer.addVertex(x2, -16.0F, z2);
   }

   private void buildHalf(VertexConsumer consumer, float adjacent, float opposite, boolean invert) {
      if (invert) {
         adjacent = -adjacent;
         opposite = -opposite;
      }

      this.buildQuad(consumer, adjacent, -opposite, opposite, -adjacent);
      this.buildQuad(consumer, adjacent, opposite, adjacent, -opposite);
      this.buildQuad(consumer, opposite, adjacent, adjacent, opposite);
      this.buildQuad(consumer, -opposite, adjacent, opposite, adjacent);
   }

   private void buildOctagonalPrism(VertexConsumer consumer, float adjacent, float opposite) {
      this.buildHalf(consumer, adjacent, opposite, false);
      this.buildHalf(consumer, adjacent, opposite, true);
   }

   private void buildRegularOctagonalPrism(VertexConsumer consumer, float radius) {
      this.buildOctagonalPrism(consumer, radius * COS_22_5, radius * SIN_22_5);
   }

   private void buildBottomPlane(VertexConsumer consumer, int radius) {
      for (int x = -radius; x <= radius; x += 64) {
         for (int z = -radius; z <= radius; z += 64) {
            consumer.addVertex(x + 64, -16.0F, z);
            consumer.addVertex(x, -16.0F, z);
            consumer.addVertex(x, -16.0F, z + 64);
            consumer.addVertex(x + 64, -16.0F, z + 64);
         }
      }
   }

   private void buildTopPlane(VertexConsumer consumer, int radius) {
      for (int x = -radius; x <= radius; x += 64) {
         for (int z = -radius; z <= radius; z += 64) {
            consumer.addVertex(x + 64, 16.0F, z);
            consumer.addVertex(x + 64, 16.0F, z + 64);
            consumer.addVertex(x, 16.0F, z + 64);
            consumer.addVertex(x, 16.0F, z);
         }
      }
   }

   private void buildHorizon(int radius, VertexConsumer consumer) {
      if (radius > 256) {
         radius = 256;
      }

      this.buildRegularOctagonalPrism(consumer, radius);
      this.buildTopPlane(consumer, 384);
      this.buildBottomPlane(consumer, 384);
   }

   public void renderHorizon(Matrix4fc modelView, Matrix4fc projection, ShaderInstance shader) {
      if (this.currentRenderDistance != Minecraft.getInstance().options.getEffectiveRenderDistance()) {
         this.currentRenderDistance = Minecraft.getInstance().options.getEffectiveRenderDistance();
         this.rebuildBuffer();
      }

      this.buffer.bind();
      this.buffer.drawWithShader(new Matrix4f(modelView), new Matrix4f(projection), shader);
      VertexBuffer.unbind();
   }

   public void destroy() {
      this.buffer.close();
   }
}
