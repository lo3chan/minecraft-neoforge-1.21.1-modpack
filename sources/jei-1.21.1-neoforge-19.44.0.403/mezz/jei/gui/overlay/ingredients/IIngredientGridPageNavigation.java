package mezz.jei.gui.overlay.ingredients;

import mezz.jei.gui.overlay.elements.IElement;
import org.jetbrains.annotations.Nullable;

public interface IIngredientGridPageNavigation {
   @Nullable
   IElement<?> getPageAnchorElement();

   void updateLayoutKeepingPageAnchorVisible(@Nullable IElement<?> var1);

   void updateLayoutToFirstPage();
}
