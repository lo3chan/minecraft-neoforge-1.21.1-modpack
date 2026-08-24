package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelSoulVulture;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderSoulVulture;
import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class LayerSoulVultureGlow extends RenderLayer<EntitySoulVulture, ModelSoulVulture> {
   private static final ResourceLocation TEXTURE_GLOW = AMCompat.rl("alexsmobs:textures/entity/soul_vulture/soul_vulture_glow.png");
   private static final ResourceLocation TEXTURE_0 = AMCompat.rl("alexsmobs:textures/entity/soul_vulture/soul_vulture_flames_0.png");
   private static final ResourceLocation TEXTURE_1 = AMCompat.rl("alexsmobs:textures/entity/soul_vulture/soul_vulture_flames_1.png");
   private static final ResourceLocation TEXTURE_2 = AMCompat.rl("alexsmobs:textures/entity/soul_vulture/soul_vulture_flames_2.png");

   public LayerSoulVultureGlow(RenderSoulVulture renderSoulVulture) {
      super(renderSoulVulture);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntitySoulVulture entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ((ModelSoulVulture)this.getParentModel())
         .renderToBuffer(
            matrixStackIn,
            bufferIn.getBuffer(AMRenderTypes.getGhost(TEXTURE_GLOW)),
            240,
            LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F),
            1.0F,
            1.0F,
            1.0F,
            1.0F
         );
      if (entitylivingbaseIn.hasSoulHeart()) {
         ((ModelSoulVulture)this.getParentModel())
            .renderToBuffer(
               matrixStackIn,
               bufferIn.getBuffer(AMRenderTypes.getGhost(this.getFlames(entitylivingbaseIn.tickCount))),
               240,
               LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F),
               1.0F,
               1.0F,
               1.0F,
               1.0F
            );
      }
   }

   private ResourceLocation getFlames(int tickCount) {
      int i = tickCount / 3 % 3;

      return switch (i) {
         case 1 -> TEXTURE_1;
         case 2 -> TEXTURE_2;
         default -> TEXTURE_0;
      };
   }
}
