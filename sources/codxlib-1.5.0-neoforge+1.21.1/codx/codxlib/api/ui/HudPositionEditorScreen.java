package codx.codxlib.api.ui;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudPositionEditorScreen extends CodxConfigScreen {
   private final int elementWidth;
   private final int elementHeight;
   private final HudPosition position;
   private final HudPositionEditorScreen.Renderer renderer;
   private final Consumer<HudPosition> onSave;
   private int curX;
   private int curY;
   private boolean dragging;
   private int grabDX;
   private int grabDY;

   public HudPositionEditorScreen(
      Screen parent,
      Component title,
      int elementWidth,
      int elementHeight,
      HudPosition position,
      HudPositionEditorScreen.Renderer renderer,
      Consumer<HudPosition> onSave
   ) {
      super(parent, title, Component.literal("Drag the element — it snaps to the nearest anchor"));
      this.elementWidth = elementWidth;
      this.elementHeight = elementHeight;
      this.position = position;
      this.renderer = renderer;
      this.onSave = onSave;
   }

   @Override
   protected void addContents() {
      this.curX = clamp(this.position.x(this.width, this.elementWidth), 0, Math.max(0, this.width - this.elementWidth));
      this.curY = clamp(this.position.y(this.height, this.elementHeight), 0, Math.max(0, this.height - this.elementHeight));
   }

   @Override
   protected void drawExtras(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      graphics.fill(this.curX - 2, this.curY - 2, this.curX + this.elementWidth + 2, this.curY + this.elementHeight + 2, this.dragging ? 1090519039 : 419430399);
      graphics.renderOutline(this.curX - 2, this.curY - 2, this.elementWidth + 4, this.elementHeight + 4, this.accent());
      this.renderer.render(graphics, this.curX, this.curY);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return button == 0 && this.grab(mouseX, mouseY) ? true : super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      return this.drag(mouseX, mouseY) ? true : super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return button == 0 && this.drop() ? true : super.mouseReleased(mouseX, mouseY, button);
   }

   private boolean grab(double mouseX, double mouseY) {
      if (!this.inElement(mouseX, mouseY)) {
         return false;
      } else {
         this.dragging = true;
         this.grabDX = (int)Math.round(mouseX) - this.curX;
         this.grabDY = (int)Math.round(mouseY) - this.curY;
         return true;
      }
   }

   private boolean drag(double mouseX, double mouseY) {
      if (!this.dragging) {
         return false;
      } else {
         this.curX = clamp((int)Math.round(mouseX) - this.grabDX, 0, this.width - this.elementWidth);
         this.curY = clamp((int)Math.round(mouseY) - this.grabDY, 0, this.height - this.elementHeight);
         return true;
      }
   }

   private boolean drop() {
      if (!this.dragging) {
         return false;
      } else {
         this.dragging = false;
         return true;
      }
   }

   @Override
   protected void onSave() {
      HudAnchor anchor = this.nearestAnchor(this.curX + this.elementWidth / 2, this.curY + this.elementHeight / 2);
      this.position.setFromTopLeft(this.curX, this.curY, this.width, this.height, this.elementWidth, this.elementHeight, anchor);
      if (this.onSave != null) {
         this.onSave.accept(this.position);
      }
   }

   private boolean inElement(double mouseX, double mouseY) {
      return mouseX >= this.curX && mouseX < this.curX + this.elementWidth && mouseY >= this.curY && mouseY < this.curY + this.elementHeight;
   }

   private HudAnchor nearestAnchor(int centerX, int centerY) {
      HudAnchor best = HudAnchor.TOP_LEFT;
      long bestDistance = 9223372036854775807L;

      for (HudAnchor anchor : HudAnchor.values()) {
         int ax = Math.round(this.width * anchor.fx);
         int ay = Math.round(this.height * anchor.fy);
         long distance = (long)(ax - centerX) * (ax - centerX) + (long)(ay - centerY) * (ay - centerY);
         if (distance < bestDistance) {
            bestDistance = distance;
            best = anchor;
         }
      }

      return best;
   }

   private static int clamp(int value, int lo, int hi) {
      return Math.max(lo, Math.min(hi, value));
   }

   @FunctionalInterface
   public interface Renderer {
      void render(GuiGraphics var1, int var2, int var3);
   }
}
