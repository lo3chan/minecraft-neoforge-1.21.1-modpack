package net.irisshaders.iris.gui.option;

import java.util.function.Consumer;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.OptionInstance.CaptionBasedToString;
import net.minecraft.client.OptionInstance.TooltipSupplier;
import net.minecraft.client.OptionInstance.ValueSet;
import net.minecraft.client.gui.components.AbstractWidget;

public class ShadowDistanceOption<T> extends OptionInstance<T> {
   public ShadowDistanceOption(String string, TooltipSupplier<T> arg, CaptionBasedToString<T> arg2, ValueSet<T> arg3, T object, Consumer<T> consumer) {
      super(string, arg, arg2, arg3, object, consumer);
   }

   public AbstractWidget createButton(Options options, int x, int y, int width) {
      AbstractWidget widget = super.createButton(options, x, y, width);
      widget.active = IrisVideoSettings.isShadowDistanceSliderEnabled();
      return widget;
   }
}
