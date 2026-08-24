package dev.latvian.mods.kubejs.web.local.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;

public record MovedVertexConsumer(VertexConsumer parent, Pose pose) implements VertexConsumer {
   public VertexConsumer addVertex(float x, float y, float z) {
      return this.parent.addVertex(this.pose.pose(), x, y, z);
   }

   public VertexConsumer setColor(int red, int green, int blue, int alpha) {
      return this.parent.setColor(red, green, blue, alpha);
   }

   public VertexConsumer setUv(float u, float v) {
      return this.parent.setUv(u, v);
   }

   public VertexConsumer setUv1(int u, int v) {
      return this.parent.setUv1(u, v);
   }

   public VertexConsumer setUv2(int u, int v) {
      return this.parent.setUv2(u, v);
   }

   public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
      return this.parent.setNormal(this.pose, normalX, normalY, normalZ);
   }
}
