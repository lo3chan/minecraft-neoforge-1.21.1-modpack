package com.teamresourceful.resourcefulconfig.client.components.options.misc.draggable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulconfig.client.components.base.ListWidget;
import com.teamresourceful.resourcefulconfig.client.utils.KeyCodeHelper;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2d;

public class DraggableList<T> extends ListWidget {
   private final Vector2d draggingOffset = new Vector2d();
   private int draggingIndex = -1;
   private Consumer<List<T>> onUpdate = value -> {};
   private boolean canDelete = true;

   public DraggableList(int x, int y, int width, int height) {
      super(x, y, width, height);
   }

   public void setOnUpdate(Consumer<List<T>> onUpdate) {
      this.onUpdate = onUpdate;
   }

   public void setCanDelete(boolean canDelete) {
      this.canDelete = canDelete;
   }

   public boolean canDelete() {
      return this.canDelete;
   }

   public void addAll(List<T> values) {
      this.items.clear();

      for (T value : values) {
         AtomicReference<DraggableItem<T>> item = new AtomicReference<>();
         item.set(new DraggableItem<>(this.width, this, value, () -> {
            this.items.remove(item.get());
            this.onUpdate.accept(this.items.stream().map(i -> (DraggableItem)i).map(DraggableItem::value).toList());
            this.updateScrollBar();
         }));
         this.items.add(item.get());
      }

      this.updateLastHeight();
   }

   @Deprecated
   @Override
   public void add(ListWidget.Item item) {
      throw new UnsupportedOperationException("Use addAll(T value) instead.");
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);
      if (!this.isMouseOver(mouseX, mouseY) && this.draggingIndex != -1 && !KeyCodeHelper.isMouseKeyPressed(0)) {
         this.draggingIndex = -1;
      }

      graphics.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
      this.renderPossiblePositionLine(graphics, mouseX, mouseY);
      graphics.disableScissor();
      this.renderDraggedItem(graphics, mouseX, mouseY);
   }

   private void renderPossiblePositionLine(GuiGraphics graphics, int mouseX, int mouseY) {
      if (this.draggingIndex != -1) {
         int hoveredIndex = this.getItemOver(mouseX, mouseY);
         if (hoveredIndex != this.draggingIndex && hoveredIndex != -1) {
            ListWidget.Item hoveredItem = this.items.get(hoveredIndex);
            boolean isAbove = hoveredIndex < this.draggingIndex;
            int y = hoveredItem.getY() + (isAbove ? -1 : hoveredItem.getHeight() - 1);
            graphics.fill(hoveredItem.getX(), y, hoveredItem.getX() + hoveredItem.getWidth(), y + 2, -11141291);
         }
      }
   }

   private void renderDraggedItem(GuiGraphics graphics, int mouseX, int mouseY) {
      if (this.draggingIndex != -1) {
         ListWidget.Item item = this.items.get(this.draggingIndex);
         if (item instanceof DraggableItem<?> draggableItem) {
            PoseStack stack = graphics.pose();
            stack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.75F);
            stack.translate(0.0F, 0.0F, 300.0F);
            int x = (int)(mouseX - this.draggingOffset.x);
            int y = (int)(mouseY - this.draggingOffset.y);
            draggableItem.render(graphics, x, y, mouseX, mouseY, true, false, false);
            RenderSystem.disableBlend();
            stack.popPose();
         }
      }
   }

   public int getItemOver(double mouseX, double mouseY) {
      for (int i = 0; i < this.items.size(); i++) {
         if (this.items.get(i).isMouseOver(mouseX, mouseY)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (super.mouseClicked(mouseX, mouseY, button)) {
         return true;
      } else {
         if (button == 0) {
            this.draggingIndex = this.getItemOver(mouseX, mouseY);
            if (this.draggingIndex != -1) {
               ListWidget.Item item = this.items.get(this.draggingIndex);
               this.draggingOffset.set(mouseX - item.getX(), mouseY - item.getY());
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0 && this.draggingIndex != -1) {
         int newIndex = this.getItemOver(mouseX, mouseY);
         if (newIndex != -1 && newIndex != this.draggingIndex) {
            this.items.add(newIndex, this.items.remove(this.draggingIndex));
            this.onUpdate.accept(this.items.stream().map(item -> (DraggableItem)item).map(DraggableItem::value).toList());
         }

         this.draggingIndex = -1;
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean isDraggingItem() {
      return this.draggingIndex != -1;
   }

   public ListWidget.Item getDraggingItem() {
      return this.draggingIndex != -1 ? this.items.get(this.draggingIndex) : null;
   }
}
