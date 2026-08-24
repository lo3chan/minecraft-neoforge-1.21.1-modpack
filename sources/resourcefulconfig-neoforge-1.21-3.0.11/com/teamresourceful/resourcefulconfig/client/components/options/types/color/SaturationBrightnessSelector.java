package com.teamresourceful.resourcefulconfig.client.components.options.types.color;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class SaturationBrightnessSelector extends BaseWidget {
   private final HsbState state;

   public SaturationBrightnessSelector(int width, int height, HsbState state) {
      super(width, height);
      this.state = state;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      HsbColor color = this.state.get();
      int posX = Math.round(color.saturation() * this.getWidth());
      int posY = this.getHeight() - Math.round(color.brightness() * this.getHeight());
      int tileWidth = Math.round(this.getWidth() / 10.0F);
      int tileHeight = Math.round(this.getHeight() / 10.0F);

      for (int dy = 0; dy < 10; dy++) {
         float minB = dy / 10.0F;
         float maxB = (dy + 1) / 10.0F;

         for (int dx = 0; dx < 10; dx++) {
            float minS = dx / 10.0F;
            float maxS = (dx + 1) / 10.0F;
            this.drawGradient(
               graphics,
               this.getX() + dx * tileWidth,
               this.getY() + (10 - dy - 1) * tileHeight,
               tileWidth,
               tileHeight,
               HsbColor.of(color.hue(), minS, maxB, 255).toRgba(),
               HsbColor.of(color.hue(), maxS, maxB, 255).toRgba(),
               HsbColor.of(color.hue(), minS, minB, 255).toRgba(),
               HsbColor.of(color.hue(), maxS, minB, 255).toRgba()
            );
         }
      }

      graphics.renderOutline(this.getX() + posX - 1, this.getY() + posY - 1, 3, 3, -16777216);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return false;
      } else if (!this.isMouseOver(mouseX, mouseY)) {
         return false;
      } else {
         int x = (int)mouseX - this.getX();
         int y = (int)mouseY - this.getY();
         if (x >= 0 && x < this.getWidth() && y >= 0 && y < this.getHeight()) {
            this.state
               .set(
                  HsbColor.of(
                     this.state.get().hue(), Mth.clamp((float)x / this.getWidth(), 0.0F, 1.0F), 1.0F - Mth.clamp((float)y / this.getHeight(), 0.0F, 1.0F), 255
                  )
               );
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean mouseDragged(double d, double e, int i, double f, double g) {
      return this.mouseClicked(d, e, i);
   }

   private void drawGradient(GuiGraphics graphics, int x, int y, int width, int height, int topLeft, int topRight, int bottomLeft, int bottomRight) {
      Matrix4f matrix4f = graphics.pose().last().pose();
      VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.gui());
      buffer.addVertex(matrix4f, x, y + height, 0.0F).setColor(bottomLeft);
      buffer.addVertex(matrix4f, x + width, y + height, 0.0F).setColor(bottomRight);
      buffer.addVertex(matrix4f, x + width, y, 0.0F).setColor(topRight);
      buffer.addVertex(matrix4f, x, y, 0.0F).setColor(topLeft);
      graphics.flush();
   }
}
