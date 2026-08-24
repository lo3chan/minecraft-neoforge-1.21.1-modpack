package dev.tr7zw.waveycapes.support;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import net.minecraft.client.player.AbstractClientPlayer;

public interface ModSupport {
   default boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
      return this.shouldBeUsed((AbstractClientPlayer)capeRenderInfo.getEntity());
   }

   @Deprecated
   default boolean shouldBeUsed(AbstractClientPlayer player) {
      return false;
   }

   CapeRenderer getRenderer();

   boolean blockFeatureRenderer(Object var1);
}
