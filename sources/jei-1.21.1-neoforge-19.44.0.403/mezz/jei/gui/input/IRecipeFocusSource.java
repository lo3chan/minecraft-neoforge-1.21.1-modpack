package mezz.jei.gui.input;

import java.util.stream.Stream;

public interface IRecipeFocusSource {
   Stream<IClickableIngredientInternal<?>> getIngredientUnderMouse(double var1, double var3);

   Stream<IDraggableIngredientInternal<?>> getDraggableIngredientUnderMouse(double var1, double var3);
}
