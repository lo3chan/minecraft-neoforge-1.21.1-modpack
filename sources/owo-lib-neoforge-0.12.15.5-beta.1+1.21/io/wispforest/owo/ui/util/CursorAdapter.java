package io.wispforest.owo.ui.util;

import com.mojang.blaze3d.platform.Window;
import io.wispforest.owo.ui.core.CursorStyle;
import java.util.EnumMap;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class CursorAdapter {
   protected static final CursorStyle[] ACTIVE_STYLES = new CursorStyle[]{
      CursorStyle.POINTER,
      CursorStyle.TEXT,
      CursorStyle.HAND,
      CursorStyle.CROSSHAIR,
      CursorStyle.MOVE,
      CursorStyle.HORIZONTAL_RESIZE,
      CursorStyle.VERTICAL_RESIZE,
      CursorStyle.NWSE_RESIZE,
      CursorStyle.NESW_RESIZE,
      CursorStyle.NOT_ALLOWED
   };
   protected final EnumMap<CursorStyle, Long> cursors = new EnumMap<>(CursorStyle.class);
   protected final long windowHandle;
   protected CursorStyle lastCursorStyle = CursorStyle.POINTER;
   protected boolean disposed = false;

   protected CursorAdapter(long windowHandle) {
      this.windowHandle = windowHandle;

      for (CursorStyle style : ACTIVE_STYLES) {
         long pointer = GLFW.glfwCreateStandardCursor(style.glfw);
         if (pointer != 0L) {
            this.cursors.put(style, pointer);
         }
      }
   }

   public static CursorAdapter ofClientWindow() {
      return new CursorAdapter(Minecraft.getInstance().getWindow().getWindow());
   }

   public static CursorAdapter ofWindow(Window window) {
      return new CursorAdapter(window.getWindow());
   }

   public static CursorAdapter ofWindow(long windowHandle) {
      return new CursorAdapter(windowHandle);
   }

   public void applyStyle(CursorStyle style) {
      if (!this.disposed && this.lastCursorStyle != style) {
         if (style == CursorStyle.NONE) {
            GLFW.glfwSetCursor(this.windowHandle, 0L);
         } else {
            GLFW.glfwSetCursor(this.windowHandle, this.cursors.getOrDefault(style, 0L));
         }

         this.lastCursorStyle = style;
      }
   }

   public void dispose() {
      if (!this.disposed) {
         this.cursors.values().forEach(GLFW::glfwDestroyCursor);
         this.disposed = true;
      }
   }
}
