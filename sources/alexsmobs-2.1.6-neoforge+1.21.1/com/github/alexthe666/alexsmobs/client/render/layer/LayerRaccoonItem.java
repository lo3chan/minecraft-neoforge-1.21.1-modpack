package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.client.render.RenderRaccoon;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerRaccoonItem extends RenderLayer<EntityRaccoon, ModelRaccoon> {
   public LayerRaccoonItem(RenderRaccoon render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityRaccoon entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
      matrixStackIn.pushPose();
      boolean inHand = entitylivingbaseIn.begProgress > 0.0F || entitylivingbaseIn.standProgress > 0.0F || entitylivingbaseIn.washProgress > 0.0F;
      if (entitylivingbaseIn.isBaby()) {
         matrixStackIn.scale(0.5F, 0.5F, 0.5F);
         matrixStackIn.translate(0.0, 1.5, 0.0);
      }

      matrixStackIn.pushPose();
      this.translateToHand(inHand, matrixStackIn);
      if (inHand) {
         matrixStackIn.translate(0.2F, 0.4F, 0.0F);
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * entitylivingbaseIn.washProgress * 0.2F));
      } else {
         matrixStackIn.translate(0.0F, 0.1F, -0.35F);
      }

      matrixStackIn.mulPose(Axis.YP.rotationDegrees(-2.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F));
      ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
      AMRenderCompat.renderItemInHand(renderer, entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.popPose();
   }

   protected void translateToHand(boolean inHand, PoseStack matrixStack) {
      if (inHand) {
         ((ModelRaccoon)this.getParentModel()).root.translateAndRotate(matrixStack);
         ((ModelRaccoon)this.getParentModel()).body.translateAndRotate(matrixStack);
         ((ModelRaccoon)this.getParentModel()).arm_right.translateAndRotate(matrixStack);
      } else {
         ((ModelRaccoon)this.getParentModel()).root.translateAndRotate(matrixStack);
         ((ModelRaccoon)this.getParentModel()).body.translateAndRotate(matrixStack);
         ((ModelRaccoon)this.getParentModel()).head.translateAndRotate(matrixStack);
      }
   }
}
