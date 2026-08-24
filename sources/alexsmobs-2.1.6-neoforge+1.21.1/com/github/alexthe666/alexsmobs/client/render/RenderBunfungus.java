package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBunfungus;
import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
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

public class RenderBunfungus extends MobRenderer<EntityBunfungus, ModelBunfungus> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/bunfungus.png");
   private static final ResourceLocation TEXTURE_SLEEPING = AMCompat.rl("alexsmobs:textures/entity/bunfungus_sleeping.png");

   public RenderBunfungus(Context renderManagerIn) {
      super(renderManagerIn, new ModelBunfungus(), 0.6F);
      this.addLayer(new RenderBunfungus.LayerHeldItem(this));
   }

   protected void scale(EntityBunfungus rabbit, PoseStack matrixStackIn, float partialTickTime) {
      float f = rabbit.prevTransformTime + (rabbit.transformsIn() - rabbit.prevTransformTime) * partialTickTime;
      float f1 = (50.0F - f) / 50.0F;
      float f2 = f1 * 0.7F + 0.3F;
      matrixStackIn.scale(f2, f2, f2);
   }

   public ResourceLocation getTextureLocation(EntityBunfungus entity) {
      return entity.isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
   }

   static class LayerHeldItem extends RenderLayer<EntityBunfungus, ModelBunfungus> {
      public LayerHeldItem(RenderBunfungus render) {
         super(render);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityBunfungus entitylivingbaseIn,
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
         matrixStackIn.translate(0.3F, 0.45F, -0.15F);
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F));
         matrixStackIn.scale(1.15F, 1.15F, 1.15F);
         ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
         AMRenderCompat.renderItemInHand(renderer, entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.popPose();
         matrixStackIn.popPose();
      }

      protected void translateToHand(PoseStack matrixStack) {
         ((ModelBunfungus)this.getParentModel()).root.translateAndRotate(matrixStack);
         ((ModelBunfungus)this.getParentModel()).body.translateAndRotate(matrixStack);
         ((ModelBunfungus)this.getParentModel()).right_arm.translateAndRotate(matrixStack);
      }
   }
}
