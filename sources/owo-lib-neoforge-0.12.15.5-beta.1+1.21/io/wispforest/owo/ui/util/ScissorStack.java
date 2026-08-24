package io.wispforest.owo.ui.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.PositionedRectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public final class ScissorStack {
   private static final PoseStack EMPTY_STACK = new PoseStack();
   private static final Deque<PositionedRectangle> STACK = new ArrayDeque<>();

   private ScissorStack() {
   }

   public static void pushDirect(int x, int y, int width, int height) {
      Window window = Minecraft.getInstance().getWindow();
      double scale = window.getGuiScale();
      push((int)(x / scale), (int)(window.getGuiScaledHeight() - y / scale - height / scale), (int)(width / scale), (int)(height / scale), null);
   }

   public static void push(int x, int y, int width, int height, @Nullable PoseStack matrices) {
      PositionedRectangle newFrame = withGlTransform(x, y, width, height, matrices);
      if (STACK.isEmpty()) {
         STACK.push(newFrame);
      } else {
         PositionedRectangle top = STACK.peek();
         STACK.push(top.intersection(newFrame));
      }

      applyState();
   }

   public static void pop() {
      if (STACK.isEmpty()) {
         throw new IllegalStateException("Cannot pop frame from empty scissor stack");
      } else {
         STACK.pop();
         applyState();
      }
   }

   private static void applyState() {
      if (STACK.isEmpty()) {
         Window window = Minecraft.getInstance().getWindow();
         GL11.glScissor(0, 0, window.getWidth(), window.getHeight());
      } else if (GL11.glIsEnabled(3089)) {
         PositionedRectangle newFrame = STACK.peek();
         Window window = Minecraft.getInstance().getWindow();
         double scale = window.getGuiScale();
         GL11.glScissor(
            Math.max(0, (int)(newFrame.x() * scale)),
            Math.max((int)(window.getHeight() - newFrame.y() * scale - newFrame.height() * scale), 0),
            Math.min(Mth.clamp((int)(newFrame.width() * scale), 0, window.getWidth()), window.getWidth()),
            Math.min(Mth.clamp((int)(newFrame.height() * scale), 0, window.getHeight()), window.getHeight())
         );
      }
   }

   public static void drawUnclipped(Runnable action) {
      boolean scissorEnabled = GL11.glIsEnabled(3089);
      if (scissorEnabled) {
         GlStateManager._disableScissorTest();
      }

      action.run();
      if (scissorEnabled) {
         GlStateManager._enableScissorTest();
      }
   }

   public static void popFramesAndDraw(int maxPopFrames, Runnable action) {
      ArrayList<PositionedRectangle> previousFrames = new ArrayList<>();

      while (maxPopFrames > 1 && STACK.size() > 1) {
         previousFrames.add(0, STACK.pop());
         maxPopFrames--;
      }

      applyState();
      action.run();
      previousFrames.forEach(STACK::push);
      applyState();
   }

   public static boolean isVisible(int x, int y, @Nullable PoseStack matrices) {
      PositionedRectangle top = STACK.peek();
      return top == null ? true : top.intersects(withGlTransform(x, y, 0, 0, matrices));
   }

   public static boolean isVisible(Component component, @Nullable PoseStack matrices) {
      PositionedRectangle top = STACK.peek();
      if (top == null) {
         return true;
      } else {
         Insets margins = component.margins().get();
         return top.intersects(
            withGlTransform(
               component.x() - margins.left(),
               component.y() - margins.top(),
               component.width() + margins.right(),
               component.height() + margins.bottom(),
               matrices
            )
         );
      }
   }

   private static PositionedRectangle withGlTransform(int x, int y, int width, int height, @Nullable PoseStack matrices) {
      if (matrices == null) {
         matrices = EMPTY_STACK;
      }

      matrices.pushPose();
      matrices.mulPose(RenderSystem.getModelViewMatrix());
      Vector4f root = new Vector4f(x, y, 0.0F, 1.0F);
      Vector4f end = new Vector4f(x + width, y + height, 0.0F, 1.0F);
      root.mul(matrices.last().pose());
      end.mul(matrices.last().pose());
      x = (int)root.x;
      y = (int)root.y;
      width = (int)Math.ceil(end.x - root.x);
      height = (int)Math.ceil(end.y - root.y);
      matrices.popPose();
      return PositionedRectangle.of(x, y, width, height);
   }
}
