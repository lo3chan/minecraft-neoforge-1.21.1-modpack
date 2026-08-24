package dev.isxander.yacl3.gui.controllers.slider;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;

public interface ISliderController<T extends Number> extends Controller<T> {
   double min();

   double max();

   double interval();

   default double range() {
      return this.max() - this.min();
   }

   void setPendingValue(double var1);

   double pendingValue();

   @Override
   default AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new SliderControllerElement(this, screen, widgetDimension, this.min(), this.max(), this.interval());
   }
}
