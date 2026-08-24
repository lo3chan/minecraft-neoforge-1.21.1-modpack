package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCosmaw;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderCosmaw extends MobRenderer<EntityCosmaw, ModelCosmaw> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/cosmaw.png");
   private static final ResourceLocation TEXTURE_GLOW = AMCompat.rl("alexsmobs:textures/entity/cosmaw_glow.png");

   public RenderCosmaw(Context renderManagerIn) {
      super(renderManagerIn, new ModelCosmaw(), 0.9F);
      this.addLayer(new RenderCosmaw.LayerHeldItem());
      this.addLayer(new LayerBasicGlow(this, TEXTURE_GLOW));
   }

   protected void scale(EntityCosmaw entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.translate(0.0F, -0.5F, 0.0F);
   }

   public ResourceLocation getTextureLocation(EntityCosmaw entity) {
      return TEXTURE;
   }

   class LayerHeldItem extends RenderLayer<EntityCosmaw, ModelCosmaw> {
      public LayerHeldItem() {
         super(RenderCosmaw.this);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityCosmaw entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         ItemStack itemstack = entitylivingbaseIn.getMainHandItem();
         matrixStackIn.pushPose();
         this.translateToHand(matrixStackIn);
         matrixStackIn.translate(-0.0, 0.10000000149011612, -1.350000023841858);
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(-45.0F));
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180.0F));
         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(135.0F));
         matrixStackIn.scale(2.0F, 2.0F, 2.0F);
         ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
         AMRenderCompat.renderItemInHand(renderer, entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.popPose();
      }

      protected void translateToHand(PoseStack matrixStack) {
         ((ModelCosmaw)this.getParentModel()).root.translateAndRotate(matrixStack);
         ((ModelCosmaw)this.getParentModel()).body.translateAndRotate(matrixStack);
         ((ModelCosmaw)this.getParentModel()).mouthArm1.translateAndRotate(matrixStack);
         ((ModelCosmaw)this.getParentModel()).mouthArm2.translateAndRotate(matrixStack);
      }
   }
}
