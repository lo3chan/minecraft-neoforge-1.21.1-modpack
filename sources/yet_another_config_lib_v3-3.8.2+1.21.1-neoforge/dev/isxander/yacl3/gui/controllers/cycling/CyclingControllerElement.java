package dev.isxander.yacl3.gui.controllers.cycling;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.gui.utils.KeyUtils;
import net.minecraft.client.gui.GuiGraphics;

public class CyclingControllerElement extends ControllerWidget<ICyclingController<?>> {
   public CyclingControllerElement(ICyclingController<?> control, YACLScreen screen, Dimension<Integer> dim) {
      super(control, screen, dim);
   }

   @Override
   protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      super.drawValueText(graphics, mouseX, mouseY, delta);
      if (this.hovered) {
      }
   }

   public void cycleValue(int increment) {
      int targetIdx = this.control.getPendingValue() + increment;
      if (targetIdx >= this.control.getCycleLength()) {
         targetIdx -= this.control.getCycleLength();
      } else if (targetIdx < 0) {
         targetIdx += this.control.getCycleLength();
      }

      this.control.setPendingValue(targetIdx);
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int button) {
      if (this.isMouseOver(mouseX, mouseY) && (button == 0 || button == 1) && this.isAvailable()) {
         this.playDownSound();
         this.cycleValue(button != 1 && !KeyUtils.hasShiftDown() && !KeyUtils.hasControlDown() ? 1 : -1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
      if (!this.focused) {
         return false;
      } else {
         switch (keyCode) {
            case 32:
            case 257:
            case 335:
               this.cycleValue(!KeyUtils.hasControlDown(modifiers) && !KeyUtils.hasShiftDown(modifiers) ? 1 : -1);
               break;
            case 262:
               this.cycleValue(1);
               break;
            case 263:
               this.cycleValue(-1);
               break;
            default:
               return false;
         }

         return true;
      }
   }

   @Override
   protected int getHoveredControlWidth() {
      return this.getUnhoveredControlWidth();
   }
}
