package dev.isxander.yacl3.gui.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

public interface GuiRenderStateSink {
   MultiBufferSource yacl$bufferSource();

   static MultiBufferSource bufferSource(GuiGraphics graphics) {
      return ((GuiRenderStateSink)graphics).yacl$bufferSource();
   }
}
