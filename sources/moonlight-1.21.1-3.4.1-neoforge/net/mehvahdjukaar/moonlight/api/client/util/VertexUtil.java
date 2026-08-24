package net.mehvahdjukaar.moonlight.api.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class VertexUtil {
   private static final Direction[] DIRS = new Direction[]{
      Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN, null
   };

   public static void addCube(VertexConsumer builder, PoseStack poseStack, float width, float height, int light, int color) {
      addCube(builder, poseStack, 0.0F, 0.0F, width, height, light, color);
   }

   public static void addCube(VertexConsumer builder, PoseStack poseStack, float uOff, float vOff, float width, float height, int light, int color) {
      addCube(builder, poseStack, uOff, vOff, width, height, light, color, 1.0F, true, true, false);
   }

   public static void addCube(
      VertexConsumer builder,
      PoseStack poseStack,
      float uOff,
      float vOff,
      float w,
      float h,
      int combinedLightIn,
      int color,
      float alpha,
      boolean up,
      boolean down,
      boolean wrap
   ) {
      addCube(builder, poseStack, uOff, 1.0F - (vOff + h), uOff + w, 1.0F - vOff, w, h, combinedLightIn, color, alpha, up, down, wrap);
   }

   public static void addCube(
      VertexConsumer builder,
      PoseStack poseStack,
      float minU,
      float minV,
      float maxU,
      float maxV,
      float w,
      float h,
      int combinedLightIn,
      int color,
      float alpha,
      boolean up,
      boolean down,
      boolean wrap
   ) {
      int lu = combinedLightIn & 65535;
      int lv = combinedLightIn >> 16 & 65535;
      float minV2 = maxV - w;
      int r = ARGB32.red(color);
      int g = ARGB32.green(color);
      int b = ARGB32.blue(color);
      int a = (int)(255.0F * alpha);
      float hw = w / 2.0F;
      float hh = h / 2.0F;
      float inc = 0.0F;
      poseStack.pushPose();
      poseStack.translate(0.0F, hh, 0.0F);

      for (Direction d : Direction.values()) {
         float v0 = minV;
         float t = hw;
         float y0 = -hh;
         float y1 = hh;
         float i = inc;
         if (d.getAxis() == Axis.Y) {
            if (!up && d == Direction.UP || !down) {
               continue;
            }

            t = hh;
            y0 = -hw;
            y1 = hw;
            v0 = minV2;
         } else if (wrap) {
            inc += w;
         }

         poseStack.pushPose();
         poseStack.mulPose(RotHlpr.rot(d));
         poseStack.translate(0.0F, 0.0F, -t);
         addQuad(builder, poseStack, -hw, y0, hw, y1, minU + i, v0, maxU + i, maxV, r, g, b, a, lu, lv);
         poseStack.popPose();
      }

      poseStack.popPose();
   }

   public static void addQuad(VertexConsumer builder, PoseStack poseStack, float x0, float y0, float x1, float y1, int lu, int lv) {
      addQuad(builder, poseStack, x0, y0, x1, y1, 255, 255, 255, 255, lu, lv);
   }

   public static void addQuad(VertexConsumer builder, PoseStack poseStack, float x0, float y0, float x1, float y1, int r, int g, int b, int a, int lu, int lv) {
      addQuad(builder, poseStack, x0, y0, x1, y1, 0.0F, 0.0F, 1.0F, 1.0F, r, g, b, a, lu, lv);
   }

   public static void addQuad(
      VertexConsumer builder,
      PoseStack poseStack,
      float x0,
      float y0,
      float x1,
      float y1,
      float u0,
      float v0,
      float u1,
      float v1,
      int r,
      int g,
      int b,
      int a,
      int lu,
      int lv
   ) {
      Pose last = poseStack.last();
      Vector3f vector3f = last.normal().transform(new Vector3f(0.0F, 0.0F, -1.0F));
      float nx = vector3f.x;
      float ny = vector3f.y;
      float nz = vector3f.z;
      vertF(builder, poseStack, x0, y1, 0.0F, u1, v0, r, g, b, a, lu, lv, nx, ny, nz);
      vertF(builder, poseStack, x1, y1, 0.0F, u0, v0, r, g, b, a, lu, lv, nx, ny, nz);
      vertF(builder, poseStack, x1, y0, 0.0F, u0, v1, r, g, b, a, lu, lv, nx, ny, nz);
      vertF(builder, poseStack, x0, y0, 0.0F, u1, v1, r, g, b, a, lu, lv, nx, ny, nz);
   }

   public static void vert(
      VertexConsumer builder,
      PoseStack poseStack,
      float x,
      float y,
      float z,
      float u,
      float v,
      float r,
      float g,
      float b,
      float a,
      int lu,
      int lv,
      float nx,
      float ny,
      float nz
   ) {
      builder.addVertex(poseStack.last().pose(), x, y, z);
      builder.setColor(r, g, b, a);
      builder.setUv(u, v);
      builder.setUv1(0, 10);
      builder.setUv2(lu, lv);
      builder.setNormal(poseStack.last(), nx, ny, nz);
   }

   private static void vertF(
      VertexConsumer builder,
      PoseStack poseStack,
      float x,
      float y,
      float z,
      float u,
      float v,
      int r,
      int g,
      int b,
      int a,
      int lu,
      int lv,
      float nx,
      float ny,
      float nz
   ) {
      builder.addVertex(poseStack.last().pose(), x, y, z);
      builder.setColor(r, g, b, a);
      builder.setUv(u, v);
      builder.setUv1(0, 10);
      builder.setUv2(lu, lv);
      builder.setNormal(nx, ny, nz);
   }

   private static void vertF(
      VertexConsumer builder, PoseStack poseStack, float x, float y, float z, float u, float v, int color, int lu, int lv, float nx, float ny, float nz
   ) {
      builder.addVertex(poseStack.last().pose(), x, y, z);
      builder.setColor(color);
      builder.setUv(u, v);
      builder.setUv1(0, 10);
      builder.setUv2(lu, lv);
      builder.setNormal(nx, ny, nz);
   }

   public static int lightU(int light) {
      return light & 65535;
   }

   public static int lightV(int light) {
      return light >> 16 & 65535;
   }

   public static List<BakedQuad> getAllModelQuads(BakedModel model, BlockState state, RandomSource rand) {
      List<BakedQuad> allQuads = new ArrayList<>();

      for (Direction d : DIRS) {
         allQuads.addAll(model.getQuads(state, d, rand));
      }

      return allQuads;
   }
}
