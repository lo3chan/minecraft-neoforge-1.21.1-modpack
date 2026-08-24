package dev.tr7zw.waveycapes.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.support.ModSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class CapeNodeCollector {
   private final CustomCapeRenderer customCapeRenderer = new CustomCapeRenderer();
   private final VanillaCapeRenderer vanillaCape = new VanillaCapeRenderer();

   public void submitCape(MultiBufferSource multiBufferSource, PlayerWrapper playerWrapper, PoseStack stack, int packedLight, float delta) {
      CapeRenderer renderer = this.getCapeRenderer(playerWrapper);
      if (renderer != null) {
         CapeInfos capeInfo = renderer.getCapeInfo(playerWrapper);
         if (capeInfo != null) {
            VertexConsumer vertexConsumer;
            if (capeInfo.isGlint()) {
               vertexConsumer = ItemRenderer.getFoilBuffer(multiBufferSource, capeInfo.renderType(), false, true);
            } else {
               vertexConsumer = multiBufferSource.getBuffer(capeInfo.renderType());
            }

            this.customCapeRenderer.render(playerWrapper, renderer, vertexConsumer, stack, packedLight, delta);
         }
      }
   }

   private CapeRenderer getCapeRenderer(PlayerWrapper capeRenderInfo) {
      for (ModSupport support : SupportManager.getSupportedMods()) {
         if (support.shouldBeUsed(capeRenderInfo)) {
            return support.getRenderer();
         }
      }

      return capeRenderInfo.getCapeTexture() != null && capeRenderInfo.isCapeVisible() ? this.vanillaCape : null;
   }
}
