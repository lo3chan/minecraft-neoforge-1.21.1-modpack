package mezz.jei.gui.overlay.ingredients;

import java.util.List;
import java.util.stream.Stream;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.overlay.elements.IElement;

public interface IIngredientGrid extends IRecipeFocusSource {
   boolean isMouseOver(double var1, double var3);

   int size();

   int getColumnCount();

   int getRowCount();

   void set(int var1, List<IElement<?>> var2);

   default void set(int firstItemIndex, int smoothScrollRowPixelOffset, List<IElement<?>> ingredientList) {
      this.set(firstItemIndex, ingredientList);
   }

   Stream<IElement<?>> getVisibleElements();

   void tick();
}
