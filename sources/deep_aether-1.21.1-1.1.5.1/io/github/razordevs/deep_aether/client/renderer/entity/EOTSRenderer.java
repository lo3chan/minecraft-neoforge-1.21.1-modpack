package io.github.razordevs.deep_aether.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.razordevs.deep_aether.client.model.EOTSModel;
import io.github.razordevs.deep_aether.client.renderer.DAModelLayers;
import io.github.razordevs.deep_aether.entity.living.boss.eots.EOTSController;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class EOTSRenderer extends MobRenderer<EOTSController, EOTSModel> {
   public EOTSRenderer(Context renderManager) {
      super(renderManager, new EOTSModel(renderManager.bakeLayer(DAModelLayers.EOTS_CONTROLLER)), 0.0F);
   }

   public void render(EOTSController pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
      pPoseStack.scale(1.3F, 1.3F, 1.3F);
      super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
   }

   public ResourceLocation getTextureLocation(EOTSController instance) {
      return ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/eots/eots_controller.png");
   }
}
