package net.mehvahdjukaar.moonlight.api.client.texture_renderer;

import java.util.function.Consumer;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Deprecated(
   forRemoval = true
)
public class TickableFrameBufferBackedDynamicTexture extends FrameBufferBackedDynamicTexture implements Tickable {
   private boolean shouldTick = true;

   public TickableFrameBufferBackedDynamicTexture(
      ResourceLocation resourceLocation, int width, int height, @NotNull Consumer<FrameBufferBackedDynamicTexture> textureDrawingFunction
   ) {
      super(resourceLocation, width, height, textureDrawingFunction);
   }

   public TickableFrameBufferBackedDynamicTexture(
      ResourceLocation resourceLocation, int size, @NotNull Consumer<FrameBufferBackedDynamicTexture> textureDrawingFunction
   ) {
      super(resourceLocation, size, textureDrawingFunction);
   }

   public void tick() {
      if (this.shouldTick) {
         this.shouldTick = false;
         this.redraw();
      }
   }

   @Override
   public void markForUpdate() {
      this.shouldTick = true;
   }

   @Override
   public void unMarkForUpdate() {
      this.shouldTick = false;
   }
}
