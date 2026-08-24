package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderSeagull extends MobRenderer<EntitySeagull, ModelSeagull> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/seagull.png");
   private static final ResourceLocation TEXTURE_WINGULL = AMCompat.rl("alexsmobs:textures/entity/seagull_wingull.png");

   public RenderSeagull(Context renderManagerIn) {
      super(renderManagerIn, new ModelSeagull(), 0.2F);
      this.addLayer(new RenderSeagull.LayerHeldItem(this));
   }

   protected void scale(EntitySeagull entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   public ResourceLocation getTextureLocation(EntitySeagull entity) {
      return entity.isWingull() ? TEXTURE_WINGULL : TEXTURE;
   }

   static class LayerHeldItem extends RenderLayer<EntitySeagull, ModelSeagull> {
      public LayerHeldItem(RenderSeagull render) {
         super(render);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntitySeagull entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
         matrixStackIn.pushPose();
         if (entitylivingbaseIn.isBaby()) {
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0, 1.5, 0.0);
         }

         matrixStackIn.pushPose();
         this.translateToHand(matrixStackIn);
         matrixStackIn.translate(0.0F, -0.24F, -0.25F);
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(-2.5F));
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F));
         ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
         AMRenderCompat.renderItemInHand(renderer, entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.popPose();
         matrixStackIn.popPose();
      }

      protected void translateToHand(PoseStack matrixStack) {
         ((ModelSeagull)this.getParentModel()).root.translateAndRotate(matrixStack);
         ((ModelSeagull)this.getParentModel()).body.translateAndRotate(matrixStack);
         ((ModelSeagull)this.getParentModel()).head.translateAndRotate(matrixStack);
      }
   }
}
