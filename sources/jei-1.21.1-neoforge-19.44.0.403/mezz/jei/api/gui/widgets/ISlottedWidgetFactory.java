package mezz.jei.api.gui.widgets;

import java.util.List;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;

@Deprecated(
   since = "19.19.3",
   forRemoval = true
)
@FunctionalInterface
public interface ISlottedWidgetFactory<R> {
   @Deprecated(
      since = "19.19.3",
      forRemoval = true
   )
   void createWidgetForSlots(IRecipeExtrasBuilder var1, R var2, List<IRecipeSlotDrawable> var3);
}
