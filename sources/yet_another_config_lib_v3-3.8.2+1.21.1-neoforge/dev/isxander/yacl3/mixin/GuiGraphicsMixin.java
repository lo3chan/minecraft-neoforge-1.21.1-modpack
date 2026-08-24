package dev.isxander.yacl3.mixin;

import dev.isxander.yacl3.gui.render.GuiRenderStateSink;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({GuiGraphics.class})
public class GuiGraphicsMixin implements GuiRenderStateSink {
   @Shadow
   @Final
   private BufferSource bufferSource;

   @Override
   public MultiBufferSource yacl$bufferSource() {
      return this.bufferSource;
   }
}
