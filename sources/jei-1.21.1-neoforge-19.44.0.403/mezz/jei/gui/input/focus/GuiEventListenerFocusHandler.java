package mezz.jei.gui.input.focus;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class GuiEventListenerFocusHandler implements IFocusHandler {
   private final GuiEventListener guiEventListener;
   private boolean unfocused;

   public static IFocusHandler create(GuiEventListener guiEventListener) {
      return (IFocusHandler)(guiEventListener instanceof EditBox editBox
         ? new EditBoxFocusHandler(editBox)
         : new GuiEventListenerFocusHandler(guiEventListener));
   }

   private GuiEventListenerFocusHandler(GuiEventListener guiEventListener) {
      this.guiEventListener = guiEventListener;
   }

   @Override
   public void unFocus() {
      this.unfocused = this.guiEventListener.isFocused();
      if (this.unfocused) {
         this.guiEventListener.setFocused(false);
      }
   }

   @Override
   public void focus() {
      if (this.unfocused && !this.guiEventListener.isFocused()) {
         this.guiEventListener.setFocused(true);
      }

      this.unfocused = false;
   }
}
