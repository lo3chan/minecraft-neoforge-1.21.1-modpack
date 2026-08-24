package vazkii.patchouli.client.book;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;

public record LiquidBlockVertexConsumer(VertexConsumer prior, PoseStack pose, BlockPos pos) implements VertexConsumer {
   public VertexConsumer addVertex(float x, float y, float z) {
      float dx = this.pos.getX() & 15;
      float dy = this.pos.getY() & 15;
      float dz = this.pos.getZ() & 15;
      return this.prior.addVertex(this.pose.last().pose(), x - dx, y - dy, z - dz);
   }

   public VertexConsumer setColor(int r, int g, int b, int a) {
      return this.prior.setColor(r, g, b, a);
   }

   public VertexConsumer setUv(float u, float v) {
      return this.prior.setUv(u, v);
   }

   public VertexConsumer setUv1(int u, int v) {
      return this.prior.setUv1(u, v);
   }

   public VertexConsumer setOverlay(int uv) {
      return this.prior.setUv1(uv & 65535, uv >> 16 & 65535);
   }

   public VertexConsumer setUv2(int u, int v) {
      return this.prior.setUv2(u, v);
   }

   public VertexConsumer setNormal(float x, float y, float z) {
      return this.prior.setNormal(this.pose.last(), x, y, z);
   }
}
