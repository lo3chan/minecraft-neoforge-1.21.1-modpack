package mezz.jei.api.gui.ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.jetbrains.annotations.Unmodifiable;

public interface IRecipeSlotDrawablesView {
   @Unmodifiable
   List<IRecipeSlotDrawable> getSlots();

   default List<IRecipeSlotDrawable> getSlots(RecipeIngredientRole role) {
      List<IRecipeSlotDrawable> list = new ArrayList<>();

      for (IRecipeSlotDrawable slotView : this.getSlots()) {
         if (slotView.getRole() == role) {
            list.add(slotView);
         }
      }

      return list;
   }

   default Optional<IRecipeSlotDrawable> findSlotByName(String slotName) {
      return this.getSlots().stream().filter(slot -> slot.getSlotName().map(slotName::equals).orElse(false)).findFirst();
   }
}
