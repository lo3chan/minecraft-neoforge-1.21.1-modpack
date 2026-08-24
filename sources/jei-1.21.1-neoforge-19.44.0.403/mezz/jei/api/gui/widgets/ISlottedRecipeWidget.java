package mezz.jei.api.gui.widgets;

import java.util.Optional;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;

public interface ISlottedRecipeWidget extends IRecipeWidget {
   Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double var1, double var3);
}
