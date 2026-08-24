package mezz.jei.api.gui.widgets;

import mezz.jei.api.gui.placement.IPlaceable;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface IScrollGridWidget extends ISlottedRecipeWidget, IPlaceable<IScrollGridWidget> {
   ScreenRectangle getScreenRectangle();
}
