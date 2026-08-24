package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelElephant;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.client.render.RenderElephant;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerElephantItem extends RenderLayer<EntityElephant, ModelElephant> {
   public LayerElephantItem(RenderElephant render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityElephant entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ItemStack itemstack = entitylivingbaseIn.getMainHandItem();
      matrixStackIn.pushPose();
      if (entitylivingbaseIn.isBaby()) {
         matrixStackIn.scale(0.35F, 0.35F, 0.35F);
         matrixStackIn.translate(0.0, 2.8, 0.0);
      }

      matrixStackIn.pushPose();
      this.translateToHand(matrixStackIn);
      if (entitylivingbaseIn.isBaby()) {
         matrixStackIn.translate(0.0, 0.20000000298023224, -0.22);
      }

      matrixStackIn.translate(-0.0, 1.0, 0.15000000596046448);
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
      matrixStackIn.scale(1.3F, 1.3F, 1.3F);
      if (Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemstack).isGui3d()) {
         matrixStackIn.translate(-0.05F, -0.1F, -0.15F);
         matrixStackIn.scale(2.0F, 2.0F, 2.0F);
      }

      ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
      AMRenderCompat.renderItemInHand(renderer, entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.popPose();
   }

   protected void translateToHand(PoseStack matrixStack) {
      ((ModelElephant)this.getParentModel()).root.translateAndRotate(matrixStack);
      ((ModelElephant)this.getParentModel()).body.translateAndRotate(matrixStack);
      ((ModelElephant)this.getParentModel()).head.translateAndRotate(matrixStack);
      ((ModelElephant)this.getParentModel()).trunk1.translateAndRotate(matrixStack);
      ((ModelElephant)this.getParentModel()).trunk2.translateAndRotate(matrixStack);
   }
}
