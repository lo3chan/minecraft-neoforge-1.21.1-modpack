package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.model.ModelAnteater;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.client.render.RenderAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

public class LayerAnteaterBaby extends RenderLayer<EntityAnteater, ModelAnteater> {
   public LayerAnteaterBaby(RenderAnteater render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityAnteater roo,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (roo.isVehicle() && !roo.isBaby()) {
         for (Entity passenger : roo.getPassengers()) {
            float riderRot = passenger.yRotO + (passenger.getYRot() - passenger.yRotO) * partialTicks;
            EntityModel<?> modelBase = AMRenderCompat.rendererModel(passenger);
            if (modelBase != null) {
               ClientProxy.currentUnrenderedEntities.remove(passenger.getUUID());
               matrixStackIn.pushPose();
               this.translateToPouch(matrixStackIn);
               matrixStackIn.translate(0.0F, -0.12F, 0.1F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(riderRot + 180.0F));
               this.renderEntity(passenger, 0.0, 0.0, 0.0, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
               matrixStackIn.popPose();
               ClientProxy.currentUnrenderedEntities.add(passenger.getUUID());
            }
         }
      }
   }

   public <E extends Entity> void renderEntity(
      E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight
   ) {
      AMRenderCompat.renderEntity(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
   }

   protected void translateToPouch(PoseStack matrixStack) {
      ((ModelAnteater)this.getParentModel()).root.translateAndRotate(matrixStack);
      ((ModelAnteater)this.getParentModel()).body.translateAndRotate(matrixStack);
   }
}
