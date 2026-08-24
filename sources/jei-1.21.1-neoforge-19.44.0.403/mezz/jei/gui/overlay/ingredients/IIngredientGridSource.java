package mezz.jei.gui.overlay.ingredients;

import java.util.List;
import mezz.jei.gui.overlay.elements.IElement;
import org.jetbrains.annotations.Unmodifiable;

public interface IIngredientGridSource {
   @Unmodifiable
   List<IElement<?>> getElements();

   void addSourceListChangedListener(IIngredientGridSource.SourceListChangedListener var1);

   public interface SourceListChangedListener {
      void onSourceListChanged();
   }
}
