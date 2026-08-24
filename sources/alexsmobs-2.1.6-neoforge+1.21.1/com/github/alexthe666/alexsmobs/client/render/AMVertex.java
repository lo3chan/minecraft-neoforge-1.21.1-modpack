package com.github.alexthe666.alexsmobs.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class AMVertex {
   public static VertexConsumer normal(VertexConsumer consumer, Matrix3f normalMatrix, float x, float y, float z) {
      Vector3f transformed = normalMatrix.transform(new Vector3f(x, y, z)).normalize();
      return normal(consumer, transformed.x(), transformed.y(), transformed.z());
   }

   public static void addVertex(
      VertexConsumer consumer,
      float x,
      float y,
      float z,
      float red,
      float green,
      float blue,
      float alpha,
      float u,
      float v,
      int packedOverlay,
      int packedLight,
      float nx,
      float ny,
      float nz
   ) {
      consumer.addVertex(x, y, z, AMRenderCompat.packColor(red, green, blue, alpha), u, v, packedOverlay, packedLight, nx, ny, nz);
   }

   public static VertexConsumer normal(VertexConsumer consumer, float x, float y, float z) {
      return consumer.setNormal(x, y, z);
   }
}
