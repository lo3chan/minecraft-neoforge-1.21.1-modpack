package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderVoidPortal extends EntityRenderer<EntityVoidPortal> {
   private static final ResourceLocation TEXTURE_0 = AMCompat.rl("alexsmobs:textures/entity/void_worm/portal/portal_idle_0.png");
   private static final ResourceLocation TEXTURE_1 = AMCompat.rl("alexsmobs:textures/entity/void_worm/portal/portal_idle_1.png");
   private static final ResourceLocation TEXTURE_2 = AMCompat.rl("alexsmobs:textures/entity/void_worm/portal/portal_idle_2.png");
   private static final ResourceLocation TEXTURE_SHATTERED_0 = AMCompat.rl("alexsmobs:textures/entity/void_worm/portal/shattered/portal_idle_0.png");
   private static final ResourceLocation TEXTURE_SHATTERED_1 = AMCompat.rl("alexsmobs:textures/entity/void_worm/portal/shattered/portal_idle_1.png");
   private static final ResourceLocation TEXTURE_SHATTERED_2 = AMCompat.rl("alexsmobs:textures/entity/void_worm/portal/shattered/portal_idle_2.png");
   private static final ResourceLocation[] TEXTURE_PROGRESS = new ResourceLocation[10];
   private static final ResourceLocation[] TEXTURE_SHATTERED_PROGRESS = new ResourceLocation[10];
   private static final int NOISE_VARIANTS = 4;
   private static final ResourceLocation[][] TEXTURE_SHATTERED_IDLE_STATIC = new ResourceLocation[3][4];
   private static final ResourceLocation[][] TEXTURE_SHATTERED_PROGRESS_STATIC = new ResourceLocation[10][4];

   public RenderVoidPortal(Context renderManagerIn) {
      super(renderManagerIn);

      for (int i = 0; i < 10; i++) {
         TEXTURE_PROGRESS[i] = AMCompat.rl("alexsmobs:textures/entity/void_worm/portal/portal_grow_" + i + ".png");
         TEXTURE_SHATTERED_PROGRESS[i] = AMCompat.rl("alexsmobs:textures/entity/void_worm/portal/shattered/portal_grow_" + i + ".png");
      }
   }

   public void render(EntityVoidPortal entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(entityIn.getAttachmentFacing().getOpposite().getRotation());
      matrixStackIn.translate(0.5, 0.0, 0.5);
      matrixStackIn.scale(2.0F, 2.0F, 2.0F);
      this.renderPortal(entityIn, matrixStackIn, bufferIn, false);
      if (entityIn.isShattered()) {
         float off = 0.01F;
         matrixStackIn.pushPose();
         matrixStackIn.translate(0.0F, off, 0.0F);
         this.renderPortal(entityIn, matrixStackIn, bufferIn, true);
         matrixStackIn.popPose();
         matrixStackIn.pushPose();
         matrixStackIn.translate(0.0F, -off, 0.0F);
         this.renderPortal(entityIn, matrixStackIn, bufferIn, true);
         matrixStackIn.popPose();
      }

      matrixStackIn.popPose();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   private void renderPortal(EntityVoidPortal entityIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, boolean shattered) {
      int variant = Math.floorMod(entityIn.tickCount / 2, 4);
      ResourceLocation tex;
      ResourceLocation baked;
      if (entityIn.getLifespan() < 20) {
         int frame = Mth.clamp((int)(entityIn.getLifespan() * 0.5F % 10.0F), 0, 9);
         tex = this.getGrowingTexture(frame, shattered);
         baked = TEXTURE_SHATTERED_PROGRESS_STATIC[frame][variant];
      } else if (entityIn.tickCount < 20) {
         int frame = Mth.clamp((int)(entityIn.tickCount * 0.5F % 10.0F), 0, 9);
         tex = this.getGrowingTexture(frame, shattered);
         baked = TEXTURE_SHATTERED_PROGRESS_STATIC[frame][variant];
      } else {
         int age = entityIn.tickCount % 9;
         tex = this.getIdleTexture(age, shattered);
         baked = TEXTURE_SHATTERED_IDLE_STATIC[idleIndex(age)][variant];
      }

      if (shattered) {
         AMRenderTypes.renderStaticMasked(
            bufferIn, AMRenderTypes.STATIC_PORTAL, RenderType.entityCutoutNoCull(tex), baked, ivertexbuilder -> this.renderArc(matrixStackIn, ivertexbuilder)
         );
      } else {
         this.renderArc(matrixStackIn, bufferIn.getBuffer(AMRenderTypes.getFullBright(tex)));
      }
   }

   private static int idleIndex(int age) {
      if (age < 3) {
         return 0;
      } else if (age < 6) {
         return 1;
      } else {
         return age < 10 ? 2 : 0;
      }
   }

   private void renderArc(PoseStack matrixStackIn, VertexConsumer ivertexbuilder) {
      matrixStackIn.pushPose();
      Pose lvt_19_1_ = matrixStackIn.last();
      Matrix4f lvt_20_1_ = lvt_19_1_.pose();
      Matrix3f lvt_21_1_ = lvt_19_1_.normal();
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, -1, 0.0F, 0.0F, 1, 0, 1, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, 1, 0.0F, 1.0F, 1, 0, 1, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, 1, 1.0F, 1.0F, 1, 0, 1, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, -1, 1.0F, 0.0F, 1, 0, 1, 240);
      matrixStackIn.popPose();
   }

   public ResourceLocation getTextureLocation(EntityVoidPortal entity) {
      return TEXTURE_0;
   }

   public void drawVertex(
      Matrix4f p_229039_1_,
      Matrix3f p_229039_2_,
      VertexConsumer p_229039_3_,
      int p_229039_4_,
      int p_229039_5_,
      int p_229039_6_,
      float p_229039_7_,
      float p_229039_8_,
      int p_229039_9_,
      int p_229039_10_,
      int p_229039_11_,
      int p_229039_12_
   ) {
      AMVertex.normal(
         p_229039_3_.addVertex(p_229039_1_, p_229039_4_, p_229039_5_, p_229039_6_)
            .setColor(255, 255, 255, 255)
            .setUv(p_229039_7_, p_229039_8_)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(p_229039_12_),
         p_229039_2_,
         p_229039_9_,
         p_229039_11_,
         p_229039_10_
      );
   }

   public ResourceLocation getIdleTexture(int age, boolean shattered) {
      switch (idleIndex(age)) {
         case 1:
            return shattered ? TEXTURE_SHATTERED_1 : TEXTURE_1;
         case 2:
            return shattered ? TEXTURE_SHATTERED_2 : TEXTURE_2;
         default:
            return shattered ? TEXTURE_SHATTERED_0 : TEXTURE_0;
      }
   }

   public ResourceLocation getGrowingTexture(int age, boolean shattered) {
      return shattered ? TEXTURE_SHATTERED_PROGRESS[Mth.clamp(age, 0, 9)] : TEXTURE_PROGRESS[Mth.clamp(age, 0, 9)];
   }

   static {
      for (int v = 0; v < 4; v++) {
         for (int i = 0; i < 3; i++) {
            TEXTURE_SHATTERED_IDLE_STATIC[i][v] = AMCompat.rl("alexsmobs:textures/entity/void_worm/portal/shattered/portal_idle_" + i + "_static_" + v + ".png");
         }

         for (int i = 0; i < 10; i++) {
            TEXTURE_SHATTERED_PROGRESS_STATIC[i][v] = AMCompat.rl(
               "alexsmobs:textures/entity/void_worm/portal/shattered/portal_grow_" + i + "_static_" + v + ".png"
            );
         }
      }
   }
}
