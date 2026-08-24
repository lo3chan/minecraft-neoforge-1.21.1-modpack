package dev.isxander.yacl3.gui.controllers.cycling;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;

public interface ICyclingController<T> extends Controller<T> {
   void setPendingValue(int var1);

   int getPendingValue();

   int getCycleLength();

   @Override
   default AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new CyclingControllerElement(this, screen, widgetDimension);
   }
}
