package mezz.jei.api.gui.widgets;

import net.minecraft.client.gui.navigation.ScreenRectangle;

@Deprecated(
   since = "19.19.3",
   forRemoval = true
)
public interface IScrollGridWidgetFactory<R> extends ISlottedWidgetFactory<R> {
   void setPosition(int var1, int var2);

   ScreenRectangle getArea();
}
