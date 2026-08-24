package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;

public class TConfigEntryCustomButton extends TConfigEntry {
   private final Button button;

   public TConfigEntryCustomButton(@Translatable String text, @Translatable String tooltip, OnPress action) {
      super(text, tooltip);
      this.button = Button.builder(this.getText(), action).bounds(0, 0, 0, 0).tooltip(this.getTooltip()).build();
   }

   public TConfigEntryCustomButton(@Translatable String text, OnPress button) {
      this(text, null, button);
   }

   @Override
   public AbstractWidget getWidget(int x, int y, int width, int height) {
      this.button.setRectangle(width, height, x, y);
      return this.button;
   }

   @Override
   boolean saveValuesToConfig() {
      return false;
   }

   @Override
   void setValuesToDefault() {
   }

   @Override
   void resetValuesToInitial() {
   }

   @Override
   boolean hasChangedFromInitial() {
      return false;
   }
}
