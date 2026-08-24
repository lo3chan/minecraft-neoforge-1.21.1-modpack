package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSeal;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerSealItem;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.ClientHooks;

public class RenderSeal extends MobRenderer<EntitySeal, ModelSeal> {
   private static final ResourceLocation TEXTURE_BROWN_0 = AMCompat.rl("alexsmobs:textures/entity/seal/seal_brown_0.png");
   private static final ResourceLocation TEXTURE_BROWN_1 = AMCompat.rl("alexsmobs:textures/entity/seal/seal_brown_1.png");
   private static final ResourceLocation TEXTURE_ARCTIC_0 = AMCompat.rl("alexsmobs:textures/entity/seal/seal_arctic_0.png");
   private static final ResourceLocation TEXTURE_ARCTIC_1 = AMCompat.rl("alexsmobs:textures/entity/seal/seal_arctic_1.png");
   private static final ResourceLocation TEXTURE_ARCTIC_BABY = AMCompat.rl("alexsmobs:textures/entity/seal/seal_arctic_baby.png");
   private static final ResourceLocation TEXTURE_TEARS = AMCompat.rl("alexsmobs:textures/entity/seal/seal_crying.png");
   private static final ResourceLocation TEXTURE_TONGUE = AMCompat.rl("alexsmobs:textures/entity/seal/seal_tongue.png");

   public RenderSeal(Context renderManagerIn) {
      super(renderManagerIn, new ModelSeal(), 0.45F);
      this.addLayer(new LayerSealItem(this));
      this.addLayer(new RenderSeal.SealTearsLayer(this));
   }

   protected boolean shouldShowName(EntitySeal seal) {
      return super.shouldShowName(seal) || seal.isTearsEasterEgg();
   }

   public ResourceLocation getTextureLocation(EntitySeal entity) {
      if (entity.isArctic()) {
         return entity.isBaby() ? TEXTURE_ARCTIC_BABY : (entity.getVariant() == 1 ? TEXTURE_ARCTIC_1 : TEXTURE_ARCTIC_0);
      } else {
         return entity.getVariant() == 1 ? TEXTURE_BROWN_1 : TEXTURE_BROWN_0;
      }
   }

   protected void renderNameTag(EntitySeal seal, Component text, PoseStack poseStack, MultiBufferSource bufferSrc, int numberIn, float partialTick) {
      if (seal.isTearsEasterEgg()) {
         double d0 = this.entityRenderDispatcher.distanceToSqr(seal);
         if (ClientHooks.isNameplateInRenderDistance(seal, d0)) {
            boolean flag = !seal.isDiscrete();
            float f = seal.getBbHeight() + 0.5F;
            String[] split = text.getString(512).split(" ");
            StringBuilder recombined = new StringBuilder();
            List<String> strings = new ArrayList<>();

            for (int wordIndex = 0; wordIndex < split.length; wordIndex++) {
               recombined.append(split[wordIndex]).append(" ");
               if (recombined.length() > 15 || wordIndex == split.length - 1) {
                  strings.add(recombined.toString());
                  recombined = new StringBuilder();
               }
            }

            int i = 10 - 10 * strings.size();
            poseStack.pushPose();
            poseStack.translate(0.0, f, 0.0);
            poseStack.mulPose(AMRenderCompat.cameraOrientation(this.entityRenderDispatcher));
            poseStack.scale(-0.025F, -0.025F, 0.025F);
            float f1 = 1.0F;
            int j = -1;
            Font font = this.getFont();
            String widest = "";

            for (String print : strings) {
               if (font.width(widest) < font.width(print)) {
                  widest = print;
               }
            }

            float widestCenter = -font.width(widest) / 2;

            for (String printx : strings) {
               float f2 = -font.width(printx) / 2;
               poseStack.translate(0.0, 0.0, 0.1);
               AMRenderCompat.drawTextInBatch(font, widest, widestCenter, i, j, false, poseStack, bufferSrc, j, 240);
               poseStack.translate(0.0, 0.0, -0.1);
               AMRenderCompat.drawTextInBatch(font, printx, f2, i, -16777215, false, poseStack, bufferSrc, j, 240);
               AMRenderCompat.drawTextInBatch(font, printx, f2, i, -16777216, false, poseStack, bufferSrc, j, 240);
               i += 10;
            }

            poseStack.popPose();
         }
      } else {
         super.renderNameTag(seal, text, poseStack, bufferSrc, numberIn, partialTick);
      }
   }

   static class SealTearsLayer extends RenderLayer<EntitySeal, ModelSeal> {
      public SealTearsLayer(RenderSeal p_i50928_1_) {
         super(p_i50928_1_);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntitySeal entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (entitylivingbaseIn.isTearsEasterEgg()) {
            VertexConsumer lead = bufferIn.getBuffer(AMRenderTypes.entityCutoutNoCull(RenderSeal.TEXTURE_TEARS));
            ((ModelSeal)this.getParentModel())
               .renderToBuffer(matrixStackIn, lead, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}
