package mezz.jei.gui.input.focus;

import mezz.jei.common.platform.IPlatformScreenHelper;
import mezz.jei.common.platform.Services;
import net.minecraft.client.gui.components.EditBox;

public class EditBoxFocusHandler implements IFocusHandler {
   private final EditBox editBox;
   private final boolean canLoseFocus;
   private boolean wasFocused;

   public EditBoxFocusHandler(EditBox editBox) {
      this.editBox = editBox;
      IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
      this.canLoseFocus = screenHelper.canLoseFocus(this.editBox);
   }

   @Override
   public void unFocus() {
      boolean focused = this.editBox.isFocused();
      if (focused) {
         if (!this.canLoseFocus) {
            this.editBox.setCanLoseFocus(true);
         }

         this.editBox.setFocused(false);
      }

      this.wasFocused = focused;
   }

   @Override
   public void focus() {
      if (this.wasFocused) {
         if (!this.editBox.isFocused()) {
            this.editBox.setFocused(true);
         }

         if (!this.canLoseFocus) {
            this.editBox.setCanLoseFocus(false);
         }

         this.wasFocused = false;
      }
   }
}
