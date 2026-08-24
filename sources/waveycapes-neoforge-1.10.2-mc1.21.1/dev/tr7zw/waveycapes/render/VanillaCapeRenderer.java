package dev.tr7zw.waveycapes.render;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class VanillaCapeRenderer implements CapeRenderer {
   @Override
   public CapeInfos getCapeInfo(PlayerWrapper capeRenderInfo) {
      ResourceLocation cape = capeRenderInfo.getCapeTexture();
      return cape != null ? new CapeInfos(this, RenderType.entityTranslucent(cape), false) : null;
   }

   @Override
   public boolean vanillaUvValues() {
      return true;
   }
}
