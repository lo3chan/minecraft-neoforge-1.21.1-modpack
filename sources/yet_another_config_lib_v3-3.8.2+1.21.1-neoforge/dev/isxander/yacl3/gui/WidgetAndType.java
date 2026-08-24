package dev.isxander.yacl3.gui;

public interface WidgetAndType<T> {
   T getType();

   net.minecraft.client.gui.components.AbstractWidget getWidget();

   static <T extends net.minecraft.client.gui.components.AbstractWidget> WidgetAndType<T> ofWidget(final T widget) {
      return new WidgetAndType<T>() {
         public T getType() {
            return widget;
         }

         @Override
         public net.minecraft.client.gui.components.AbstractWidget getWidget() {
            return widget;
         }
      };
   }
}
