package dev.tr7zw.waveycapes.renderlayers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class CustomCapeRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public CustomCapeRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int packedLight,
      AbstractClientPlayer renderState,
      float f,
      float g,
      float delta,
      float j,
      float k,
      float l
   ) {
      PlayerWrapper capeRenderInfo = new PlayerWrapper(renderState);
      if (!capeRenderInfo.isPlayerInvisible()) {
         if (!capeRenderInfo.hasElytraEquipped()) {
            if (capeRenderInfo.isCapeVisible()) {
               poseStack.pushPose();
               ((PlayerModel)this.getParentModel()).body.translateAndRotate(poseStack);
               if (capeRenderInfo.hasChestplateEquipped()) {
                  poseStack.translate(0.0F, -0.053125F, 0.06875F);
               }

               WaveyCapesBase.INSTANCE.getCapeNodeCollector().submitCape(multiBufferSource, capeRenderInfo, poseStack, packedLight, delta);
               poseStack.popPose();
            }
         }
      }
   }
}
