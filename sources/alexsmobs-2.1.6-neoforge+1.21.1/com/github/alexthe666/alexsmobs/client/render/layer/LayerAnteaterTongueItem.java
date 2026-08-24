package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelAnteater;
import com.github.alexthe666.alexsmobs.client.model.ModelLeafcutterAnt;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.client.render.RenderAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerAnteaterTongueItem extends RenderLayer<EntityAnteater, ModelAnteater> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/leafcutter_ant.png");
   private final ModelLeafcutterAnt ANT_MODEL = new ModelLeafcutterAnt();

   public LayerAnteaterTongueItem(RenderAnteater render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityAnteater anteater,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ItemStack itemstack = anteater.getMainHandItem();
      if (!itemstack.isEmpty() || anteater.hasAntOnTongue()) {
         double tongueM = Math.min(Math.sin(ageInTicks * 0.15F), 0.0);
         float scaleItem = -0.2F
            * (float)tongueM
            * (anteater.prevTongueProgress + (anteater.tongueProgress - anteater.prevTongueProgress) * partialTicks * 0.2F);
         matrixStackIn.pushPose();
         if (anteater.isBaby()) {
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0, 2.8, 0.0);
         }

         matrixStackIn.pushPose();
         this.translateToTongue(matrixStackIn);
         if (anteater.isBaby()) {
            matrixStackIn.translate(0.0, 0.20000000298023224, -0.22);
         }

         matrixStackIn.translate(-0.0, 0.0, -0.3499999940395355);
         matrixStackIn.scale(scaleItem, scaleItem, scaleItem);
         if (anteater.hasAntOnTongue()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0F, -1.35F, -0.01F);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
            this.ANT_MODEL.animateAnteater(anteater, partialTicks);
            this.ANT_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
         } else {
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            AMRenderCompat.renderItemInHand(renderer, anteater, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         }

         matrixStackIn.popPose();
         matrixStackIn.popPose();
      }
   }

   protected void translateToTongue(PoseStack matrixStack) {
      ((ModelAnteater)this.getParentModel()).root.translateAndRotate(matrixStack);
      ((ModelAnteater)this.getParentModel()).body.translateAndRotate(matrixStack);
      ((ModelAnteater)this.getParentModel()).head.translateAndRotate(matrixStack);
      ((ModelAnteater)this.getParentModel()).snout.translateAndRotate(matrixStack);
      ((ModelAnteater)this.getParentModel()).tongue1.translateAndRotate(matrixStack);
      ((ModelAnteater)this.getParentModel()).tongue2.translateAndRotate(matrixStack);
   }
}
