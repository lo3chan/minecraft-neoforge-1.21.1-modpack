package com.teamresourceful.resourcefulconfig.client.components.base;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ContainerWidget extends AbstractWidget implements ContainerEventHandler {
   protected final List<Renderable> renderables = new ArrayList<>();
   protected final List<GuiEventListener> children = new ArrayList<>();
   @Nullable
   private GuiEventListener focused;
   private boolean isDragging;

   public ContainerWidget(int x, int y, int width, int height) {
      super(x, y, width, height, CommonComponents.EMPTY);
   }

   @NotNull
   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   protected <T extends GuiEventListener & Renderable> T addRenderableWidget(T widget) {
      this.renderables.add(widget);
      this.children.add(widget);
      return widget;
   }

   protected void clear() {
      this.renderables.clear();
      this.children.clear();
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      for (Renderable renderable : this.renderables) {
         renderable.render(graphics, mouseX, mouseY, partialTicks);
      }
   }

   @NotNull
   public NarrationPriority narrationPriority() {
      return NarrationPriority.NONE;
   }

   public void setFocused(boolean focused) {
      super.setFocused(focused);
      if (this.focused != null) {
         this.focused.setFocused(focused);
      }
   }

   public final boolean isDragging() {
      return this.isDragging;
   }

   public final void setDragging(boolean bl) {
      this.isDragging = bl;
   }

   @Nullable
   public GuiEventListener getFocused() {
      return this.focused;
   }

   public void setFocused(@Nullable GuiEventListener guiEventListener) {
      if (this.focused != null && this.focused != guiEventListener) {
         this.focused.setFocused(false);
      }

      if (guiEventListener != null) {
         guiEventListener.setFocused(true);
      }

      this.focused = guiEventListener;
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }

   public void setX(int i) {
      int oldX = this.getX();
      super.setX(i);
      if (oldX != i) {
         this.positionUpdated();
      }
   }

   public void setY(int i) {
      int oldY = this.getY();
      super.setY(i);
      if (oldY != i) {
         this.positionUpdated();
      }
   }

   protected void positionUpdated() {
   }

   public final void onClick(double d, double e) {
   }

   public final void onRelease(double d, double e) {
   }

   protected final void onDrag(double d, double e, double f, double g) {
   }

   public boolean mouseClicked(double d, double e, int i) {
      return super.mouseClicked(d, e, i);
   }

   public boolean mouseReleased(double d, double e, int i) {
      return super.mouseReleased(d, e, i);
   }

   public boolean mouseDragged(double d, double e, int i, double f, double g) {
      return super.mouseDragged(d, e, i, f, g);
   }
}
