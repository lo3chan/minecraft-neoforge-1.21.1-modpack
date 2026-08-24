package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.model.layered.ModelSombrero;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.client.render.RenderCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class LayerCockroachMaracas extends RenderLayer<EntityCockroach, ModelCockroach> {
   private ItemStack maracaStack;
   private final ModelSombrero sombrero;
   private static final ResourceLocation SOMBRERO_TEX = AMCompat.rl("alexsmobs:textures/armor/sombrero.png");

   public LayerCockroachMaracas(RenderCockroach render, Context renderManagerIn) {
      super(render);
      this.sombrero = new ModelSombrero(renderManagerIn.bakeLayer(AMModelLayers.SOMBRERO));
      this.sombrero.young = false;
   }

   private ItemStack maracas() {
      if (this.maracaStack == null) {
         this.maracaStack = new ItemStack((ItemLike)AMItemRegistry.MARACA.get());
      }

      return this.maracaStack;
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityCockroach entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (entitylivingbaseIn.hasMaracas()) {
         ItemStack stack = this.maracas();
         ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
         matrixStackIn.pushPose();
         if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0, 0.815, 0.125);
         }

         matrixStackIn.pushPose();
         this.translateToHand(0, matrixStackIn);
         matrixStackIn.translate(-0.25F, 0.0F, 0.0F);
         matrixStackIn.scale(1.4F, 1.4F, 1.4F);
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F));
         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(60.0F));
         AMRenderCompat.renderItemInHand(renderer, entitylivingbaseIn, stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.popPose();
         matrixStackIn.pushPose();
         this.translateToHand(1, matrixStackIn);
         matrixStackIn.translate(0.25F, 0.0F, 0.0F);
         matrixStackIn.scale(1.4F, 1.4F, 1.4F);
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-120.0F));
         AMRenderCompat.renderItemInHand(renderer, entitylivingbaseIn, stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.popPose();
         matrixStackIn.pushPose();
         this.translateToHand(2, matrixStackIn);
         matrixStackIn.translate(-0.35F, 0.0F, 0.0F);
         matrixStackIn.scale(1.4F, 1.4F, 1.4F);
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F));
         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(60.0F));
         AMRenderCompat.renderItemInHand(renderer, entitylivingbaseIn, stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.popPose();
         matrixStackIn.pushPose();
         this.translateToHand(3, matrixStackIn);
         matrixStackIn.translate(0.35F, 0.0F, 0.0F);
         matrixStackIn.scale(1.4F, 1.4F, 1.4F);
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-120.0F));
         AMRenderCompat.renderItemInHand(renderer, entitylivingbaseIn, stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.popPose();
         if (!entitylivingbaseIn.isHeadless()) {
            matrixStackIn.pushPose();
            this.translateToHand(4, matrixStackIn);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(60.0F * entitylivingbaseIn.danceProgress * 0.2F));
            matrixStackIn.translate(0.0F, 0.15F - entitylivingbaseIn.danceProgress * 0.008F, 0.02F);
            matrixStackIn.scale(0.8F, 0.8F, 0.8F);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(SOMBRERO_TEX));
            AMRenderCompat.renderToBuffer(
               this.sombrero,
               matrixStackIn,
               ivertexbuilder,
               packedLightIn,
               LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F),
               1.0F,
               1.0F,
               1.0F,
               1.0F
            );
            matrixStackIn.popPose();
         }

         matrixStackIn.popPose();
      }
   }

   protected void translateToHand(int hand, PoseStack matrixStack) {
      ((ModelCockroach)this.getParentModel()).root.translateAndRotate(matrixStack);
      ((ModelCockroach)this.getParentModel()).abdomen.translateAndRotate(matrixStack);
      if (hand == 0) {
         ((ModelCockroach)this.getParentModel()).right_leg_front.translateAndRotate(matrixStack);
      } else if (hand == 1) {
         ((ModelCockroach)this.getParentModel()).left_leg_front.translateAndRotate(matrixStack);
      } else if (hand == 2) {
         ((ModelCockroach)this.getParentModel()).right_leg_mid.translateAndRotate(matrixStack);
      } else if (hand == 3) {
         ((ModelCockroach)this.getParentModel()).left_leg_mid.translateAndRotate(matrixStack);
      } else {
         ((ModelCockroach)this.getParentModel()).neck.translateAndRotate(matrixStack);
         ((ModelCockroach)this.getParentModel()).head.translateAndRotate(matrixStack);
      }
   }
}
