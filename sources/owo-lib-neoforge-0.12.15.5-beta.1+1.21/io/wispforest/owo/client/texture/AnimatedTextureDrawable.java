package io.wispforest.owo.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;

public class AnimatedTextureDrawable implements Renderable {
   private final SpriteSheetMetadata metadata;
   private final ResourceLocation texture;
   private final int validFrames;
   private final int delay;
   private final boolean loop;
   private final int rows;
   private long startTime = -1L;
   private final int width;
   private final int height;
   private int x;
   private int y;

   public AnimatedTextureDrawable(int x, int y, ResourceLocation texture, SpriteSheetMetadata metadata, int delay, boolean loop) {
      this(x, y, metadata.width(), metadata.height(), texture, metadata, delay, loop);
   }

   public AnimatedTextureDrawable(int x, int y, int width, int height, ResourceLocation texture, SpriteSheetMetadata metadata, int delay, boolean loop) {
      this.x = x;
      this.y = y;
      this.texture = texture;
      this.delay = delay;
      this.metadata = metadata;
      this.width = width;
      this.height = height;
      this.loop = loop;
      int columns = metadata.width() / metadata.frameWidth();
      this.rows = metadata.height() / metadata.frameHeight();
      this.validFrames = columns * this.rows;
   }

   public void render(int x, int y, GuiGraphics context, int mouseX, int mouseY, float delta) {
      this.x = x;
      this.y = y;
      this.render(context, mouseX, mouseY, delta);
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      if (this.startTime == -1L) {
         this.startTime = Util.getMillis();
      }

      long currentTime = Util.getMillis();
      long frame = Math.min((long)(this.validFrames - 1), (currentTime - this.startTime) / this.delay);
      if (this.loop && frame == this.validFrames - 1) {
         this.startTime = Util.getMillis();
         frame = 0L;
      }

      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();
      context.blit(
         this.texture,
         this.x,
         this.y,
         (float)(frame / this.rows * this.metadata.frameWidth()),
         (float)(frame % this.rows * this.metadata.frameHeight()),
         this.width,
         this.height,
         this.metadata.width(),
         this.metadata.height()
      );
      RenderSystem.disableDepthTest();
      RenderSystem.disableBlend();
   }
}
