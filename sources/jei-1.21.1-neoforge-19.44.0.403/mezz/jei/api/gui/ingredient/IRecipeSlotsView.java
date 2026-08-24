package mezz.jei.api.gui.ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.jetbrains.annotations.Unmodifiable;

public interface IRecipeSlotsView {
   @Unmodifiable
   List<IRecipeSlotView> getSlotViews();

   default List<IRecipeSlotView> getSlotViews(RecipeIngredientRole role) {
      List<IRecipeSlotView> list = new ArrayList<>();

      for (IRecipeSlotView slotView : this.getSlotViews()) {
         if (slotView.getRole() == role) {
            list.add(slotView);
         }
      }

      return list;
   }

   default Optional<IRecipeSlotView> findSlotByName(String slotName) {
      return this.getSlotViews().stream().filter(slot -> slot.getSlotName().map(slotName::equals).orElse(false)).findFirst();
   }
}
