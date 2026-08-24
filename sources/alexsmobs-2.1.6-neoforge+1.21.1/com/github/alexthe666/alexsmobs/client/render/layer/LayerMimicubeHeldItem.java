package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelMimicube;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.client.render.RenderMimicube;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class LayerMimicubeHeldItem extends RenderLayer<EntityMimicube, ModelMimicube> {
   public LayerMimicubeHeldItem(RenderMimicube render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityMimicube entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ItemStack itemRight = entitylivingbaseIn.getMainHandItem();
      ItemStack itemLeft = entitylivingbaseIn.getOffhandItem();
      float rightSwap = Mth.lerp(partialTicks, entitylivingbaseIn.prevRightSwapProgress, entitylivingbaseIn.rightSwapProgress) * 0.2F;
      float leftSwap = Mth.lerp(partialTicks, entitylivingbaseIn.prevLeftSwapProgress, entitylivingbaseIn.leftSwapProgress) * 0.2F;
      float attackprogress = Mth.lerp(partialTicks, entitylivingbaseIn.prevAttackProgress, entitylivingbaseIn.attackProgress);
      double bob1 = Math.cos(ageInTicks * 0.1F) * 0.10000000149011612 + 0.10000000149011612;
      double bob2 = Math.sin(ageInTicks * 0.1F) * 0.10000000149011612 + 0.10000000149011612;
      if (!itemRight.isEmpty()) {
         matrixStackIn.pushPose();
         this.translateToHand(false, matrixStackIn);
         matrixStackIn.translate(-0.5, 0.10000000149011612 - bob1, -0.10000000149011612);
         matrixStackIn.scale(0.9F * (1.0F - rightSwap), 0.9F * (1.0F - rightSwap), 0.9F * (1.0F - rightSwap));
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
         if (itemRight.getItem() instanceof ShieldItem) {
            matrixStackIn.translate(-0.1F, 0.0F, -0.4F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
         }

         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-10.0F));
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(360.0F * rightSwap));
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(-40.0F * attackprogress));
         AMRenderCompat.renderItemStatic(
            itemRight,
            ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
            rightSwap > 0.0F ? (int)(-100.0F * rightSwap) : packedLightIn,
            LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F),
            matrixStackIn,
            bufferIn,
            entitylivingbaseIn.level(),
            0
         );
         matrixStackIn.popPose();
      }

      if (!itemLeft.isEmpty()) {
         matrixStackIn.pushPose();
         this.translateToHand(false, matrixStackIn);
         matrixStackIn.translate(0.44999998807907104, 0.10000000149011612 - bob2, -0.10000000149011612);
         matrixStackIn.scale(0.9F * (1.0F - leftSwap), 0.9F * (1.0F - leftSwap), 0.9F * (1.0F - leftSwap));
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
         int clampedLight = (int)Math.floor(packedLightIn * (1.0F - leftSwap));
         if (itemLeft.getItem() instanceof ShieldItem) {
            matrixStackIn.translate(-0.2F, 0.0F, -0.4F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
         }

         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(10.0F));
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(360.0F * leftSwap));
         AMRenderCompat.renderItemStatic(
            itemLeft,
            ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
            leftSwap > 0.0F ? (int)(-100.0F * leftSwap) : packedLightIn,
            LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F),
            matrixStackIn,
            bufferIn,
            entitylivingbaseIn.level(),
            0
         );
         matrixStackIn.popPose();
      }
   }

   protected void translateToHand(boolean left, PoseStack matrixStack) {
      ((ModelMimicube)this.getParentModel()).root.translateAndRotate(matrixStack);
      ((ModelMimicube)this.getParentModel()).innerbody.translateAndRotate(matrixStack);
   }
}
