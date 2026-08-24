package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.model.ModelCachalotWhale;
import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.github.alexthe666.alexsmobs.client.render.RenderCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

public class LayerCachalotWhaleCapturedSquid extends RenderLayer<EntityCachalotWhale, ModelCachalotWhale> {
   public LayerCachalotWhaleCapturedSquid(RenderCachalotWhale render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityCachalotWhale whale,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (whale.hasCaughtSquid() && whale.isAlive()) {
         Entity squid = whale.getCaughtSquid();
         if (squid != null && squid.isAlive()) {
            boolean rightSquid = !whale.isHoldingSquidLeft();
            float riderRot = squid.yRotO + (squid.getYRot() - squid.yRotO) * partialTicks;
            EntityModel<?> modelBase = AMRenderCompat.rendererModel(squid);
            if (modelBase != null) {
               ClientProxy.currentUnrenderedEntities.remove(squid.getUUID());
               matrixStackIn.pushPose();
               this.translateToPouch(matrixStackIn);
               matrixStackIn.translate(rightSquid ? -1.2F : 1.2F, 0.0F, -3.4F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(riderRot + (rightSquid ? -90.0F : 90.0F)));
               this.renderEntity(squid, 0.0, 0.0, 0.0, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
               matrixStackIn.popPose();
               ClientProxy.currentUnrenderedEntities.add(squid.getUUID());
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
      ((ModelCachalotWhale)this.getParentModel()).root.translateAndRotate(matrixStack);
      ((ModelCachalotWhale)this.getParentModel()).body.translateAndRotate(matrixStack);
      ((ModelCachalotWhale)this.getParentModel()).head.translateAndRotate(matrixStack);
      ((ModelCachalotWhale)this.getParentModel()).jaw.translateAndRotate(matrixStack);
   }
}
