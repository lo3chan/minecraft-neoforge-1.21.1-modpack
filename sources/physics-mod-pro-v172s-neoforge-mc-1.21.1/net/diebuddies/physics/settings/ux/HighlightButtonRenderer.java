package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor.ARGB32;
import org.joml.Matrix4f;

public class HighlightButtonRenderer extends Animator {
   private float time;

   @Override
   public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      this.time += delta;
      RenderSystem.enableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      float x = animatable.getAnimX();
      float y = animatable.getAnimY();
      float width = animatable.getAnimWidth() - 1.0F;
      float height = animatable.getAnimHeight() - 1.0F;
      if (animatable instanceof AbstractWidget widget) {
         x = widget.getX();
         y = widget.getY();
         width = widget.getWidth();
         height = widget.getHeight();
      }

      float depth = 100.0F;
      int offset = (int)(Math.abs(Math.sin(this.time * 5.0)) * 3.0) + 1;
      int lineLength = 3;
      Matrix4f pose = guiGraphics.pose().last().pose();
      int color = BaseColors.HIGHLIGHT_COLOR;
      int backgroundColor = ARGB32.color(255, 60, 90, 60);
      drawLine(bufferBuilder, pose, x - offset + 1.0F, y - offset + 1.0F, x + lineLength - offset + 1.0F, y - offset + 1.0F, depth, backgroundColor);
      drawLine(bufferBuilder, pose, x - offset + 1.0F, y - offset + 1.0F, x - offset + 1.0F, y - offset + lineLength + 1.0F, depth, backgroundColor);
      drawLine(bufferBuilder, pose, x - offset, y - offset, x + lineLength - offset, y - offset, depth, color);
      drawLine(bufferBuilder, pose, x - offset, y - offset, x - offset, y - offset + lineLength, depth, color);
      drawLine(
         bufferBuilder, pose, x + offset + width - lineLength + 1.0F, y - offset + 1.0F, x + offset + width + 1.0F, y - offset + 1.0F, depth, backgroundColor
      );
      drawLine(
         bufferBuilder, pose, x + offset + width + 1.0F, y - offset + 1.0F, x + offset + width + 1.0F, y - offset + lineLength + 1.0F, depth, backgroundColor
      );
      drawLine(bufferBuilder, pose, x + offset + width - lineLength, y - offset, x + offset + width, y - offset, depth, color);
      drawLine(bufferBuilder, pose, x + offset + width, y - offset, x + offset + width, y - offset + lineLength, depth, color);
      drawLine(
         bufferBuilder, pose, x - offset + 1.0F, y + offset + height + 1.0F, x + lineLength - offset + 1.0F, y + offset + height + 1.0F, depth, backgroundColor
      );
      drawLine(
         bufferBuilder, pose, x - offset + 1.0F, y + offset - lineLength + height + 1.0F, x - offset + 1.0F, y + offset + height + 1.0F, depth, backgroundColor
      );
      drawLine(bufferBuilder, pose, x - offset, y + offset + height, x + lineLength - offset, y + offset + height, depth, color);
      drawLine(bufferBuilder, pose, x - offset, y + offset - lineLength + height, x - offset, y + offset + height, depth, color);
      drawLine(
         bufferBuilder,
         pose,
         x + offset + width - lineLength + 1.0F,
         y + offset + height + 1.0F,
         x + offset + width + 1.0F,
         y + offset + height + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(
         bufferBuilder,
         pose,
         x + offset + width + 1.0F,
         y + offset - lineLength + height + 1.0F,
         x + offset + width + 1.0F,
         y + offset + height + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(bufferBuilder, pose, x + offset + width - lineLength, y + offset + height, x + offset + width, y + offset + height, depth, color);
      drawLine(bufferBuilder, pose, x + offset + width, y + offset - lineLength + height, x + offset + width, y + offset + height, depth, color);
      BufferUploader.drawWithShader(bufferBuilder.build());
      RenderSystem.disableBlend();
      RenderSystem.enableDepthTest();
      return super.render(animatable, guiGraphics, mouseX, mouseY, renderPercent, delta);
   }
}
