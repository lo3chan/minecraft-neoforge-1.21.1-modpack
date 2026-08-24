package dev.tr7zw.waveycapes.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.transition.mc.MathUtil;
import dev.tr7zw.transition.mc.VertexConsumerUtil;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.NMSUtil;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.support.SupportManager;
import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.versionless.CapeMovement;
import dev.tr7zw.waveycapes.versionless.CapeStyle;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.WindMode;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import dev.tr7zw.waveycapes.versionless.util.Vector4;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class CustomCapeRenderer {
   private static final int PART_COUNT = 16;
   private final ModelPart[] customCape = NMSUtil.buildCape(64, 64, x -> 0, y -> y);
   private static final float CAPE_WIDTH = 0.625F;
   private static final float CAPE_HEIGHT = 1.0F;
   private static final float CAPE_DEPTH = 0.0625F;

   public void render(PlayerWrapper capeRenderInfo, CapeRenderer renderer, VertexConsumer vertexConsumer, PoseStack poseStack, int packedLight, float delta) {
      if (ModBase.config.capeStyle == CapeStyle.SMOOTH && renderer.vanillaUvValues()) {
         this.renderSmoothCape(poseStack, vertexConsumer, capeRenderInfo, delta, packedLight);
      } else {
         ModelPart[] parts = this.customCape;

         for (int part = 0; part < 16; part++) {
            ModelPart model = parts[part];
            this.modifyPoseStack(poseStack, capeRenderInfo, delta, part);
            renderer.render(capeRenderInfo, part, model, poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
         }
      }
   }

   private void renderSmoothCape(PoseStack poseStack, VertexConsumer bufferBuilder, PlayerWrapper capeRenderInfo, float delta, int light) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      float alpha = SupportManager.getAlphaSupplier().get();
      Matrix4f[] positionMatrices = new Matrix4f[16];
      Vector3[] frontNormalVecs = new Vector3[16];
      Vector3[] backNormalVecs = new Vector3[16];

      for (int part = 0; part < 16; part++) {
         this.modifyPoseStack(poseStack, capeRenderInfo, delta, part);
         positionMatrices[part] = new Matrix4f(poseStack.last().pose());
         frontNormalVecs[part] = getNormalVec(
            positionMatrices[Math.max(part - 1, 0)],
            positionMatrices[Math.max(part - 1, 0)],
            positionMatrices[part],
            new Vector3(0.3125F, part * 0.0625F, -0.0625F),
            new Vector3(-0.3125F, part * 0.0625F, -0.0625F),
            new Vector3(0.3125F, (part + 1) * 0.0625F, -0.0625F),
            light == 15728880
         );
         backNormalVecs[part] = getNormalVec(
            positionMatrices[Math.max(part - 1, 0)],
            positionMatrices[Math.max(part - 1, 0)],
            positionMatrices[part],
            new Vector3(0.3125F, (part + 1) * 0.0625F, 0.0F),
            new Vector3(-0.3125F, (part + 1) * 0.0625F, 0.0F),
            new Vector3(0.3125F, part * 0.0625F, 0.0F),
            light == 15728880
         );
         poseStack.popPose();
      }

      for (int part = 0; part < 16; part++) {
         if (part == 0) {
            float minU = 0.015625F;
            float maxU = 0.171875F;
            float minV = 0.0F;
            float maxV = 0.03125F;
            Vector3 normalVec = getNormalVec(
               positionMatrices[0],
               positionMatrices[0],
               positionMatrices[0],
               new Vector3(0.3125F, 0.0F, 0.0F),
               new Vector3(-0.3125F, 0.0F, 0.0F),
               new Vector3(0.3125F, 0.0F, 0.0625F),
               light == 15728880
            );
            VertexConsumerUtil.addVertex(
               bufferBuilder,
               positionMatrices[0],
               0.3125F,
               0.0F,
               0.0F,
               maxU,
               maxV,
               OverlayTexture.NO_OVERLAY,
               light,
               normalVec.x,
               normalVec.y,
               normalVec.z,
               alpha
            );
            VertexConsumerUtil.addVertex(
               bufferBuilder,
               positionMatrices[0],
               -0.3125F,
               0.0F,
               0.0F,
               minU,
               maxV,
               OverlayTexture.NO_OVERLAY,
               light,
               normalVec.x,
               normalVec.y,
               normalVec.z,
               alpha
            );
            VertexConsumerUtil.addVertex(
               bufferBuilder,
               positionMatrices[0],
               -0.3125F,
               0.0F,
               -0.0625F,
               minU,
               minV,
               OverlayTexture.NO_OVERLAY,
               light,
               normalVec.x,
               normalVec.y,
               normalVec.z,
               alpha
            );
            VertexConsumerUtil.addVertex(
               bufferBuilder,
               positionMatrices[0],
               0.3125F,
               0.0F,
               -0.0625F,
               maxU,
               minV,
               OverlayTexture.NO_OVERLAY,
               light,
               normalVec.x,
               normalVec.y,
               normalVec.z,
               alpha
            );
         }

         if (part == 15) {
            float minU = 0.171875F;
            float maxU = 0.328125F;
            float minV = 0.0F;
            float maxV = 0.03125F;
            Vector3 normalVec = getNormalVec(
               positionMatrices[part],
               positionMatrices[part],
               positionMatrices[part],
               new Vector3(0.3125F, 1.0F, -0.0625F),
               new Vector3(-0.3125F, 1.0F, -0.0625F),
               new Vector3(0.3125F, 1.0F, 0.0F),
               light == 15728880
            );
            VertexConsumerUtil.addVertex(
               bufferBuilder,
               positionMatrices[part],
               0.3125F,
               1.0F,
               -0.0625F,
               maxU,
               minV,
               OverlayTexture.NO_OVERLAY,
               light,
               normalVec.x,
               normalVec.y,
               normalVec.z,
               alpha
            );
            VertexConsumerUtil.addVertex(
               bufferBuilder,
               positionMatrices[part],
               -0.3125F,
               1.0F,
               -0.0625F,
               minU,
               minV,
               OverlayTexture.NO_OVERLAY,
               light,
               normalVec.x,
               normalVec.y,
               normalVec.z,
               alpha
            );
            VertexConsumerUtil.addVertex(
               bufferBuilder,
               positionMatrices[part],
               -0.3125F,
               1.0F,
               0.0F,
               minU,
               maxV,
               OverlayTexture.NO_OVERLAY,
               light,
               normalVec.x,
               normalVec.y,
               normalVec.z,
               alpha
            );
            VertexConsumerUtil.addVertex(
               bufferBuilder,
               positionMatrices[part],
               0.3125F,
               1.0F,
               0.0F,
               maxU,
               maxV,
               OverlayTexture.NO_OVERLAY,
               light,
               normalVec.x,
               normalVec.y,
               normalVec.z,
               alpha
            );
         }

         float minU = 0.0F;
         float maxU = 0.015625F;
         float minV = 0.03125F * (part + 1);
         float maxV = minV + 0.03125F;
         Vector3 normalVec = getNormalVec(
            positionMatrices[part],
            positionMatrices[part],
            positionMatrices[Math.max(part - 1, 0)],
            new Vector3(-0.3125F, (part + 1) * 0.0625F, 0.0F),
            new Vector3(-0.3125F, (part + 1) * 0.0625F, -0.0625F),
            new Vector3(-0.3125F, part * 0.0625F, 0.0F),
            light == 15728880
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[part],
            -0.3125F,
            (part + 1) * 0.0625F,
            0.0F,
            minU,
            maxV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVec.x,
            normalVec.y,
            normalVec.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[part],
            -0.3125F,
            (part + 1) * 0.0625F,
            -0.0625F,
            maxU,
            maxV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVec.x,
            normalVec.y,
            normalVec.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[Math.max(part - 1, 0)],
            -0.3125F,
            part * 0.0625F,
            -0.0625F,
            maxU,
            minV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVec.x,
            normalVec.y,
            normalVec.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[Math.max(part - 1, 0)],
            -0.3125F,
            part * 0.0625F,
            0.0F,
            minU,
            minV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVec.x,
            normalVec.y,
            normalVec.z,
            alpha
         );
         minU = 0.171875F;
         maxU = 0.1875F;
         normalVec = getNormalVec(
            positionMatrices[part],
            positionMatrices[part],
            positionMatrices[Math.max(part - 1, 0)],
            new Vector3(0.3125F, (part + 1) * 0.0625F, -0.0625F),
            new Vector3(0.3125F, (part + 1) * 0.0625F, 0.0F),
            new Vector3(0.3125F, part * 0.0625F, -0.0625F),
            light == 15728880
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[part],
            0.3125F,
            (part + 1) * 0.0625F,
            -0.0625F,
            minU,
            maxV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVec.x,
            normalVec.y,
            normalVec.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[part],
            0.3125F,
            (part + 1) * 0.0625F,
            0.0F,
            maxU,
            maxV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVec.x,
            normalVec.y,
            normalVec.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[Math.max(part - 1, 0)],
            0.3125F,
            part * 0.0625F,
            0.0F,
            maxU,
            minV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVec.x,
            normalVec.y,
            normalVec.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[Math.max(part - 1, 0)],
            0.3125F,
            part * 0.0625F,
            -0.0625F,
            minU,
            minV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVec.x,
            normalVec.y,
            normalVec.z,
            alpha
         );
         minU = 0.015625F;
         maxU = 0.171875F;
         Vector3 normalVecTop = frontNormalVecs[part].clone().add(frontNormalVecs[Math.max(part - 1, 0)]).div(2.0F);
         Vector3 normalVecBottom = frontNormalVecs[part].clone().add(frontNormalVecs[Math.min(part + 1, 15)]).div(2.0F);
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[Math.max(part - 1, 0)],
            0.3125F,
            part * 0.0625F,
            -0.0625F,
            maxU,
            minV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVecTop.x,
            normalVecTop.y,
            normalVecTop.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[Math.max(part - 1, 0)],
            -0.3125F,
            part * 0.0625F,
            -0.0625F,
            minU,
            minV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVecTop.x,
            normalVecTop.y,
            normalVecTop.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[part],
            -0.3125F,
            (part + 1) * 0.0625F,
            -0.0625F,
            minU,
            maxV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVecBottom.x,
            normalVecBottom.y,
            normalVecBottom.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[part],
            0.3125F,
            (part + 1) * 0.0625F,
            -0.0625F,
            maxU,
            maxV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVecBottom.x,
            normalVecBottom.y,
            normalVecBottom.z,
            alpha
         );
         minU = 0.1875F;
         maxU = 0.34375F;
         normalVecTop = backNormalVecs[part].clone().add(backNormalVecs[Math.max(part - 1, 0)]).div(2.0F);
         normalVecBottom = backNormalVecs[part].clone().add(backNormalVecs[Math.min(part + 1, 15)]).div(2.0F);
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[Math.max(part - 1, 0)],
            0.3125F,
            part * 0.0625F,
            0.0F,
            minU,
            minV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVecTop.x,
            normalVecTop.y,
            normalVecTop.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[Math.max(part - 1, 0)],
            -0.3125F,
            part * 0.0625F,
            0.0F,
            maxU,
            minV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVecTop.x,
            normalVecTop.y,
            normalVecTop.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[part],
            -0.3125F,
            (part + 1) * 0.0625F,
            0.0F,
            maxU,
            maxV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVecBottom.x,
            normalVecBottom.y,
            normalVecBottom.z,
            alpha
         );
         VertexConsumerUtil.addVertex(
            bufferBuilder,
            positionMatrices[part],
            0.3125F,
            (part + 1) * 0.0625F,
            0.0F,
            minU,
            maxV,
            OverlayTexture.NO_OVERLAY,
            light,
            normalVecBottom.x,
            normalVecBottom.y,
            normalVecBottom.z,
            alpha
         );
      }
   }

   private void modifyPoseStack(PoseStack poseStack, PlayerWrapper capeRenderInfo, float h, int part) {
      if (WaveyCapesBase.config.capeMovement != CapeMovement.VANILLA) {
         this.modifyPoseStackSimulation(poseStack, capeRenderInfo, h, part);
      } else {
         this.modifyPoseStackVanilla(poseStack, (AbstractClientPlayer)capeRenderInfo.getEntity(), h, part);
      }
   }

   private void modifyPoseStackSimulation(PoseStack poseStack, PlayerWrapper capeRenderInfo, float delta, int part) {
      Player entity = capeRenderInfo.getEntity();
      BasicSimulation simulation = ((CapeHolder)entity).getSimulation();
      if (simulation == null) {
         poseStack.pushPose();
      } else {
         poseStack.pushPose();
         poseStack.translate(0.0, 0.0, 0.125);
         float x = simulation.getPoints().get(part).getLerpX(delta) - simulation.getPoints().get(0).getLerpX(delta);
         if (x > 0.0F) {
            x = 0.0F;
         }

         float y = simulation.getPoints().get(0).getLerpY(delta) - part - simulation.getPoints().get(part).getLerpY(delta);
         float z = simulation.getPoints().get(0).getLerpZ(delta) - simulation.getPoints().get(part).getLerpZ(delta);
         float sidewaysRotationOffset = 0.0F;
         float partRotation = this.getRotation(delta, part, simulation);
         float height = 0.0F;
         float naturalWindSwing = this.getNatrualWindSwing(part, entity.isUnderWater());
         poseStack.mulPose(MathUtil.XP.rotationDegrees(6.0F + height + naturalWindSwing));
         poseStack.mulPose(MathUtil.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
         poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
         poseStack.translate(-z / 16.0F, y / 16.0F, x / 16.0F);
         poseStack.translate(0.0, 0.03, -0.03);
         poseStack.translate(0.0F, part * 1.0F / 16.0F, part * 0 / 16);
         poseStack.mulPose(MathUtil.XP.rotationDegrees(-partRotation));
         poseStack.translate(0.0F, -part * 1.0F / 16.0F, -part * 0 / 16);
         poseStack.translate(0.0, -0.03, 0.03);
      }
   }

   private float getRotation(float delta, int part, BasicSimulation simulation) {
      return part == 15
         ? this.getRotation(delta, part - 1, simulation)
         : (float)this.getAngle(simulation.getPoints().get(part).getLerpedPos(delta), simulation.getPoints().get(part + 1).getLerpedPos(delta));
   }

   private double getAngle(Vector3 a, Vector3 b) {
      Vector3 angle = b.subtract(a);
      return Math.toDegrees(Math.atan2(angle.x, angle.y)) + 180.0;
   }

   private void modifyPoseStackVanilla(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, float h, int part) {
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, 0.125);
      double d = Mth.lerp(h, abstractClientPlayer.xCloakO, abstractClientPlayer.xCloak) - Mth.lerp(h, abstractClientPlayer.xo, abstractClientPlayer.getX());
      double e = Mth.lerp(h, abstractClientPlayer.yCloakO, abstractClientPlayer.yCloak) - Mth.lerp(h, abstractClientPlayer.yo, abstractClientPlayer.getY());
      double m = Mth.lerp(h, abstractClientPlayer.zCloakO, abstractClientPlayer.zCloak) - Mth.lerp(h, abstractClientPlayer.zo, abstractClientPlayer.getZ());
      float n = abstractClientPlayer.yBodyRotO + abstractClientPlayer.yBodyRot - abstractClientPlayer.yBodyRotO;
      double o = Mth.sin(n * 0.017453292F);
      double p = -Mth.cos(n * 0.017453292F);
      float height = (float)e * 10.0F;
      height = Mth.clamp(height, -6.0F, 32.0F);
      float swing = (float)(d * o + m * p) * easeOutSine(0.0625F * part) * 100.0F;
      swing = Mth.clamp(swing, 0.0F, 150.0F * easeOutSine(0.0625F * part));
      float sidewaysRotationOffset = (float)(d * p - m * o) * 100.0F;
      sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
      float t = Mth.lerp(h, abstractClientPlayer.oBob, abstractClientPlayer.bob);
      height += Mth.sin(Mth.lerp(h, abstractClientPlayer.walkDistO, abstractClientPlayer.walkDist) * 6.0F) * 32.0F * t;
      float naturalWindSwing = this.getNatrualWindSwing(part, abstractClientPlayer.isUnderWater());
      poseStack.mulPose(MathUtil.XP.rotationDegrees(6.0F + swing / 2.0F + height + naturalWindSwing));
      poseStack.mulPose(MathUtil.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
      poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
   }

   private static float easeOutSine(float x) {
      return Mth.sin((float)(x * 3.141592653589793 / 2.0));
   }

   private float getNatrualWindSwing(int part, boolean underwater) {
      long highlightedPart = System.currentTimeMillis() / (underwater ? 9 : 3) % 360L;
      float relativePart = (part + 1) / 16.0F;
      return WaveyCapesBase.config.windMode == WindMode.WAVES ? (float)(Math.sin(Math.toRadians(relativePart * 360.0F - (float)highlightedPart)) * 3.0) : 0.0F;
   }

   private static Vector3 getNormalVec(Matrix4f matrix1, Matrix4f matrix2, Matrix4f matrix3, Vector3 vector1, Vector3 vector2, Vector3 vector3, boolean inverse) {
      Vector3 vector1Transformed = transform(matrix1, new Vector4(vector1.x, vector1.y, vector1.z, 1.0F)).toVec3();
      Vector3 vector2Transformed = transform(matrix2, new Vector4(vector2.x, vector2.y, vector2.z, 1.0F)).toVec3();
      Vector3 vector3Transformed = transform(matrix3, new Vector4(vector3.x, vector3.y, vector3.z, 1.0F)).toVec3();
      vector2Transformed.subtract(vector1Transformed);
      vector3Transformed.subtract(vector1Transformed);
      vector2Transformed.cross(vector3Transformed);
      vector2Transformed.normalize();
      return inverse ? vector2Transformed.mul(-1.0F) : vector2Transformed;
   }

   private static Vector4 transform(Matrix4f matrix, Vector4 vector) {
      Vector4f vector4f = matrix.transform(new Vector4f(vector.x, vector.y, vector.z, vector.w));
      return new Vector4(vector4f.x, vector4f.y, vector4f.z, vector4f.w);
   }
}
