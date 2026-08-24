package at.petrak.hexcasting.client.entity;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.client.render.PatternTextureManager;
import at.petrak.hexcasting.client.render.RenderLib;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class WallScrollRenderer extends EntityRenderer<EntityWallScroll> {
   private static final ResourceLocation PRISTINE_BG_LARGE = HexAPI.modLoc("textures/entity/scroll_large.png");
   private static final ResourceLocation PRISTINE_BG_MEDIUM = HexAPI.modLoc("textures/entity/scroll_medium.png");
   private static final ResourceLocation PRISTINE_BG_SMOL = HexAPI.modLoc("textures/block/scroll_paper.png");
   private static final ResourceLocation ANCIENT_BG_LARGE = HexAPI.modLoc("textures/entity/scroll_ancient_large.png");
   private static final ResourceLocation ANCIENT_BG_MEDIUM = HexAPI.modLoc("textures/entity/scroll_ancient_medium.png");
   private static final ResourceLocation ANCIENT_BG_SMOL = HexAPI.modLoc("textures/block/ancient_scroll_paper.png");
   private static final ResourceLocation WHITE = HexAPI.modLoc("textures/entity/white.png");

   public WallScrollRenderer(Context p_174008_) {
      super(p_174008_);
   }

   public void render(EntityWallScroll wallScroll, float yaw, float partialTicks, PoseStack ps, MultiBufferSource bufSource, int packedLight) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      ps.pushPose();
      ps.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
      ps.mulPose(Axis.ZP.rotationDegrees(180.0F));
      int light = LevelRenderer.getLightColor(wallScroll.level(), wallScroll.getPos());
      ps.pushPose();
      ps.translate(-wallScroll.blockSize / 2.0F, -wallScroll.blockSize / 2.0F, 0.03125F);
      float dx = wallScroll.blockSize;
      float dy = wallScroll.blockSize;
      float dz = -0.0625F;
      float margin = 0.020833334F;
      Pose last = ps.last();
      Matrix4f mat = last.pose();
      Matrix3f norm = last.normal();
      VertexConsumer verts = bufSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(wallScroll)));
      vertex(mat, norm, light, verts, 0.0F, 0.0F, dz, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F);
      vertex(mat, norm, light, verts, 0.0F, dy, dz, 0.0F, 1.0F, 0.0F, 0.0F, -1.0F);
      vertex(mat, norm, light, verts, dx, dy, dz, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F);
      vertex(mat, norm, light, verts, dx, 0.0F, dz, 1.0F, 0.0F, 0.0F, 0.0F, -1.0F);
      vertex(mat, norm, light, verts, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
      vertex(mat, norm, light, verts, dx, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
      vertex(mat, norm, light, verts, dx, dy, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
      vertex(mat, norm, light, verts, 0.0F, dy, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F);
      vertex(mat, norm, light, verts, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F);
      vertex(mat, norm, light, verts, 0.0F, 0.0F, dz, 0.0F, margin, 0.0F, -1.0F, 0.0F);
      vertex(mat, norm, light, verts, dx, 0.0F, dz, 1.0F, margin, 0.0F, -1.0F, 0.0F);
      vertex(mat, norm, light, verts, dx, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -1.0F, 0.0F);
      vertex(mat, norm, light, verts, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F);
      vertex(mat, norm, light, verts, 0.0F, dy, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F);
      vertex(mat, norm, light, verts, 0.0F, dy, dz, margin, 1.0F, -1.0F, 0.0F, 0.0F);
      vertex(mat, norm, light, verts, 0.0F, 0.0F, dz, margin, 0.0F, -1.0F, 0.0F, 0.0F);
      vertex(mat, norm, light, verts, dx, 0.0F, dz, 1.0F - margin, 0.0F, 1.0F, 0.0F, 0.0F);
      vertex(mat, norm, light, verts, dx, dy, dz, 1.0F - margin, 1.0F, 1.0F, 0.0F, 0.0F);
      vertex(mat, norm, light, verts, dx, dy, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F);
      vertex(mat, norm, light, verts, dx, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F);
      vertex(mat, norm, light, verts, 0.0F, dy, dz, 0.0F, 1.0F - margin, 0.0F, 1.0F, 0.0F);
      vertex(mat, norm, light, verts, 0.0F, dy, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F);
      vertex(mat, norm, light, verts, dx, dy, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F);
      vertex(mat, norm, light, verts, dx, dy, dz, 1.0F, 1.0F - margin, 0.0F, 1.0F, 0.0F);
      ps.popPose();
      if (PatternTextureManager.useTextures && wallScroll.points != null) {
         PatternTextureManager.renderPatternForScroll(
            wallScroll.points.pointsKey, ps, bufSource, light, wallScroll.points.zappyPoints, wallScroll.blockSize, wallScroll.getShowsStrokeOrder()
         );
      }

      if (!PatternTextureManager.useTextures && wallScroll.points != null) {
         List<Vec2> points = wallScroll.points.zappyPoints;
         ps.pushPose();
         ps.mulPose(Axis.YP.rotationDegrees(180.0F));
         ps.translate(0.0F, 0.0F, 0.06875F);
         dy = Mth.sqrt(wallScroll.blockSize * wallScroll.blockSize + 60);
         dz = 0.0033333334F * dy;
         ps.scale(dz, dz, 0.01F);
         Pose lastx = ps.last();
         Matrix4f matx = lastx.pose();
         Matrix3f normx = lastx.normal();
         int outer = -2963256;
         int inner = -936236237;
         VertexConsumer vertsx = bufSource.getBuffer(RenderType.entityCutout(WHITE));
         theCoolerDrawLineSeq(matx, normx, light, vertsx, points, wallScroll.blockSize * 5.0F / 3.0F, outer);
         ps.translate(0.0, 0.0, 0.01);
         theCoolerDrawLineSeq(matx, normx, light, vertsx, points, wallScroll.blockSize * 2.0F / 3.0F, inner);
         if (wallScroll.getShowsStrokeOrder()) {
            ps.translate(0.0, 0.0, 0.01);
            float spotFrac = 0.8F * wallScroll.blockSize;
            theCoolerDrawSpot(matx, normx, light, vertsx, points.get(0), 0.6666667F * spotFrac, -10781737);
         }

         ps.popPose();
      }

      ps.popPose();
      super.render(wallScroll, yaw, partialTicks, ps, bufSource, packedLight);
   }

   public ResourceLocation getTextureLocation(EntityWallScroll wallScroll) {
      if (wallScroll.isAncient) {
         if (wallScroll.blockSize <= 1) {
            return ANCIENT_BG_SMOL;
         } else {
            return wallScroll.blockSize == 2 ? ANCIENT_BG_MEDIUM : ANCIENT_BG_LARGE;
         }
      } else if (wallScroll.blockSize <= 1) {
         return PRISTINE_BG_SMOL;
      } else {
         return wallScroll.blockSize == 2 ? PRISTINE_BG_MEDIUM : PRISTINE_BG_LARGE;
      }
   }

   private static void vertex(
      Matrix4f mat, Matrix3f normal, int light, VertexConsumer verts, float x, float y, float z, float u, float v, float nx, float ny, float nz
   ) {
      verts.addVertex(mat, x, y, z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
   }

   private static void vertexCol(Matrix4f mat, Matrix3f normal, int light, VertexConsumer verts, int col, Vec2 pos) {
      verts.addVertex(mat, -pos.x, pos.y, 0.0F)
         .setColor(col)
         .setUv(0.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 0.0F, 1.0F);
   }

   private static void theCoolerDrawLineSeq(Matrix4f mat, Matrix3f normalMat, int light, VertexConsumer verts, List<Vec2> points, float width, int color) {
      if (points.size() > 1) {
         float[] joinAngles = new float[points.size()];
         float[] joinOffsets = new float[points.size()];

         for (int i = 2; i < points.size(); i++) {
            Vec2 p0 = points.get(i - 2);
            Vec2 p1 = points.get(i - 1);
            Vec2 p2 = points.get(i);
            Vec2 prev = p1.add(p0.negated());
            Vec2 next = p2.add(p1.negated());
            float angle = (float)Mth.atan2(prev.x * next.y - prev.y * next.x, prev.x * next.x + prev.y * next.y);
            joinAngles[i - 1] = angle;
            float clamp = Math.min(prev.length(), next.length()) / (width * 0.5F);
            joinOffsets[i - 1] = Mth.clamp(Mth.sin(angle) / (1.0F + Mth.cos(angle)), -clamp, clamp);
         }

         for (int i = 0; i < points.size() - 1; i++) {
            Vec2 p1 = points.get(i);
            Vec2 p2 = points.get(i + 1);
            Vec2 tangent = p2.add(p1.negated()).normalized().scale(width * 0.5F);
            Vec2 normal = new Vec2(-tangent.y, tangent.x);
            float jlow = joinOffsets[i];
            float jhigh = joinOffsets[i + 1];
            Vec2 p1Down = p1.add(tangent.scale(Math.max(0.0F, jlow))).add(normal);
            Vec2 p1Up = p1.add(tangent.scale(Math.max(0.0F, -jlow))).add(normal.negated());
            Vec2 p2Down = p2.add(tangent.scale(Math.max(0.0F, jhigh)).negated()).add(normal);
            Vec2 p2Up = p2.add(tangent.scale(Math.max(0.0F, -jhigh)).negated()).add(normal.negated());
            vertexCol(mat, normalMat, light, verts, color, p1);
            vertexCol(mat, normalMat, light, verts, color, p2);
            vertexCol(mat, normalMat, light, verts, color, p2Up);
            vertexCol(mat, normalMat, light, verts, color, p1Up);
            vertexCol(mat, normalMat, light, verts, color, p1);
            vertexCol(mat, normalMat, light, verts, color, p1Down);
            vertexCol(mat, normalMat, light, verts, color, p2Down);
            vertexCol(mat, normalMat, light, verts, color, p2);
            if (i > 0) {
               float sangle = joinAngles[i];
               float angle = Math.abs(sangle);
               Vec2 rnormal = normal.negated();
               int joinSteps = Mth.ceil(angle * 180.0F / 56.548668F);
               if (joinSteps >= 1) {
                  if (sangle < 0.0F) {
                     Vec2 prevVert = new Vec2(p1.x - rnormal.x, p1.y - rnormal.y);

                     for (int j = 1; j <= joinSteps; j++) {
                        Vec2 fan = RenderLib.rotate(rnormal, -sangle * ((float)j / joinSteps));
                        Vec2 fanShift = new Vec2(p1.x - fan.x, p1.y - fan.y);
                        vertexCol(mat, normalMat, light, verts, color, p1);
                        vertexCol(mat, normalMat, light, verts, color, p1);
                        vertexCol(mat, normalMat, light, verts, color, fanShift);
                        vertexCol(mat, normalMat, light, verts, color, prevVert);
                        prevVert = fanShift;
                     }
                  } else {
                     Vec2 startFan = RenderLib.rotate(normal, -sangle);
                     Vec2 prevVert = new Vec2(p1.x - startFan.x, p1.y - startFan.y);

                     for (int j = joinSteps - 1; j >= 0; j--) {
                        Vec2 fan = RenderLib.rotate(normal, -sangle * ((float)j / joinSteps));
                        Vec2 fanShift = new Vec2(p1.x - fan.x, p1.y - fan.y);
                        vertexCol(mat, normalMat, light, verts, color, p1);
                        vertexCol(mat, normalMat, light, verts, color, p1);
                        vertexCol(mat, normalMat, light, verts, color, fanShift);
                        vertexCol(mat, normalMat, light, verts, color, prevVert);
                        prevVert = fanShift;
                     }
                  }
               }
            }
         }

         for (Vec2[] pair : new Vec2[][]{{points.get(0), points.get(1)}, {points.get(points.size() - 1), points.get(points.size() - 2)}}) {
            Vec2 point = pair[0];
            Vec2 prev = pair[1];
            Vec2 tangent = point.add(prev.negated()).normalized().scale(0.5F * width);
            Vec2 normal = new Vec2(-tangent.y, tangent.x);
            int joinSteps = Mth.ceil(10.0F);

            for (int j = joinSteps; j > 0; j--) {
               Vec2 fan0 = RenderLib.rotate(normal, -3.1415927F * ((float)j / joinSteps));
               Vec2 fan1 = RenderLib.rotate(normal, -3.1415927F * ((float)(j - 1) / joinSteps));
               vertexCol(mat, normalMat, light, verts, color, point);
               vertexCol(mat, normalMat, light, verts, color, point);
               vertexCol(mat, normalMat, light, verts, color, point.add(fan1));
               vertexCol(mat, normalMat, light, verts, color, point.add(fan0));
            }
         }
      }
   }

   private static void theCoolerDrawSpot(Matrix4f mat, Matrix3f normal, int light, VertexConsumer verts, Vec2 point, float radius, int color) {
      int fracOfCircle = 6;

      for (int i = 0; i < fracOfCircle; i++) {
         vertexCol(mat, normal, light, verts, color, point);
         vertexCol(mat, normal, light, verts, color, point);

         for (int j = 0; j <= 1; j++) {
            float theta = (float)(i - j) / fracOfCircle * 6.2831855F;
            float rx = Mth.cos(theta) * radius + point.x;
            float ry = Mth.sin(theta) * radius + point.y;
            vertexCol(mat, normal, light, verts, color, new Vec2(rx, ry));
         }
      }
   }
}
