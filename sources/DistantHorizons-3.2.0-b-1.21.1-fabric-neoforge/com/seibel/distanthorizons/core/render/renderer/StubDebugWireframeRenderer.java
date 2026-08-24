package com.seibel.distanthorizons.core.render.renderer;

import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.render.RenderParams;

public class StubDebugWireframeRenderer extends AbstractDebugWireframeRenderer {
   @Override
   public void render(RenderParams renderParams) {
   }

   @Override
   public void renderBox(AbstractDebugWireframeRenderer.Box box) {
   }

   @Override
   public void makeParticle(AbstractDebugWireframeRenderer.BoxParticle particle) {
   }

   @Override
   public void register(IDebugRenderable renderable, ConfigEntry<Boolean> config) {
   }

   @Override
   public void addRenderer(IDebugRenderable renderable, ConfigEntry<Boolean> config) {
   }

   @Override
   public void unregister(IDebugRenderable renderable, ConfigEntry<Boolean> config) {
   }

   @Override
   public void removeRenderer(IDebugRenderable renderable, ConfigEntry<Boolean> config) {
   }

   @Override
   public void clearRenderables() {
   }
}
