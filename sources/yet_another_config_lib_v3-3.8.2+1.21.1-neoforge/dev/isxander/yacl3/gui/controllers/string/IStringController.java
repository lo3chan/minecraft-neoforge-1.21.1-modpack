package dev.isxander.yacl3.gui.controllers.string;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;

public interface IStringController<T> extends Controller<T> {
   String getString();

   void setFromString(String var1);

   @Override
   default Component formatValue() {
      return Component.literal(this.getString());
   }

   default boolean isInputValid(String input) {
      return true;
   }

   @Override
   default AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new StringControllerElement(this, screen, widgetDimension, true);
   }
}
