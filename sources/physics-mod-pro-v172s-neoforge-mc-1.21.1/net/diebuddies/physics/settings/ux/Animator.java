package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FastColor.ARGB32;
import org.joml.Matrix4f;

public abstract class Animator {
   public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      return false;
   }

   public boolean renderToolTip(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent) {
      return false;
   }

   public void tick(Animatable animatable) {
   }

   public void init(Animatable animatable) {
   }

   public static final void drawRect(BufferBuilder bufferBuilder, Matrix4f pose, float x, float y, float width, float height, float depth, int color) {
      bufferBuilder.addVertex(pose, x, y + height, depth).setColor(color);
      bufferBuilder.addVertex(pose, x + width, y + height, depth).setColor(color);
      bufferBuilder.addVertex(pose, x + width, y, depth).setColor(color);
      bufferBuilder.addVertex(pose, x, y, depth).setColor(color);
   }

   public static final void drawLine(BufferBuilder bufferBuilder, Matrix4f pose, float x1, float y1, float x2, float y2, float depth, int color) {
      bufferBuilder.addVertex(pose, x1, y2 + 1.0F, depth).setColor(color);
      bufferBuilder.addVertex(pose, x2 + 1.0F, y2 + 1.0F, depth).setColor(color);
      bufferBuilder.addVertex(pose, x2 + 1.0F, y1, depth).setColor(color);
      bufferBuilder.addVertex(pose, x1, y1, depth).setColor(color);
   }

   public static final void drawRect(
      BufferBuilder bufferBuilder, Matrix4f pose, float x, float y, float width, float height, float depth, float umin, float umax, float vmin, float vmax
   ) {
      bufferBuilder.addVertex(pose, x, y + height, depth).setUv(umin, vmax);
      bufferBuilder.addVertex(pose, x + width, y + height, depth).setUv(umax, vmax);
      bufferBuilder.addVertex(pose, x + width, y, depth).setUv(umax, vmin);
      bufferBuilder.addVertex(pose, x, y, depth).setUv(umin, vmin);
   }

   public static final void drawRect(
      BufferBuilder bufferBuilder,
      Matrix4f pose,
      float x,
      float y,
      float width,
      float height,
      float depth,
      float umin,
      float umax,
      float vmin,
      float vmax,
      int color
   ) {
      bufferBuilder.addVertex(pose, x, y + height, depth).setUv(umin, vmax).setColor(color);
      bufferBuilder.addVertex(pose, x + width, y + height, depth).setUv(umax, vmax).setColor(color);
      bufferBuilder.addVertex(pose, x + width, y, depth).setUv(umax, vmin).setColor(color);
      bufferBuilder.addVertex(pose, x, y, depth).setUv(umin, vmin).setColor(color);
   }

   public static final void drawText(GuiGraphics guiGraphics, Font font, FormattedCharSequence formattedCharSequence, float x, float y) {
      Matrix4f transformation = guiGraphics.pose().last().pose();
      font.drawInBatch(
         formattedCharSequence,
         x + 1.0F,
         y + 1.0F,
         ARGB32.color(255, 0, 0, 0),
         false,
         transformation,
         guiGraphics.bufferSource(),
         DisplayMode.NORMAL,
         0,
         15728880
      );
      guiGraphics.flush();
      font.drawInBatch(
         formattedCharSequence, x, y, ARGB32.color(255, 255, 255, 255), false, transformation, guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
      );
      guiGraphics.flush();
   }
}
